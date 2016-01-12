package ru.ruranobe.mybatis.mappers;

import org.apache.ibatis.annotations.Param;
import ru.ruranobe.mybatis.entities.tables.User;

import java.util.Collection;

public interface UsersMapper
{
    User getUserByUsername(String username);

    User getUserByEmail(String email);

    int registerUser(User user);

    void updateUser(User user);

    User getUserByPassRecoveryToken(String passRecoveryToken);

    User getUserByEmailToken(String token);

    Collection<User> searchUsersByUsername(String query);

    Collection<User> searchUsersByUsernameWithCustomColumns(@Param("query") String query, @Param("columns") String columns);
}
