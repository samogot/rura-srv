package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.User;

public interface UsersMapper
{
    User getUserByUsername(String username);

    User getUserByEmail(String email);

    int registerUser(User user);

    void updateUser(User user);

    User getUserByPassRecoveryToken(String passRecoveryToken);

    User getUserByEmailToken(String token);
}
