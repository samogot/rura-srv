package ru.ruranobe.wicket.components;

import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.mybatis.mappers.UsersMapper;
import ru.ruranobe.mybatis.tables.User;
import ru.ruranobe.misc.MD5;
import ru.ruranobe.misc.RuranobeUtils;

public class PasswordRecoveryPanel extends Panel
{

    public PasswordRecoveryPanel(String id, User user, UsersMapper usersMapper, SqlSession session)
    {
        super(id);
        add(new FeedbackPanel("feedback"));
        add(new PasswordRecoveryPanel.PasswordRecoveryForm(PASSWORD_RECOVERY_FORM));
        
        this.user = user;
        this.usersMapper = usersMapper;
        this.session = session;
    }
    
    public final class PasswordRecoveryForm extends StatelessForm<PasswordRecoveryPanel>
    {

        public PasswordRecoveryForm(final String id)
        {
            super(id);
            setModel(new CompoundPropertyModel<PasswordRecoveryPanel>(PasswordRecoveryPanel.this));
            add(new PasswordTextField("password"));
            add(new PasswordTextField("confirmPassword"));
        }

        @Override
        public final void onSubmit()
        {
            if (Strings.isEmpty(password))      
            {
                error("Введен пустой пароль учетной записи.");
            }
            else if (password.length() < 8 || password.length() > 31)
            {
                error("Длина пароля не должна превышать 31 символ или быть меньше 8 символов.");
            }
            else if (!password.equals(confirmPassword))
            {
                error("Введенные пароли не совпадают.");
            }
            else if (!RuranobeUtils.isPasswordSyntaxValid(password))
            {
                error("Пароль может состоять только из больших и маленьких латинских букв, а также цифр.");
            }
            else if (System.currentTimeMillis() > user.getPassRecoveryTokenDate().getTime())
            {
                error("С момента отправки сообщения прошло слишком много времени. Отправьте сообщение о смене пароля еще раз.");
            }
            else
            {
                try
                {
                    user.setPass(MD5.crypt(password));
                    usersMapper.updateUser(user);
                    session.commit();
                    info("Пароль был успешно изменен.");
                }
                finally
                {
                    session.close();
                }
            }
        }
            
        private static final long serialVersionUID = 1L;
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

    private String password;
    private String confirmPassword;
    private final User user;
    private final UsersMapper usersMapper;
    private final SqlSession session;
    private static final String PASSWORD_RECOVERY_FORM = "passwordRecoveryForm";
    private static final long serialVersionUID = 1L;
}
