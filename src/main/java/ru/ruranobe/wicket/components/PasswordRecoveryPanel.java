package ru.ruranobe.wicket.components;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.misc.MD5;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.User;
import ru.ruranobe.mybatis.mappers.UsersMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;

public class PasswordRecoveryPanel extends Panel
{

    private static final String PASSWORD_RECOVERY_FORM = "passwordRecoveryForm";
    private static final long serialVersionUID = 1L;
    private final User user;
    private String password;
    private String confirmPassword;

    public PasswordRecoveryPanel(String id, User user)
    {
        super(id);
        add(new FeedbackPanel("feedback"));
        add(new PasswordRecoveryPanel.PasswordRecoveryForm(PASSWORD_RECOVERY_FORM));
        this.user = user;
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

    public final class PasswordRecoveryForm extends StatelessForm<PasswordRecoveryPanel>
    {

        private static final long serialVersionUID = 1L;

        public PasswordRecoveryForm(final String id)
        {
            super(id);
            setModel(new CompoundPropertyModel<>(PasswordRecoveryPanel.this));
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
            else if (RuranobeUtils.isPasswordSyntaxInvalid(password))
            {
                error("Пароль может состоять только из больших и маленьких латинских букв, а также цифр.");
            }
            else if (System.currentTimeMillis() > user.getPassRecoveryTokenDate().getTime())
            {
                error("С момента отправки сообщения прошло слишком много времени. Отправьте сообщение о смене пароля еще раз.");
            }
            else
            {
                SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();

                try (SqlSession session = sessionFactory.openSession())
                {
                    UsersMapper usersMapper = CachingFacade.getCacheableMapper(session, UsersMapper.class);

                    user.setPass(MD5.crypt(password));
                    usersMapper.updateUser(user);
                    session.commit();
                    info("Пароль был успешно изменен.");
                }
            }
        }
    }
}
