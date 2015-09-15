package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.Project;

import java.util.Collection;
import java.util.List;

public interface ProjectsMapper
{
    public void insertProject(Project project);

    public Project getProjectByUrl(String url);

    public List<Project> getSubProjectsByParentProjectId(Integer parentId);

    public Collection<Project> getAllProjects();

    public void updateProject(Project Project);

    public void deleteProject(Integer projectId);
}
