package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.User;

import java.util.Collection;
import java.util.Set;

public interface UsersMapper
{
    User getUserByUsername(String username);

    User getUserByEmail(String email);

    User getUserById(Integer userId);

    int registerUser(User user);

    void updateUser(User user);

    User getUserByPassRecoveryToken(String passRecoveryToken);

    User getUserByEmailToken(String token);

    Set<String> getOwnProjectsByUser(int userId);

    Collection<User> searchUsersByUsername(String query);
}
