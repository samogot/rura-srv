package ru.ruranobe.wicket.components;

import java.sql.Date;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
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

public class RuranobeRegistrationPanel extends Panel 
{
    public RuranobeRegistrationPanel(final String id)
    {
        super(id);
        add(new FeedbackPanel("feedback"));
        add(new RuranobeRegistrationForm(REGISTRATION_FORM));
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
    
    public final class RuranobeRegistrationForm extends StatelessForm<RuranobeRegistrationPanel>
    {

        public RuranobeRegistrationForm(final String id)
        {
            super(id);

            setModel(new CompoundPropertyModel<RuranobeRegistrationPanel>(RuranobeRegistrationPanel.this));

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
                error("Длина имени учетной записи не должна превышать 64 символов.");
            }
            else if (Strings.isEmpty(password))      
            {
                error("Введен пусто пароль учетной записи.");
            }
            else if (password.length() > 31)
            {
                error("Длина пароля не должна превышать 32 символа.");
            }
            else if (!password.equals(confirmPassword))
            {
                error("Введенные пароли не совпадают.");
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
                if (usersMapper.getUserByUsername(username) != null)
                {
                    error("Пользователь с такой учетной записью уже зарегистрирован в системе.");
                }
                else if (usersMapper.getUserByUsername(email) != null)
                {
                    error("Пользователь с таким электронным адресом уже зарегистрирован в системе.");
                }
                else
                {
                    User user = new User();
                    user.setUsername(username);
                    user.setPass(password);
                    user.setRealname(realname);
                    user.setEmail(email);
                    if (!Strings.isEmpty(email))
                    {
                        Token token = Token.newInstance();
                        user.setEmailToken(token.getTokenValue());
                        user.setEmailTokenDate(token.getTokenExpirationDate());
                        user.setIsEmailActivated(false);
                    }
                    user.setRegistrationDate(new Date(System.currentTimeMillis()));
                    user.setPassRecoveryToken(null);
                    user.setPassRecoveryTokenDate(null);
                    usersMapper.registerUser(user);
                }
            }
        }
            
        private static final long serialVersionUID = 1L;
    }
    
    private static final String REGISTRATION_FORM = "registrationForm";
    private String password;
    private String username;
    private String confirmPassword;
    private String email;
    private String realname;
    private static final long serialVersionUID = 1L;
}
