package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.tables.Team;
import ru.ruranobe.mybatis.tables.TeamMember;

import java.util.List;

/**
 * Created by Samogot on 10.05.2015.
 */
public interface TeamMembersMapper
{
    public TeamMember getTeamMemberById(int memberId);

    public List<TeamMember> getAllTeamMembers();

    public void insertTeamMember(TeamMember teamMember);

    public void deleteTeamMember(int teamMemberId);

    public void updateTeamMember(TeamMember teamMember);
}
