package ru.ruranobe.wicket.resources.rest;

import org.apache.ibatis.session.SqlSession;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.ResourcePath;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.resources.rest.base.GsonObjectRestResource;

import java.util.Collection;

@ResourcePath("/api/people")
public class PeopleRestWebService extends GsonObjectRestResource
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
}