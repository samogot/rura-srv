package ru.ruranobe.mybatis.mappers;

import org.apache.ibatis.annotations.Param;
import ru.ruranobe.mybatis.entities.tables.Team;

import java.util.List;

public interface TeamsMapper
{
    Team getTeamById(int teamId);

    Team getTeamByMember(@Param("nickname") String teamMemberName);

    List<Team> getAllTeams();

    void insertTeam(Team team);

    void deleteTeam(int teamId);

    void updateTeam(Team team);
}
