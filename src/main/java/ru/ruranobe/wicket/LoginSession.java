package ru.ruranobe.wicket;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.UsersMapper;
import ru.ruranobe.mybatis.tables.User;

public class LoginSession extends AuthenticatedWebSession
{

    public LoginSession(Request request)
    {
        super(request);
    }

    @Override
    public boolean authenticate(String username, String password) 
    {
        boolean authenticationCompleted = false;
        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();

        User signInUser = null;
        try
        {
            UsersMapper usersMapper = session.getMapper(UsersMapper.class);
            signInUser = usersMapper.signInUser(username, password);
        }
        finally
        {
            session.close();
        }
        
        if (signInUser != null)
        {
            this.user = signInUser;
            authenticationCompleted = true;
        }
        
        return authenticationCompleted;
    }

    @Override
    public Roles getRoles()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public User getUser()
    {
        return user;
    }
    
    private User user;

}
