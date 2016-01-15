package ru.ruranobe.mybatis.mappers.cacheable;

import org.apache.ibatis.annotations.Param;
import ru.ruranobe.mybatis.entities.tables.TeamMember;
import ru.ruranobe.mybatis.mappers.TeamMembersMapper;

import java.util.Collection;
import java.util.List;

/**
 * Created by samogot on 08.05.15.
 */
public class TeamMembersMapperCacheable implements TeamMembersMapper
{
    private TeamMembersMapper mapper;

    public TeamMembersMapperCacheable(TeamMembersMapper mapper)
    {
        this.mapper = mapper;
    }

    @Override
    public TeamMember getTeamMemberById(int memberId)
    {
        return getTeamMemberByIdWithCustomColumns(memberId, "*");
    }

    @Override
    public TeamMember getTeamMemberByIdWithCustomColumns(@Param("memberId") int memberId, @Param("columns") String columns)
    {
        return mapper.getTeamMemberByIdWithCustomColumns(memberId, columns);
    }

    @Override
    public List<TeamMember> getAllTeamMembers()
    {
        return mapper.getAllTeamMembers();
    }

    @Override
    public List<TeamMember> getAllTeamMembersWithUserName()
    {
        return mapper.getAllTeamMembersWithUserName();
    }

    @Override
    public Collection<TeamMember> searchTeamMembersByNicknameWithCustomColumns(@Param("query") String query, @Param("columns") String columns, @Param("activeOnly") boolean activeOnly)
    {
        return mapper.searchTeamMembersByNicknameWithCustomColumns(query, columns, activeOnly);
    }

    @Override
    public Collection<TeamMember> getAllTeamMembersWithCustomColumns(@Param("columns") String columns, @Param("activeOnly") boolean activeOnly)
    {
        return mapper.getAllTeamMembersWithCustomColumns(columns, activeOnly);
    }

    @Override
    public Collection<TeamMember> getTeamMembersByTeamIdWithCustomColumns(@Param("teamId") Integer teamId, @Param("columns") String columns, @Param("activeOnly") boolean activeOnly)
    {
        return mapper.getTeamMembersByTeamIdWithCustomColumns(teamId, columns, activeOnly);
    }

    @Override
    public void insertTeamMember(TeamMember teamMember)
    {
        mapper.insertTeamMember(teamMember);
    }

    @Override
    public void insertIgnoreTeamMember(String teamMemberName)
    {
        mapper.insertIgnoreTeamMember(teamMemberName);
    }

    @Override
    public void deleteTeamMember(int teamMemberId)
    {
        mapper.deleteTeamMember(teamMemberId);
    }

    @Override
    public void updateTeamMember(TeamMember teamMember)
    {
        mapper.updateTeamMember(teamMember);
    }
}
