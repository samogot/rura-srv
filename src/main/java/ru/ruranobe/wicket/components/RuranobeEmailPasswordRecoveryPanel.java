package ru.ruranobe.wicket.components;

import javax.mail.MessagingException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.UsersMapper;
import ru.ruranobe.mybatis.tables.User;
import ru.ruranobe.wicket.Email;
import ru.ruranobe.wicket.Token;

public class RuranobeEmailPasswordRecoveryPanel extends Panel
{

    public RuranobeEmailPasswordRecoveryPanel(final String id)
    {
        super(id);
        add(new FeedbackPanel("feedback"));
        add(new RuranobeEmailPasswordRecoveryForm(EMAIL_PASSWORD_RECOVERY_FORM));
    }
    
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public final class RuranobeEmailPasswordRecoveryForm extends StatelessForm<RuranobeEmailPasswordRecoveryPanel>
    {

        public RuranobeEmailPasswordRecoveryForm(final String id)
        {
            super(id);
            setModel(new CompoundPropertyModel<RuranobeEmailPasswordRecoveryPanel>(RuranobeEmailPasswordRecoveryPanel.this));
            add(new TextField<String>("email"));
        }

        @Override
        public final void onSubmit()
        {
            if (Strings.isEmpty(email))
            {
                error("Укажите, пожалуйста, электронный адрес.");
            }
            else if (Email.validateEmailSyntax(email))
            {
                error("Указан неверный адрес электронной почты.");
            }
            else if (email.length() > 255)
            {
                error("Длина электронного адреса не должна превышать 256 символов.");
            }
            else
            {
                SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
                SqlSession session = sessionFactory.openSession();

                UsersMapper usersMapper = session.getMapper(UsersMapper.class);
                User user = usersMapper.getUserByEmail(email);
                if (user == null)
                {
                    error("Пользователь с таким электронным адресом не зарегистрирован в системе.");
                }
                else if (!user.isIsEmailActivated())
                {
                    error("Электронный адрес пользователя не был подтвержден.");
                }
                else
                { 
                    Token token = Token.newInstance();
                    user.setPassRecoveryToken(token.getTokenValue());
                    user.setPassRecoveryTokenDate(token.getTokenExpirationDate());
                    usersMapper.updateUser(user);
                    StringBuilder sb = new StringBuilder();
                    sb.append("http://ruranobe.ru/blabla");
                    sb.append(user.getEmailToken());
                    try
                    {
                        Email.sendEmail(user.getEmail(), EMAIL_PASSWORD_RECOVERY_SUBJECT,
                                String.format(EMAIL_PASSWORD_RECOVERY_TEXT, sb.toString()));
                    }
                    catch (MessagingException ex)
                    {
                        error("Отправка сообщения на указанный электронный адрес не удалась. Свяжитесь, пожалуйста, с администрацией сайта.");
                    }
                }
            }
        }
        
        private static final long serialVersionUID = 1L;
    }
    
    private String email;
    private static final String EMAIL_PASSWORD_RECOVERY_FORM = "emailPasswordRecoveryForm";
    private static final String EMAIL_PASSWORD_RECOVERY_SUBJECT = "Восстановление пароля";
    private static final String EMAIL_PASSWORD_RECOVERY_TEXT = "Для восстановления пароля проследуйте по ссылке %s";
    private static final long serialVersionUID = 1L;
}
