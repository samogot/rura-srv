package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.Project;

import java.util.Collection;
import java.util.List;

public interface ProjectsMapper
{
    void insertProject(Project project);

    Project getProjectByUrl(String url);

    List<Project> getSubProjectsByParentProjectId(Integer parentId);

    Collection<Project> getAllProjects();

    Collection<Project> getRootProjects();

    void updateProject(Project Project);

    void deleteProject(Integer projectId);
}
