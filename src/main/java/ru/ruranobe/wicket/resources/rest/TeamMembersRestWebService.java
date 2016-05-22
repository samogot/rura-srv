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

@ResourcePath("/api/members")
public class TeamMembersRestWebService extends GsonObjectRestResource
{

    @MethodMapping("/search")
    public Collection<TeamMember> searchMembers(@RequestParam("q") String query,
                                                @RequestParam(value = "fields", required = false, defaultValue = "nickname")
                                                @ValidatorKey("member_fields_validator") String fieldsString,
                                                @RequestParam(value = "active", required = false, defaultValue = "false") boolean activeOnly)
    {
        HashSet<String> fields = FieldFilteringUtils.parseFieldsList(fieldsString);
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            TeamMembersMapper teamMembersMapper = CachingFacade.getCacheableMapper(session, TeamMembersMapper.class);
            Collection<TeamMember> members = teamMembersMapper.searchTeamMembersByNickname(query, activeOnly);
            for (TeamMember member : members)
            {
                FieldFilteringUtils.filterAllowedFields(member, fields);
            }
            return members;
        }
    }

    @MethodMapping("/{memberId}")
    public TeamMember getMemberById(int memberId,
                                    @RequestParam(value = "fields", required = false, defaultValue = "memberId|userId|teamId|nickname")
                                    @ValidatorKey("member_fields_validator") String fieldsString)
    {
        HashSet<String> fields = FieldFilteringUtils.parseFieldsList(fieldsString);
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            TeamMembersMapper teamMembersMapper = CachingFacade.getCacheableMapper(session, TeamMembersMapper.class);
            TeamMember member = teamMembersMapper.getTeamMemberById(memberId);
            FieldFilteringUtils.filterAllowedFields(member, fields);
            return member;
        }
    }

    @MethodMapping("")
    public Collection<TeamMember> getAllMembers(@RequestParam(value = "fields", required = false, defaultValue = "nickname")
                                                @ValidatorKey("member_fields_validator") String fieldsString,
                                                @RequestParam(value = "active", required = false, defaultValue = "false") boolean activeOnly)
    {
        HashSet<String> fields = FieldFilteringUtils.parseFieldsList(fieldsString);
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            TeamMembersMapper teamMembersMapper = CachingFacade.getCacheableMapper(session, TeamMembersMapper.class);
            Collection<TeamMember> members = teamMembersMapper.getAllTeamMembers(activeOnly);
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