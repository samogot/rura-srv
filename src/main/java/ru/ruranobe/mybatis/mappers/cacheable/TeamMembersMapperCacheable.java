package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.mappers.TeamMembersMapper;
import ru.ruranobe.mybatis.tables.TeamMember;

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
}
