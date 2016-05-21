package ru.ruranobe.wicket.resources.rest;

import org.apache.ibatis.session.SqlSession;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.ResourcePath;
import org.wicketstuff.rest.annotations.parameters.RequestParam;
import org.wicketstuff.rest.annotations.parameters.ValidatorKey;
import org.wicketstuff.rest.contenthandling.json.webserialdeserial.JsonWebSerialDeserial;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.ExternalResource;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.LoginSession;
import ru.ruranobe.wicket.resources.rest.base.FieldFilteringUtils;
import ru.ruranobe.wicket.resources.rest.base.GsonObjectRestResource;
import ru.ruranobe.wicket.validators.AllowedFieldsValidator;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@ResourcePath("/api/projects")
public class ProjectsRestWebService extends GsonObjectRestResource
{
    private static final List<String> ALLOWED_PROJECT_FIELD_LIST = Arrays.asList("projectId",
            "imageWidth", "imageHeight", "imageMimeType", "imageUrl", "imageThumbnail", "imageTitle", "imageUploadedWhen",
            "url", "title", "nameJp", "nameEn", "nameRu", "nameRomaji", "author", "illustrator", "originalDesign",
            "originalStory", "orderNumber", "bannerHidden", "projectHidden", "onevolume", "works", "franchise",
            "annotation", "forumId", "status", "issueStatus", "translationStatus", "lastUpdateDate", "lastEditDate");
    private static final List<String> ALLOWED_SUBPROJECT_FIELD_LIST = Arrays.asList("projectId", "parentId", "title", "forumId");


    @Override
    protected void onInitialize(JsonWebSerialDeserial objSerialDeserial)
    {
        super.onInitialize(objSerialDeserial);
        registerValidator("project_fields_validator", new AllowedFieldsValidator(ALLOWED_PROJECT_FIELD_LIST).setParamName("fields"));
        registerValidator("subproject_fields_validator", new AllowedFieldsValidator(ALLOWED_SUBPROJECT_FIELD_LIST).setParamName("fields"));
    }

    @MethodMapping("")
    public Collection<Project> getProjects(@RequestParam(value = "fields", required = false, defaultValue = "projectId|imageUrl|title|url|orderNumber")
                                           @ValidatorKey("project_fields_validator") String columnsString,
                                           @RequestParam(value = "show_hidden", required = false, defaultValue = "false") boolean showHidden)
    {
        HashSet<String> fields = FieldFilteringUtils.parseFieldsList(columnsString);
        HashSet<String> imageFields = FieldFilteringUtils.getImageFields(fields);
        if (!imageFields.isEmpty())
        {
            fields.add("image");
        }
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            ProjectsMapper projectsMapper = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
            ExternalResourcesMapper externalResourcesMapper = CachingFacade.getCacheableMapper(session, ExternalResourcesMapper.class);

            Collection<Project> projects = projectsMapper.getRootProjects();
            projects.removeIf(project -> (!LoginSession.get().isProjectEditAllowedByUser(project.getUrl()) || !showHidden)
                                         && project.getProjectHidden());
            for (Project project : projects)
            {
                filterProjectFields(project, fields, imageFields, projectsMapper, externalResourcesMapper);
            }
            return projects;
        }
    }

    @MethodMapping("/{projectId}")
    public Project getProject(Integer projectId,
                              @RequestParam(value = "fields", required = false, defaultValue = "title|nameJp|nameEn|nameRu|nameRomaji|author|illustrator|originalDesign|originalStory|onevolume")
                              @ValidatorKey("project_fields_validator") String columnsString)
    {
        HashSet<String> fields = FieldFilteringUtils.parseFieldsList(columnsString);
        HashSet<String> imageFields = FieldFilteringUtils.getImageFields(fields);
        if (!imageFields.isEmpty())
        {
            fields.add("image");
        }
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            ProjectsMapper projectsMapper = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
            Project project = projectsMapper.getProjectById(projectId);
            if (project == null)
            {
                throw getNotFoundException();
            }
            if (project.getProjectHidden() && !LoginSession.get().isProjectEditAllowedByUser(project.getUrl()))
            {
                throw getUnauthorizedException();
            }
            filterProjectFields(project, fields, imageFields, projectsMapper, CachingFacade.getCacheableMapper(session, ExternalResourcesMapper.class));
            return project;
        }
    }

    @MethodMapping("/{projectId}/subprojects")
    public Collection<Project> getSubProjects(Integer projectId,
                                              @RequestParam(value = "fields", required = false, defaultValue = "projectId|title")
                                              @ValidatorKey("subproject_fields_validator") String columnsString)
    {
        HashSet<String> fields = FieldFilteringUtils.parseFieldsList(columnsString);
        HashSet<String> imageFields = FieldFilteringUtils.getImageFields(fields);
        if (!imageFields.isEmpty())
        {
            fields.add("image");
        }
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            ProjectsMapper projectsMapper = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
            ExternalResourcesMapper externalResourcesMapper = CachingFacade.getCacheableMapper(session, ExternalResourcesMapper.class);

            Project parentProject = projectsMapper.getProjectById(projectId);
            if (parentProject == null)
            {
                throw getNotFoundException();
            }
            if (parentProject.getProjectHidden() && !LoginSession.get().isProjectEditAllowedByUser(parentProject.getUrl()))
            {
                throw getUnauthorizedException();
            }
            Collection<Project> projects = projectsMapper.getSubProjectsByParentProjectId(projectId);
            for (Project project : projects)
            {
                filterProjectFields(project, fields, imageFields, projectsMapper, externalResourcesMapper);
            }
            return projects;
        }
    }


    private void filterProjectFields(Project project, HashSet<String> fields, HashSet<String> imageFields, ProjectsMapper projectsMapper, ExternalResourcesMapper externalResourcesMapper)
    {
        if (!imageFields.isEmpty() && project.getImageId() != null)
        {
            ExternalResource image = externalResourcesMapper.getExternalResourceById(project.getImageId());
            FieldFilteringUtils.filterAllowedFields(image, imageFields);
            project.setImage(image);
        }
        if (fields.contains("lastUpdateDate"))
        {
            project.setLastUpdateDate(projectsMapper.getProjectUpdateDate(project.getProjectId()));
        }
        if (fields.contains("lastEditDate"))
        {
            project.setLastEditDate(projectsMapper.getProjectEditDate(project.getProjectId()));
        }
        FieldFilteringUtils.filterAllowedFields(project, fields);
    }
}