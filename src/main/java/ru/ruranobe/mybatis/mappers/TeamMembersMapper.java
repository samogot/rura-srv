package ru.ruranobe.mybatis.mappers;

import org.apache.ibatis.annotations.Param;
import ru.ruranobe.mybatis.entities.tables.TeamMember;

import java.util.Collection;
import java.util.List;

public interface TeamMembersMapper
{
    TeamMember getTeamMemberById(int memberId);

    List<TeamMember> getAllTeamMembersWithUserName();

    Collection<TeamMember> searchTeamMembersByNickname(@Param("query") String query,
                                                                        @Param("activeOnly") boolean activeOnly);

    Collection<TeamMember> getAllTeamMembers(@Param("activeOnly") boolean activeOnly);

    Collection<TeamMember> getTeamMembersByTeamId(@Param("teamId") Integer teamId,
                                                                   @Param("activeOnly") boolean activeOnly);

    void insertTeamMember(TeamMember teamMember);

    void insertIgnoreTeamMember(@Param("nickname") String teamMemberName);

    void deleteTeamMember(int teamMemberId);

    void updateTeamMember(TeamMember teamMember);
}
