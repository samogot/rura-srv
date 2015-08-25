package ru.ruranobe.wicket.webpages;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.misc.Email;
import ru.ruranobe.misc.MD5;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.misc.Token;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.UsersMapper;
import ru.ruranobe.mybatis.tables.User;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

import java.util.Date;

public class Register extends SidebarLayoutPage
{
    @Override
    protected void onInitialize()
    {
        add(new FeedbackPanel("feedback"));
        add(new RegistrationForm(REGISTRATION_FORM));
        super.onInitialize();
    }

    public final class RegistrationForm extends StatelessForm<Register>
    {
        public RegistrationForm(final String id)
        {
            super(id);

            setModel(new CompoundPropertyModel<Register>(Register.this));

            add(new TextField<String>("username"));
            add(new PasswordTextField("password"));
            add(new PasswordTextField("confirmPassword"));
            add(new TextField<String>("email"));
            add(new TextField<String>("realname"));
        }

        @Override
        public final void onSubmit()
        {
            if (Strings.isEmpty(username))
            {
                error("Введено пустое имя учетной записи.");
            }
            else if (username.length() > 63)
            {
                error("Длина имени учетной записи не должна превышать 63 символов.");
            }
            else if (Strings.isEmpty(password))
            {
                error("Введен пусто пароль учетной записи.");
            }
            else if (password.length() < 8 || password.length() > 31)
            {
                error("Длина пароля не должна быть меньше 8 символов или превышать 31 символ.");
            }
            else if (!password.equals(confirmPassword))
            {
                error("Введенные пароли не совпадают.");
            }
            else if (!RuranobeUtils.isPasswordSyntaxValid(password))
            {
                error("Пароль может состоять только из больших и маленьких латинских букв, а также цифр.");
            }
            else if (!Strings.isEmpty(email) && !Email.isEmailSyntaxValid(email))
            {
                error("Указан неверный адрес электронной почты.");
            }
            else if (email != null && email.length() > 255)
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
                    if (usersMapper.getUserByUsername(username) != null)
                    {
                        error("Пользователь с такой учетной записью уже зарегистрирован в системе.");
                    }
                    else if (usersMapper.getUserByEmail(email) != null)
                    {
                        error("Пользователь с таким электронным адресом уже зарегистрирован в системе.");
                    }
                    else
                    {
                        User user = new User();
                        user.setUsername(username);
                        user.setPass(MD5.crypt(password));
                        user.setRealname(realname);
                        user.setEmail(email);
                        user.setRegistrationDate(new Date(System.currentTimeMillis()));
                        user.setPassRecoveryToken(null);
                        user.setPassRecoveryTokenDate(null);
                        usersMapper.registerUser(user);

                        if (!Strings.isEmpty(email))
                        {
                            Token token = Token.valueOf(user.getUserId(), ETERNITY_EXPIRATION_TIME);
                            user.setEmailToken(token.getTokenValue());
                            user.setEmailTokenDate(token.getTokenExpirationDate());
                            user.setEmailActivated(false);
                            try
                            {
                                Email.sendEmail(user.getEmail(), ACTIVATE_EMAIL_SUBJECT,
                                        String.format(ACTIVATE_EMAIL_TEXT, user.getEmailToken()));
                                usersMapper.updateUser(user);
                            }
                            catch (Exception ex)
                            {
                                error("Отправка сообщения на указанный электронный адрес не удалась. Свяжитесь, пожалуйста, с администрацией сайта.");
                                session.rollback();
                            }
                        }
                        session.commit();
                    }
                }
                finally
                {
                    session.close();
                }
            }
        }

        private static final long serialVersionUID = 1L;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getConfirmPassword()
    {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword)
    {
        this.confirmPassword = confirmPassword;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getRealname()
    {
        return realname;
    }

    public void setRealname(String realname)
    {
        this.realname = realname;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    private String password;
    private String username;
    private String confirmPassword;
    private String email;
    private String realname;
    private static final String REGISTRATION_FORM = "registrationForm";
    private static final String ACTIVATE_EMAIL_SUBJECT = "Активация электронного адреса";
    private static final String ACTIVATE_EMAIL_TEXT = "Для активации электронного адреса в системе проследуйте по ссылке http://ruranobe.ru/user/email/activate?token=%s";
    private static final long ETERNITY_EXPIRATION_TIME = 31622400000000L;
    private static final long serialVersionUID = 1L;
}
