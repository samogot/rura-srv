package ru.ruranobe.wicket.resources.rest;

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
import ru.ruranobe.wicket.resources.rest.base.GsonObjectRestResource;
import ru.ruranobe.wicket.validators.AllowedFieldsValidator;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@ResourcePath("/api/users")
public class UsersRestWebService extends GsonObjectRestResource
{

    private static final List<String> ALLOWED_FIELD_LIST = Arrays.asList("user_id", "username", "realname", "email",
            "registration_date", "converter_type", "navigation_type", "convert_with_imgs",
            "adult", "prefer_colored_imgs", "convert_imgs_size");

    @MethodMapping("/search")
    public Collection<User> searchUsers(@RequestParam("q") String query,
                                        @RequestParam(value = "fields", required = false, defaultValue = "user_id,username")
                                        @ValidatorKey("fields_validator") String columns)
    {
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            UsersMapper usersMapper = CachingFacade.getCacheableMapper(session, UsersMapper.class);
            return usersMapper.searchUsersByUsernameWithCustomColumns(query, columns);
        }
    }

    @Override
    protected void onInitialize(JsonWebSerialDeserial objSerialDeserial)
    {
        super.onInitialize(objSerialDeserial);
        registerValidator("fields_validator", new AllowedFieldsValidator(ALLOWED_FIELD_LIST).setParamName("fields"));
    }
}