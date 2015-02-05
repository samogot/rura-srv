package ru.ruranobe.wicket.webpages;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.UsersMapper;
import ru.ruranobe.mybatis.tables.User;

public class ActivateEmail extends WebPage
{
    
    public ActivateEmail(final PageParameters parameters)
    {
        add(new FeedbackPanel("feedback"));
        
        if (parameters.getNamedKeys().size() != 1)
        {
            throw RuranobeUtils.REDIRECT_TO_404;
        }
        
        String emailToken = parameters.get("token").toString();
        if (Strings.isEmpty(emailToken))
        {
            throw RuranobeUtils.REDIRECT_TO_404;
        }
        
        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        try
        {
            UsersMapper usersMapper = session.getMapper(UsersMapper.class);
            User user = usersMapper.getUserByEmailToken(emailToken);
            if (user.getEmailTokenDate().getTime() > System.currentTimeMillis())
            {
                user.setEmailActivated(true);
                usersMapper.updateUser(user);
                session.commit();
            }
        }
        finally
        {
            session.close(); 
        }
        
        info("Электронный адрес успешно активирован.");
    }
}
