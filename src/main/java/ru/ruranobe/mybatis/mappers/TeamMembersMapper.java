package ru.ruranobe.mybatis.mappers;

import org.apache.ibatis.annotations.Param;
import ru.ruranobe.mybatis.entities.tables.TeamMember;

import java.util.Collection;
import java.util.List;

public interface TeamMembersMapper
{
    TeamMember getTeamMemberById(int memberId);

    TeamMember getTeamMemberByIdWithCustomColumns(@Param("memberId") int memberId,
                                                  @Param("columns") String columns);

    List<TeamMember> getAllTeamMembers();

    List<TeamMember> getAllTeamMembersWithUserName();

    Collection<TeamMember> searchTeamMembersByNicknameWithCustomColumns(@Param("query") String query,
                                                                        @Param("columns") String columns,
                                                                        @Param("activeOnly") boolean activeOnly);

    Collection<TeamMember> getAllTeamMembersWithCustomColumns(@Param("columns") String columns,
                                                              @Param("activeOnly") boolean activeOnly);

    Collection<TeamMember> getTeamMembersByTeamIdWithCustomColumns(@Param("teamId") Integer teamId,
                                                                   @Param("columns") String columns,
                                                                   @Param("activeOnly") boolean activeOnly);

    void insertTeamMember(TeamMember teamMember);

    void insertIgnoreTeamMember(@Param("nickname") String teamMemberName);

    void deleteTeamMember(int teamMemberId);

    void updateTeamMember(TeamMember teamMember);
}
