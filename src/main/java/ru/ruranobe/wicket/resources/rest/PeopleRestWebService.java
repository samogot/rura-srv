package ru.ruranobe.wicket.resources.rest;

import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.request.http.WebResponse;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.ResourcePath;
import org.wicketstuff.rest.resource.gson.GsonRestResource;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;

import java.util.Collection;

@ResourcePath("/api/people")
public class PeopleRestWebService extends GsonRestResource
{
    @MethodMapping("")
    public Collection<String> getPeople()
    {
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            ProjectsMapper projectsMapper = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
            return projectsMapper.getAllPeople();
        }
    }

    @Override
    protected void handleException(WebResponse response, Exception exception)
    {
        super.handleException(response, exception);
        exception.printStackTrace();
    }
}