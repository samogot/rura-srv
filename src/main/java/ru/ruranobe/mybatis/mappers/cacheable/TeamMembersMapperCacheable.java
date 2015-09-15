package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.mappers.TeamMembersMapper;
import ru.ruranobe.mybatis.entities.tables.TeamMember;

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
    public List<TeamMember> getAllTeamMembers()
    {
        return mapper.getAllTeamMembers();
    }

    @Override
    public void insertTeamMember(TeamMember teamMember)
    {
        mapper.insertTeamMember(teamMember);
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
