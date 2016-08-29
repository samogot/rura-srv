package ru.ruranobe.mybatis.mappers;

import org.apache.ibatis.annotations.Param;

import ru.ruranobe.mybatis.entities.base.SimpleEntry;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.mybatis.entities.tables.SectionProject;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface ProjectsMapper
{
    Collection<SimpleEntry<String, Integer>> getAllDomains();

    SimpleEntry<String, Integer> getDomain(@Param("domain") String domain);

    List<SectionProject> getAllSectionProjects(@Param("sectionId") Integer sectionId);

    SectionProject getSectionProjectByUrl(@Param("sectionId") Integer sectionId, @Param("url") String url);

    void insertProject(Project project);

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
