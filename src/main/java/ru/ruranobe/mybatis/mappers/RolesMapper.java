package ru.ruranobe.mybatis.mappers;

import java.util.List;

public interface RolesMapper
{
    List<String> getUserGroupsByUser(int userId);
}
