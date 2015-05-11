package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.tables.TeamMember;

/**
 * Created by Samogot on 10.05.2015.
 */
public interface TeamMembersMapper
{
    public TeamMember getTeamMemberById(int memberId);
}
