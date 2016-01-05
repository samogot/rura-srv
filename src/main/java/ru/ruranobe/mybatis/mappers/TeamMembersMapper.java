package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.TeamMember;

import java.util.List;

/**
 * Created by Samogot on 10.05.2015.
 */
public interface TeamMembersMapper
{
    TeamMember getTeamMemberById(int memberId);

    List<TeamMember> getAllTeamMembers();

    List<TeamMember> getAllTeamMembersWithUsernName();

    void insertTeamMember(TeamMember teamMember);

    void deleteTeamMember(int teamMemberId);

    void updateTeamMember(TeamMember teamMember);
}
