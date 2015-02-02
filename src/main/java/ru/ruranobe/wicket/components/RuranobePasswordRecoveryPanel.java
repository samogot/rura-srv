package ru.ruranobe.wicket.components;

import java.sql.Date;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.UsersMapper;
import ru.ruranobe.mybatis.tables.User;
import ru.ruranobe.wicket.Token;

public class RuranobePasswordRecoveryPanel extends Panel
{

    public RuranobePasswordRecoveryPanel(final String id)
    {
        super(id);
        add(new FeedbackPanel("feedback"));
        add(new RuranobePasswordRecoveryPanel.RuranobePasswordRecoveryForm(PASSWORD_RECOVERY_FORM));
    }
    
    public final class RuranobePasswordRecoveryForm extends StatelessForm<RuranobePasswordRecoveryPanel>
    {

        public RuranobePasswordRecoveryForm(final String id)
        {
            super(id);
            setModel(new CompoundPropertyModel<RuranobePasswordRecoveryPanel>(RuranobePasswordRecoveryPanel.this));
            add(new PasswordTextField("password"));
            add(new PasswordTextField("confirmPassword"));
        }

        @Override
        public final void onSubmit()
        {
            if (Strings.isEmpty(password))      
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
            else
            {
                SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
                SqlSession session = sessionFactory.openSession();

                UsersMapper usersMapper = session.getMapper(UsersMapper.class);
                User user = usersMapper.getUserByPassRecoveryToken(null);
                
                // TODO
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
    private static final String PASSWORD_RECOVERY_FORM = "passwordRecoveryForm";
    private static final long serialVersionUID = 1L;
}
