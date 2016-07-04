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
        return mapper.getTeamMemberById(memberId);
    }

    @Override
    public List<TeamMember> getAllTeamMembersWithUserName()
    {
        return mapper.getAllTeamMembersWithUserName();
    }

    @Override
    public Collection<TeamMember> searchTeamMembersByNickname(@Param("query") String query, @Param("activeOnly") boolean activeOnly)
    {
        return mapper.searchTeamMembersByNickname(query, activeOnly);
    }

    @Override
    public Collection<TeamMember> getAllTeamMembers(@Param("activeOnly") boolean activeOnly)
    {
        return mapper.getAllTeamMembers(activeOnly);
    }

    @Override
    public Collection<TeamMember> getTeamMembersByTeamId(@Param("teamId") Integer teamId, @Param("activeOnly") boolean activeOnly)
    {
        return mapper.getTeamMembersByTeamId(teamId, activeOnly);
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
