package ru.ruranobe.mybatis.mappers;

import java.util.Collection;
import ru.ruranobe.mybatis.tables.Project;

public interface ProjectsMapper 
{
    public void insertProject(Project project);
    public Project getProjectByUrl(String url);
    public Collection<Project> getSubProjectsByParentProjectId(Integer parentId);
    public Collection<Project> getAllProjects();
    public void updateProject(Project Project);
    public void deleteProject(Integer projectId);
}
