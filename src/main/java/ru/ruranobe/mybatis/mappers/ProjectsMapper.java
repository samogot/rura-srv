package ru.ruranobe.mybatis.mappers;

import java.util.List;
import ru.ruranobe.mybatis.tables.Project;

public interface ProjectsMapper 
{
    public void insertProject(Project project);
    public Project getProjectByUrl(String url);
    public List<Project> getAllProjects();
    public void updateProject(Project Project);
    public void deleteProject(Integer projectId);
}
