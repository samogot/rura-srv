package ru.ruranobe.wicket;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import ru.ruranobe.misc.Authentication;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.User;
import ru.ruranobe.mybatis.mappers.RolesMapper;
import ru.ruranobe.mybatis.mappers.UsersMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;

import java.util.List;
import java.util.Set;

public class LoginSession extends AuthenticatedWebSession
{

    public boolean hasOwnProject(String project)
    {
        return ownProjects != null && ownProjects.contains(project);
    }

    public boolean isProjectEditAllowedByUser(String project)
    {
        return roles != null && (roles.hasRole("ADMIN") || roles.hasRole("TEAM MEMBER")
                                 || roles.hasRole("WORKS") && hasOwnProject(project));
    }

    public boolean isProjectShowHiddenAllowedByUser(String project)
    {
        return isProjectEditAllowedByUser(project) && getUser().isShowHiddenContent();
    }

    public boolean hasRole(String role)
    {
        return roles != null && roles.hasRole(role);
    }

    public void setStyleColor(String styleColor)
    {
        this.styleColor = styleColor;
    }

    public void setStyleDayNight(String styleDayNight)
    {
        this.styleDayNight = styleDayNight;
    }

    public String getBodyClassStyle()
    {
        return styleColor + " " + styleDayNight;
    }

    public void setForceDesktopVersion(boolean forceDesktopVersion)
    {
        this.forceDesktopVersion = forceDesktopVersion;
    }

    public boolean isForceDesktopVersion()
    {
        return forceDesktopVersion;
    }

    private User user;
    private Roles roles = null;
    private Set<String> ownProjects = null;

    public LoginSession(Request request)
    {
        super(request);
    }

    public static LoginSession get()
    {
        return (LoginSession) AuthenticatedWebSession.get();
    }

    @Override
    public boolean authenticate(String username, String password)
    {
        boolean authenticationCompleted = false;
        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        try (SqlSession session = sessionFactory.openSession())
        {
            UsersMapper usersMapper = CachingFacade.getCacheableMapper(session, UsersMapper.class);
            User signInUser = usersMapper.getUserByUsername(username);
            if (signInUser != null)
            {
                String hash = Authentication.getPassHash(signInUser.getPassVersion(), password, signInUser.getPass());
                if (areHashesEqual(hash, signInUser.getPass()))
                {
                    if (signInUser.getPassVersion() < Authentication.ACTUAL_HASH_TYPE)
                    {
                        signInUser.setPassWithActualVersion(password);
                        usersMapper.updateUser(signInUser);
                    }
                    this.user = signInUser;
                    ownProjects = usersMapper.getOwnProjectsByUser(user.getUserId());
                    RolesMapper rolesMapperCacheable = CachingFacade.getCacheableMapper(session, RolesMapper.class);
                    List<String> roles = rolesMapperCacheable.getUserGroupsByUser(user.getUserId());
                    if (roles != null)
                    {
                        this.roles = new Roles(roles.toArray(new String[roles.size()]));
                    }
                    authenticationCompleted = true;
                }
            }
            session.commit();
        }
        return authenticationCompleted;
    }

    @Override
    public Roles getRoles()
    {
        return roles;
    }

    public User getUser()
    {
        return user;
    }

    @Override
    public void signOut()
    {
        super.signOut();
        this.user = null;
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        this.user = null;
    }

    public boolean validatePassword(String password)
    {
        String hash = Authentication.getPassHash(user.getPassVersion(), password, user.getPass());
        return areHashesEqual(hash, user.getPass());
    }

    public void updateUser(User user)
    {
        this.user = user;
    }

    private boolean areHashesEqual(String hash1, String hash2)
    {
        boolean result = false;
        if (hash2 != null && hash1 != null)
        {
            result = hash1.equalsIgnoreCase(hash2);
        }
        return result;
    }

    private String styleColor = "";
    private String styleDayNight = "";
    private boolean forceDesktopVersion = false;
}
