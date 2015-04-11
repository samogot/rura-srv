package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.tables.Team;

public interface TeamsMapper 
{
    public Team getTeamById (int teamId);
    public void insertTeam(Team team);
    public void deleteTeam(int teamId);
    public void updateTeam(Team team);
}
