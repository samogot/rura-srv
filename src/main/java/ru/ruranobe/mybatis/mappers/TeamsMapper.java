package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.tables.Team;

import java.util.List;

public interface TeamsMapper
{
    public Team getTeamById(int teamId);

    public List<Team> getAllTeams();

    public void insertTeam(Team team);

    public void deleteTeam(int teamId);

    public void updateTeam(Team team);
}
