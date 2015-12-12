package ru.ruranobe.wicket.resources;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.parameters.RequestParam;
import org.wicketstuff.rest.contenthandling.RestMimeTypes;
import org.wicketstuff.rest.resource.gson.GsonRestResource;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ProjectsRestWebService extends GsonRestResource
{
    @MethodMapping(value = "/get/{all}", produces = RestMimeTypes.APPLICATION_JSON)
    public Collection<Project> getProjects(@RequestParam("params") String initialResponseParameter)
    {
        Collection<Project> result;

        // We receive column representations to be send back to the client in initialResponseParameters.
        // Transform them using internal mapping.
        Collection<String> responseParameters = getResponseParameters(initialResponseParameter.split(";"));
        String columns = StringUtils.join(responseParameters, ',');
        columns = " " + columns + " ";

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

    private Collection<String> getResponseParameters(String[] initialResponseParameters)
    {
        Collection<String> responseParameters = new ArrayList<String>();
        if (initialResponseParameters != null)
        {
            for (String initialResponseParameter : initialResponseParameters)
            {
                String responseParameter = RESPONSE_PARAMETER_TO_MYBATIS_COLUMN.get(initialResponseParameter);
                if (responseParameter != null)
                {
                    responseParameters.add(responseParameter);
                }
            }
        }
        if (responseParameters.isEmpty())
        {
            responseParameters = RESPONSE_PARAMETER_TO_MYBATIS_COLUMN.values();
        }
        return responseParameters;
    }

    private static final Map<String, String> RESPONSE_PARAMETER_TO_MYBATIS_COLUMN =
            new ImmutableMap.Builder<String, String>()
                .put("parent_id", "parent_id")
                .put("image_id", "image_id")
                .put("url", "url")
                .put("name_jp", "name_jp")
                .put("name_en", "name_en")
                .put("name_ru", "name_ru")
                .put("name_romaji", "name_romaji")
                .put("author", "author")
                .put("illustrator", "illustrator")
                .put("order_number", "order_number")
                .put("banner_hidden", "banner_hidden")
                .put("onevolume", "onevolume")
                .put("franchise", "franchise")
                .put("annotation", "annotation")
                .put("title", "title")
            .build();
}