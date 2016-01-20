package ru.ruranobe.wicket.components.modals;

import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import ru.ruranobe.misc.Token;
import ru.ruranobe.misc.smtp.Email;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.User;
import ru.ruranobe.mybatis.mappers.UsersMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.LoginSession;

import javax.mail.MessagingException;

public class ModalChangePassword extends Panel 
{
    public ModalChangePassword(final String id)
    {
        super(id, Model.of("false"));
        add(new FeedbackPanel("feedback").setFilter(new ContainerFeedbackMessageFilter(this)));
        add(new StatelessForm("changePassword")
        {
            private void onFail(String message)
            {
                error(message);
                ModalChangePassword.this.setDefaultModelObject("true");
            }
            
            private void onSuccess(String message)
            {
                info(message);
                ModalChangePassword.this.setDefaultModelObject("false");
            }
            
            @Override
            public final void onSubmit() 
            {
                try (SqlSession session = MybatisUtil.getSessionFactory().openSession()) 
                {
                    User user = LoginSession.get().getUser();
                    if (!user.isEmailActivated()) 
                    {
                        onFail("Электронный адрес пользователя не был подтвержден.");
                    } 
                    else if (user.getPassRecoveryToken() != null
                             && user.getPassRecoveryTokenDate().getTime() > System.currentTimeMillis()) 
                    {
                        onFail("На указанный электронной адрес уже было отправлено письмо.");
                    } 
                    else 
                    {
                        Token token = Token.valueOf(user.getUserId(), EXPIRATION_TIME_6_HOURS);
                        user.setPassRecoveryToken(token.getTokenValue());
                        user.setPassRecoveryTokenDate(token.getTokenExpirationDate());
                        CachingFacade.getCacheableMapper(session, UsersMapper.class).updateUser(user);
                        try 
                        {
                            Email.sendPasswordRecoveryMessage(user.getEmail(), user.getPassRecoveryToken());
                            session.commit();
                            onSuccess("");
                        } 
                        catch (MessagingException ex) 
                        {
                            onFail("Отправка сообщения на указанный электронный адрес не удалась. Свяжитесь, пожалуйста, с администрацией сайта.");
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onComponentTag(ComponentTag tag)
    {
        super.onComponentTag(tag);
        tag.put("data-show", this.getDefaultModelObjectAsString());
    }

    private static final long EXPIRATION_TIME_6_HOURS = 21600000L;
    private static final long serialVersionUID = 1L;
}
