package ru.ruranobe.wicket.resources.rest;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.ibatis.session.SqlSession;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.ResourcePath;
import org.wicketstuff.rest.annotations.parameters.RequestParam;
import org.wicketstuff.rest.annotations.parameters.ValidatorKey;
import org.wicketstuff.rest.contenthandling.json.webserialdeserial.JsonWebSerialDeserial;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.User;
import ru.ruranobe.mybatis.mappers.UsersMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.LoginSession;
import ru.ruranobe.wicket.resources.rest.base.AuthorizeInvocation;
import ru.ruranobe.wicket.resources.rest.base.FieldFilteringUtils;
import ru.ruranobe.wicket.resources.rest.base.GsonObjectRestResource;
import ru.ruranobe.wicket.validators.AllowedFieldsValidator;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@ResourcePath("/api/users")
public class UsersRestWebService extends GsonObjectRestResource
{

    @MethodMapping("/search")
    public Collection<User> searchUsers(@RequestParam("q") String query,
                                        @RequestParam(value = "fields", required = false, defaultValue = "userId|username")
                                        @ValidatorKey("user_fields_validator") String fieldsString)
    {
        HashSet<String> fields = FieldFilteringUtils.parseFieldsList(fieldsString);
        if (!LoginSession.get().hasRole("ADMIN"))
        {
            for (String field : ALLOWED_ADMIN_USER_FIELD_LIST)
            {
                if (fields.contains(field))
                {
                    throw getUnauthorizedException().setDescription("Field \"" + field + "\" allowed only for admins");
                }
            }
        }
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            UsersMapper usersMapper = CachingFacade.getCacheableMapper(session, UsersMapper.class);
            Collection<User> users = usersMapper.searchUsersByUsername(query);
            for (User user : users)
            {
                FieldFilteringUtils.filterAllowedFields(user, fields);
            }
            return users;
        }
    }

    @AuthorizeInvocation("USER")
    @MethodMapping("/{userId}")
    public User getUser(Integer userId,
                        @RequestParam(value = "fields", required = false, defaultValue = "userId|username|realname|registrationDate")
                        @ValidatorKey("user_fields_validator") String fieldsString)
    {
        HashSet<String> fields = FieldFilteringUtils.parseFieldsList(fieldsString);
        if (!LoginSession.get().hasRole("ADMIN"))
        {
            for (String field : ALLOWED_ADMIN_USER_FIELD_LIST)
            {
                if (fields.contains(field))
                {
                    throw getUnauthorizedException().setDescription("Field \"" + field + "\" allowed only for admins");
                }
            }
        }
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            UsersMapper usersMapper = CachingFacade.getCacheableMapper(session, UsersMapper.class);
            User user = usersMapper.getUserById(userId);
            FieldFilteringUtils.filterAllowedFields(user, fields);
            return user;
        }
    }

    @AuthorizeInvocation("USER")
    @MethodMapping("/me")
    public User getSelf(@RequestParam(value = "fields", required = false, defaultValue = "userId|username|realname|email|registrationDate")
                        @ValidatorKey("user_fields_validator") String fieldsString)
    {
        HashSet<String> fields = FieldFilteringUtils.parseFieldsList(fieldsString);
        User user = SerializationUtils.clone(LoginSession.get().getUser());
        FieldFilteringUtils.filterAllowedFields(user, fields);
        return user;
    }

    @Override
    protected void onInitialize(JsonWebSerialDeserial objSerialDeserial)
    {
        super.onInitialize(objSerialDeserial);
        registerValidator("user_fields_validator", new AllowedFieldsValidator(ALLOWED_FIELD_LIST).setParamName("fields"));
    }
    private static final List<String> ALLOWED_FIELD_LIST = Arrays.asList("userId", "username", "realname", "email",
            "registrationDate", "converterType", "navigationType", "convertWithImgs", "adult", "preferColoredImgs",
            "showHiddenContent", "convertImgsSize");
    private static final List<String> ALLOWED_ADMIN_USER_FIELD_LIST = Arrays.asList("email",
            "converterType", "navigationType", "convertWithImgs", "adult", "preferColoredImgs",
            "showHiddenContent", "convertImgsSize");
}