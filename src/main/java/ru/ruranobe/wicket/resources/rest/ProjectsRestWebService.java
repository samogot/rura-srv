package ru.ruranobe.wicket.resources.rest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.ResourcePath;
import org.wicketstuff.rest.annotations.parameters.RequestParam;
import org.wicketstuff.rest.annotations.parameters.ValidatorKey;
import org.wicketstuff.rest.resource.gson.GsonRestResource;
import org.wicketstuff.rest.resource.gson.GsonSerialDeserial;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.validators.AllowedFieldsValidator;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@ResourcePath("/api/projects")
public class ProjectsRestWebService extends GsonRestResource
{

    @Override
    protected void onInitialize(GsonSerialDeserial objSerialDeserial)
    {
        registerValidator("fields_validator", new AllowedFieldsValidator(ALLOWED_FIELD_LIST).setParamName("fields"));
    }

    @MethodMapping("")
    public Collection<Project> getProjects(@RequestParam(value = "fields", required = false, defaultValue = "title,url")
                                           @ValidatorKey("fields_validator") String columns)
    {
        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        try (SqlSession session = sessionFactory.openSession())
        {
            ProjectsMapper projectsMapper = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
            return projectsMapper.getAllProjectsWithCustomColumns(columns);
        }
    }

    private static final List<String> ALLOWED_FIELD_LIST = Arrays.asList("parent_id", "image_id", "url",
            "name_jp", "name_en", "name_ru", "name_romaji", "author", "illustrator", "order_number",
            "banner_hidden", "onevolume", "franchise", "annotation", "title");

}