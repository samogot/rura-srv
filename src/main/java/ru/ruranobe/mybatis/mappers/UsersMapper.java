package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.User;

public interface UsersMapper
{
    public User getUserByUsername(String username);

    public User getUserByEmail(String email);

    public int registerUser(User user);

    public void updateUser(User user);

    public User getUserByPassRecoveryToken(String passRecoveryToken);

    public User getUserByEmailToken(String token);
    /*
    public void insertUser(User user);*/
}
