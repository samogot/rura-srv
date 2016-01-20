package ru.ruranobe.wicket.components.modals;

import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.misc.Token;
import ru.ruranobe.misc.smtp.Email;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.User;
import ru.ruranobe.mybatis.mappers.UsersMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.LoginSession;

public class ModalChangeEmail extends Panel
{
    public ModalChangeEmail(final String id)
    {
        super(id, Model.of("false"));
        add(new FeedbackPanel("feedback").setFilter(new ContainerFeedbackMessageFilter(this)));

        StatelessForm<EmailFormModelObject> emailForm = new StatelessForm<>("emailForm",
                new CompoundPropertyModel<>(new EmailFormModelObject()));
        emailForm.add(new PasswordTextField("currentPassword"));
        emailForm.add(new TextField("newEmail"));
        emailForm.add(new Button("updateEmail")
        {
            private void onFail(String message)
            {
                error(message);
                ModalChangeEmail.this.setDefaultModelObject("true");
            }

            private void onSuccess(String message)
            {
                info(message);
                ModalChangeEmail.this.setDefaultModelObject("false");
            }

            @Override
            public void onSubmit()
            {
                EmailFormModelObject emailFormModelObject = (EmailFormModelObject) getForm().getModelObject();
                String password = emailFormModelObject.getCurrentPassword();
                String email = emailFormModelObject.getNewEmail();

                if (LoginSession.get().validatePassword(password))
                {
                    onFail("Введенный пароль не верен.");
                }
                else if (!Strings.isEmpty(email) && Email.isEmailSyntaxInvalid(email))
                {
                    onFail("Указан неверный адрес электронной почты.");
                }
                else if (email != null && email.length() > 255)
                {
                    onFail("Длина электронного адреса не должна превышать 255 символов.");
                }
                else
                {
                    try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
                    {
                        UsersMapper usersMapperCacheable = CachingFacade.getCacheableMapper(session, UsersMapper.class);
                        if (usersMapperCacheable.getUserByEmail(email) != null)
                        {
                            onFail("Пользователь с таким электронным адресом уже зарегистрирован в системе.");
                        }
                        else
                        {
                            User user = new User(LoginSession.get().getUser());

                            Token token = Token.valueOf(user.getUserId(), Email.ETERNITY_EXPIRATION_TIME);
                            user.setEmailToken(token.getTokenValue());
                            user.setEmailTokenDate(token.getTokenExpirationDate());
                            user.setEmailActivated(false);
                            boolean committingNeeded = true;
                            try
                            {
                                Email.sendEmailActivationMessage(user.getEmail(), user.getEmailToken());
                                usersMapperCacheable.updateUser(user);
                                committingNeeded = false;
                                onSuccess("Электронный адрес был успешно изменен");
                            }
                            catch (Exception ex)
                            {
                                onFail("Отправка сообщения на указанный электронный адрес не удалась. Свяжитесь, пожалуйста, с администрацией сайта.");
                                session.rollback();
                            }
                            if (committingNeeded)
                            {
                                usersMapperCacheable.updateUser(user);
                                session.commit();
                            }
                            LoginSession.get().authenticate(user.getUsername(), user.getPass());
                        }
                    }
                }
            }
        });
        add(emailForm.setOutputMarkupId(true).setMarkupId("settingsEmail"));
    }

    @Override
    protected void onComponentTag(ComponentTag tag)
    {
        super.onComponentTag(tag);
        tag.put("data-show", this.getDefaultModelObjectAsString());
    }

    private class EmailFormModelObject
    {
        private String currentPassword;
        private String newEmail;

        public EmailFormModelObject() {
        }

        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewEmail() {
            return newEmail;
        }

        public void setNewEmail(String newEmail) {
            this.newEmail = newEmail;
        }
    }

    private static final long serialVersionUID = 1L;
}
