package ru.ruranobe.wicket.components;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.misc.Email;
import ru.ruranobe.misc.Token;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.UsersMapper;
import ru.ruranobe.mybatis.tables.User;

import javax.mail.MessagingException;

public class EmailPasswordRecoveryPanel extends Panel
{

    private static final String EMAIL_PASSWORD_RECOVERY_FORM = "emailPasswordRecoveryForm";
    private static final String EMAIL_PASSWORD_RECOVERY_SUBJECT = "Восстановление пароля";
    private static final String EMAIL_PASSWORD_RECOVERY_TEXT = "Для восстановления пароля проследуйте по ссылке http://ruranobe.ru/user/recover/pass?token=%s";
    private static final long EXPIRATION_TIME_6_HOURS = 21600000L;
    private static final long serialVersionUID = 1L;
    private String email;

    public EmailPasswordRecoveryPanel(String id)
    {
        super(id);
        add(new FeedbackPanel("feedback"));
        add(new EmailPasswordRecoveryForm(EMAIL_PASSWORD_RECOVERY_FORM));
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public final class EmailPasswordRecoveryForm extends StatelessForm<EmailPasswordRecoveryPanel>
    {

        private static final long serialVersionUID = 1L;

        public EmailPasswordRecoveryForm(String id)
        {
            super(id);
            setModel(new CompoundPropertyModel<EmailPasswordRecoveryPanel>(EmailPasswordRecoveryPanel.this));
            add(new TextField<String>("email"));
        }

        @Override
        public final void onSubmit()
        {
            if (Strings.isEmpty(email))
            {
                error("Укажите, пожалуйста, электронный адрес.");
            }
            else if (!Email.isEmailSyntaxValid(email))
            {
                error("Указан неверный адрес электронной почты.");
            }
            else if (email.length() > 255)
            {
                error("Длина электронного адреса не должна превышать 255 символов.");
            }
            else
            {
                SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
                SqlSession session = sessionFactory.openSession();

                try
                {
                    UsersMapper usersMapper = session.getMapper(UsersMapper.class);
                    User user = usersMapper.getUserByEmail(email);
                    if (user == null)
                    {
                        error("Пользователь с таким электронным адресом не зарегистрирован в системе.");
                    }
                    else if (!user.isEmailActivated())
                    {
                        error("Электронный адрес пользователя не был подтвержден.");
                    }
                    else if (user.getPassRecoveryToken() != null
                             && user.getPassRecoveryTokenDate().getTime() > System.currentTimeMillis())
                    {
                        error("На указанный электронной адрес уже было отправлено письмо.");
                    }
                    else
                    {
                        Token token = Token.valueOf(user.getUserId(), EXPIRATION_TIME_6_HOURS);
                        user.setPassRecoveryToken(token.getTokenValue());
                        user.setPassRecoveryTokenDate(token.getTokenExpirationDate());
                        usersMapper.updateUser(user);
                        try
                        {
                            Email.sendEmail(user.getEmail(), EMAIL_PASSWORD_RECOVERY_SUBJECT,
                                            String.format(EMAIL_PASSWORD_RECOVERY_TEXT, user.getPassRecoveryToken()));
                            session.commit();
                        }
                        catch (MessagingException ex)
                        {
                            error("Отправка сообщения на указанный электронный адрес не удалась. Свяжитесь, пожалуйста, с администрацией сайта.");
                        }
                    }
                }
                finally
                {
                    session.close();
                }
            }
        }
    }
}
