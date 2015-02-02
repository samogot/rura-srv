package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.tables.User;

public interface UsersMapper 
{
    public User getUserByUsername(String username);
    public User getUserByEmail(String email);
    public void registerUser(User user);
    public void updateUser(User user);
    public User getUserByPassRecoveryToken(String passRecoveryToken);
    /*
    public void insertUser(User user);
    public boolean signInUser(String username, String password);*/
}
