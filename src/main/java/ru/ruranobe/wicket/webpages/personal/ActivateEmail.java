package ru.ruranobe.wicket.webpages.personal;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.User;
import ru.ruranobe.mybatis.mappers.UsersMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;

public class ActivateEmail extends WebPage
{

    public ActivateEmail(final PageParameters parameters)
    {
        add(new FeedbackPanel("feedback"));

        if (parameters.getNamedKeys().size() != 1)
        {
            throw RuranobeUtils.getRedirectTo404Exception(this);
        }

        String emailToken = parameters.get("token").toString();
        if (Strings.isEmpty(emailToken))
        {
            throw RuranobeUtils.getRedirectTo404Exception(this);
        }

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        try (SqlSession session = sessionFactory.openSession())
        {
            UsersMapper usersMapper = CachingFacade.getCacheableMapper(session, UsersMapper.class);
            User user = usersMapper.getUserByEmailToken(emailToken);
            if (user.getEmailTokenDate().getTime() > System.currentTimeMillis())
            {
                user.setEmailActivated(true);
                usersMapper.updateUser(user);
                session.commit();
            }
        }

        info("Электронный адрес успешно активирован.");
    }
}
