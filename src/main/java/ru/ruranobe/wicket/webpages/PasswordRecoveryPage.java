package ru.ruranobe.wicket.webpages;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.User;
import ru.ruranobe.mybatis.mappers.UsersMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.components.PasswordRecoveryPanel;

public class PasswordRecoveryPage extends WebPage
{
    public PasswordRecoveryPage(final PageParameters parameters)
    {
        if (parameters.getNamedKeys().size() != 1)
        {
            throw RuranobeUtils.getRedirectTo404Exception(this);
        }

        String passRecoveryToken = parameters.get("token").toString();
        if (Strings.isEmpty(passRecoveryToken))
        {
            throw RuranobeUtils.getRedirectTo404Exception(this);
        }

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        try (SqlSession session = sessionFactory.openSession())
        {
            UsersMapper usersMapper = CachingFacade.getCacheableMapper(session, UsersMapper.class);
            User user = usersMapper.getUserByPassRecoveryToken(passRecoveryToken);
            if (user == null)
            {
                throw RuranobeUtils.getRedirectTo404Exception(this);
            }
            add(new PasswordRecoveryPanel("passwordRecoveryPanel", user));
        }


    }
}
