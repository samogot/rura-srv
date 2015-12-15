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
}
