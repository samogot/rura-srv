package ru.ruranobe.wicket.resources.rest;

import com.google.common.collect.ImmutableList;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.ResourcePath;
import org.wicketstuff.rest.annotations.parameters.RequestParam;
import org.wicketstuff.rest.annotations.parameters.ValidatorKey;
import org.wicketstuff.rest.contenthandling.RestMimeTypes;
import org.wicketstuff.rest.resource.gson.GsonRestResource;
import org.wicketstuff.rest.resource.gson.GsonSerialDeserial;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.validators.AllowedFieldsValidator;

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

    @MethodMapping(value = "", produces = RestMimeTypes.APPLICATION_JSON)
    public Collection<Project> getProjects(@RequestParam(value = "fields", required = false, defaultValue = "title,url")
                                           @ValidatorKey("fields_validator") String columns)
    {
        Collection<Project> result;

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        try
        {
            ProjectsMapper projectsMapper = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
            result = projectsMapper.getAllProjectsWithCustomColumns(columns);
        }
        finally
        {
            session.close();
        }

        return result;
    }

    private static final List<String> ALLOWED_FIELD_LIST = new ImmutableList.Builder<String>()
            .add("parent_id")
            .add("image_id")
            .add("url")
            .add("name_jp")
            .add("name_en")
            .add("name_ru")
            .add("name_romaji")
            .add("author")
            .add("illustrator")
            .add("order_number")
            .add("banner_hidden")
            .add("onevolume")
            .add("franchise")
            .add("annotation")
            .add("title")
            .build();
}