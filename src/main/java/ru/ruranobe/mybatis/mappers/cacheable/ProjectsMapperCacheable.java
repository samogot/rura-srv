package ru.ruranobe.mybatis.mappers.cacheable;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.tables.Project;

/* Simple inmemory cache, since the number of projects is very limited */
public class ProjectsMapperCacheable implements ProjectsMapper
{
    public ProjectsMapperCacheable(ProjectsMapper mapper)
    {
        this.mapper = mapper;
        if (getAllProjectsMethodCalled.compareAndSet(false, true))
        {
            Collection<Project> projects = mapper.getAllProjects();
            for (Project project : projects)
            {
                projectIdToProject.put(project.getProjectId(), project);
            }
        }
    }
    
    @Override
    public void insertProject(Project project)
    {
        mapper.insertProject(project);
        projectIdToProject.put(project.getProjectId(), project);
    }

    /* Uncacheable operation. For this operation DB level cache is used. 
     * See ProjectsMapper cache tag */
    @Override
    public Collection<Project> getProjectsByUrl(String url)
    {
        return mapper.getProjectsByUrl(url);
    }

    @Override
    public Collection<Project> getAllProjects()
    {
        return projectIdToProject.values();
    }

    @Override
    public void updateProject(Project project)
    {
        mapper.updateProject(project);
        projectIdToProject.replace(project.getProjectId(), project);
    }

    @Override
    public void deleteProject(Integer projectId)
    {
        mapper.deleteProject(projectId);
        projectIdToProject.remove(projectId);
    }
    
    private final ProjectsMapper mapper;
    private static final AtomicBoolean getAllProjectsMethodCalled = new AtomicBoolean(false);
    private static final ConcurrentHashMap<Integer, Project> projectIdToProject = 
            new ConcurrentHashMap<Integer, Project>();
}

