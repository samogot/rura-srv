package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.mappers.RolesMapper;

import java.util.List;

public class RolesMapperCacheable implements RolesMapper
{
    private RolesMapper mapper;

    public RolesMapperCacheable(RolesMapper mapper)
    {
        this.mapper = mapper;
    }

    public List<String> getUserGroupsByUser(int userId)
    {
        return mapper.getUserGroupsByUser(userId);
    }

    public void deleteUserGroupsByUserId(int userId)
    {
        mapper.deleteUserGroupsByUserId(userId);
    }

    public void setUserGroupsByUserId(int userId, List<String> userGroups)
    {
        mapper.setUserGroupsByUserId(userId, userGroups);
    }

    public List<String> getAllUserGroups()
    {
        return mapper.getAllUserGroups();
    }
}
