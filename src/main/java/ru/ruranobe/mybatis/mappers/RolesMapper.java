package ru.ruranobe.mybatis.mappers;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RolesMapper
{
    List<String> getUserGroupsByUser(int userId);

    void deleteUserGroupsByUserId(int userId);

    void setUserGroupsByUserId(@Param("userId") int userId, @Param("userGroups") List<String> userGroups);

    List<String> getAllUserGroups();
}
