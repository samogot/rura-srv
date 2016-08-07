package ru.ruranobe.wicket.resources.rest;

import org.apache.ibatis.session.SqlSession;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.ResourcePath;
import org.wicketstuff.rest.annotations.parameters.RequestParam;
import org.wicketstuff.rest.annotations.parameters.ValidatorKey;
import org.wicketstuff.rest.contenthandling.json.webserialdeserial.JsonWebSerialDeserial;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.TeamMember;
import ru.ruranobe.mybatis.mappers.TeamMembersMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.resources.rest.base.FieldFilteringUtils;
import ru.ruranobe.wicket.resources.rest.base.GsonObjectRestResource;
import ru.ruranobe.wicket.resources.rest.base.ProperifingUtils;

import java.util.Collection;
import java.util.HashSet;

@ResourcePath("/api/teams")
public class TeamsRestWebService extends GsonObjectRestResource
{

    @MethodMapping("/{teamId}/members")
    public Collection<TeamMember> getMembersByTeam(Integer teamId,
                                                   @RequestParam(value = "fields", required = false, defaultValue = "nickname")
                                                   @ValidatorKey("member_fields_validator") String fieldsString,
                                                   @RequestParam(value = "active", required = false, defaultValue = "false") boolean activeOnly)
    {
        HashSet<String> fields = FieldFilteringUtils.parseFieldsList(fieldsString);
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            TeamMembersMapper teamMembersMapper = CachingFacade.getCacheableMapper(session, TeamMembersMapper.class);
            Collection<TeamMember> members = teamMembersMapper.getTeamMembersByTeamId(teamId, activeOnly);
            for (TeamMember member : members)
            {
                FieldFilteringUtils.filterAllowedFields(member, fields);
            }
            return members;
        }
    }

    @Override
    protected void onInitialize(JsonWebSerialDeserial objSerialDeserial)
    {
        super.onInitialize(objSerialDeserial);
        registerValidator("member_fields_validator", ProperifingUtils.ALLOWED_MEMBERS_FIELD_VALIDATOR);
    }
}