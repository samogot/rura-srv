package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.mappers.TeamsMapper;
import ru.ruranobe.mybatis.entities.tables.Team;

import java.util.List;

public class TeamsMapperCacheable implements TeamsMapper
{
    private TeamsMapper mapper;

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
    public List<Team> getAllTeams()
    {
        return mapper.getAllTeams();
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
}
