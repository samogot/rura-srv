package ru.ruranobe.wicket;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

import ru.ruranobe.misc.Authentication;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.UsersMapper;
import ru.ruranobe.mybatis.entities.tables.User;

public class LoginSession extends AuthenticatedWebSession {

	private User user;

	public LoginSession(Request request) {
		super(request);
	}

	@Override
	public boolean authenticate(String username, String password) {
		boolean authenticationCompleted = false;
		SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
		SqlSession session = sessionFactory.openSession();
		try {
			UsersMapper usersMapper = session.getMapper(UsersMapper.class);
			User signInUser = usersMapper.getUserByUsername(username);
			if (signInUser != null) {
				String hash = Authentication.getPassHash(signInUser.getPassVersion(), password, signInUser.getPass());
				if (areHashesEqual(hash, signInUser.getPass())) {
					if (signInUser.getPassVersion() < Authentication.ACTUAL_HASH_TYPE) {
						signInUser.setPass(Authentication.getPassHash(Authentication.ACTUAL_HASH_TYPE, password, ""));
						signInUser.setPassVersion(Authentication.ACTUAL_HASH_TYPE);
						usersMapper.updateUser(signInUser);
					}
					this.user = signInUser;
					authenticationCompleted = true;
				}
			}
		} finally {
			session.close();
		}
		return authenticationCompleted;
	}

	@Override
	public Roles getRoles() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public User getUser() {
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

	private boolean areHashesEqual(String hash1, String hash2)
	{
		boolean result = false;
		if (hash2 != null && hash1 != null)
		{
			int len = Math.min(hash1.length(), hash2.length());
			int i = 0;
			for (; i < len; ++i)
			{
				if (hash1.codePointAt(i) != hash2.codePointAt(i))
				{
					break;
				}
			}
			result = i==len;
		}
		return result;
	}
}
