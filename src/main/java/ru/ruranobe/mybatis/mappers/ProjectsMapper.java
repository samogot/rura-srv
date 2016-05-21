package ru.ruranobe.mybatis.mappers;

import org.apache.ibatis.annotations.Param;
import ru.ruranobe.mybatis.entities.tables.Project;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface ProjectsMapper
{
    void insertProject(Project project);

    Project getProjectByUrl(String url);

    Project getProjectById(Integer projectId);

    List<Project> getSubProjectsByParentProjectId(Integer parentId);

    Collection<Project> getAllProjects();

    Collection<Project> getRootProjects();

    Collection<Project> getAllProjectsWithCustomColumns(@Param("columns") String columns);

    void updateProject(Project Project);

    void deleteProject(Integer projectId);

    Collection<String> getAllPeople();

    Date getProjectUpdateDate(Integer projectId);

    Date getProjectEditDate(Integer projectId);
}
