package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.mappers.TeamsMapper;
import ru.ruranobe.mybatis.tables.Team;

public class TeamsMapperCacheable implements TeamsMapper
{
    public TeamsMapperCacheable(TeamsMapper mapper)
    {
        this.mapper = mapper;
    }

    @Override
    public Team getTeamById(int teamId)
    {
        return mapper.getTeamById(teamId);
    }

    @Override
    public void insertTeam(Team team)
    {
        mapper.insertTeam(team);
    }

    @Override
    public void deleteTeam(int teamId)
    {
        mapper.deleteTeam(teamId);
    }

    @Override
    public void updateTeam(Team team)
    {
        mapper.updateTeam(team);
    }
    
    private TeamsMapper mapper;
}
