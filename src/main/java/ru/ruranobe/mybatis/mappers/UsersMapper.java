package ru.ruranobe.mybatis.mappers;

import org.apache.ibatis.annotations.Param;
import ru.ruranobe.mybatis.tables.User;

public interface UsersMapper 
{
    public User getUserByUsername(String username);
    public User getUserByEmail(String email);
    public int registerUser(User user);
    public void updateUser(User user);
    public User getUserByPassRecoveryToken(String passRecoveryToken);
    public User signInUser(@Param("username") String username, 
                           @Param("pass") String pass);
    public User getUserByEmailToken(String token);
    /*
    public void insertUser(User user);
    public boolean signInUser(String username, String password);*/
}
