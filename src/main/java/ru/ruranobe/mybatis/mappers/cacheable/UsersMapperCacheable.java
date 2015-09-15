package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.entities.tables.User;
import ru.ruranobe.mybatis.mappers.UsersMapper;

public class UsersMapperCacheable implements UsersMapper
{
    private UsersMapper mapper;

    public UsersMapperCacheable(UsersMapper mapper)
    {
        this.mapper = mapper;
    }

    public User getUserByUsername(String username)
    {
        return mapper.getUserByUsername(username);
    }

    public User getUserByEmail(String email)
    {
        return  mapper.getUserByEmail(email);
    }

    public int registerUser(User user)
    {
        return mapper.registerUser(user);
    }

    public void updateUser(User user)
    {
        mapper.updateUser(user);
    }

    public User getUserByPassRecoveryToken(String passRecoveryToken)
    {
        return mapper.getUserByPassRecoveryToken(passRecoveryToken);
    }


    public User getUserByEmailToken(String token)
    {
        return mapper.getUserByEmailToken(token);
    }

}
