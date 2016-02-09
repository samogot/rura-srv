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
import ru.ruranobe.wicket.resources.rest.base.GsonObjectRestResource;
import ru.ruranobe.wicket.validators.AllowedFieldsValidator;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@ResourcePath("/api/members")
public class TeamMembersRestWebService extends GsonObjectRestResource
{

    private static final List<String> ALLOWED_FIELD_LIST = Arrays.asList("member_id", "user_id", "team_id", "nickname");

    @MethodMapping("/search")
    public Collection<TeamMember> searchMembers(@RequestParam("q") String query,
                                                @RequestParam(value = "fields", required = false, defaultValue = "nickname")
                                                @ValidatorKey("fields_validator") String columns,
                                                @RequestParam(value = "active", required = false, defaultValue = "false") boolean activeOnly)
    {
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            TeamMembersMapper teamMembersMapper = CachingFacade.getCacheableMapper(session, TeamMembersMapper.class);
            return teamMembersMapper.searchTeamMembersByNicknameWithCustomColumns(query, columns, activeOnly);
        }
    }

    @MethodMapping("/team/{teamId}")
    public Collection<TeamMember> getMembersByTeam(Integer teamId,
                                                   @RequestParam(value = "fields", required = false, defaultValue = "nickname")
                                                   @ValidatorKey("fields_validator") String columns,
                                                   @RequestParam(value = "active", required = false, defaultValue = "false") boolean activeOnly)
    {
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            TeamMembersMapper teamMembersMapper = CachingFacade.getCacheableMapper(session, TeamMembersMapper.class);
            return teamMembersMapper.getTeamMembersByTeamIdWithCustomColumns(teamId, columns, activeOnly);
        }
    }

    @MethodMapping("/{memberId}")
    public TeamMember getMemberById(int memberId,
                                    @RequestParam(value = "fields", required = false, defaultValue = "member_id,user_id,team_id,nickname")
                                    @ValidatorKey("fields_validator") String columns)
    {
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            TeamMembersMapper teamMembersMapper = CachingFacade.getCacheableMapper(session, TeamMembersMapper.class);
            return teamMembersMapper.getTeamMemberByIdWithCustomColumns(memberId, columns);
        }
    }

    @MethodMapping("")
    public Collection<TeamMember> getAllMembers(@RequestParam(value = "fields", required = false, defaultValue = "nickname")
                                                @ValidatorKey("fields_validator") String columns,
                                                @RequestParam(value = "active", required = false, defaultValue = "false") boolean activeOnly)
    {
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            TeamMembersMapper teamMembersMapper = CachingFacade.getCacheableMapper(session, TeamMembersMapper.class);
            return teamMembersMapper.getAllTeamMembersWithCustomColumns(columns, activeOnly);
        }
    }

    @Override
    protected void onInitialize(JsonWebSerialDeserial objSerialDeserial)
    {
        super.onInitialize(objSerialDeserial);
        registerValidator("fields_validator", new AllowedFieldsValidator(ALLOWED_FIELD_LIST).setParamName("fields"));
    }
}