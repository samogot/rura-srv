package ru.ruranobe.wicket.resources.rest;

import org.apache.ibatis.session.SqlSession;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.ResourcePath;
import org.wicketstuff.rest.annotations.parameters.RequestParam;
import org.wicketstuff.rest.annotations.parameters.ValidatorKey;
import org.wicketstuff.rest.contenthandling.json.webserialdeserial.JsonWebSerialDeserial;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.mybatis.entities.tables.Update;
import ru.ruranobe.mybatis.entities.tables.Volume;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.UpdatesMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.LoginSession;
import ru.ruranobe.wicket.RuraConstants;
import ru.ruranobe.wicket.resources.rest.base.FieldFilteringUtils;
import ru.ruranobe.wicket.resources.rest.base.GsonObjectRestResource;
import ru.ruranobe.wicket.resources.rest.base.ProperifingUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@ResourcePath("/api/projects")
public class ProjectsRestWebService extends GsonObjectRestResource
{
	// TODO: исправить, как станет понятен механизм работы тут
/*    @MethodMapping("")
    public Collection<Project> getProjects(@RequestParam(value = "fields", required = false, defaultValue = DEFAULT_PROJECT_LIST_FIELDS)
                                           @ValidatorKey("project_fields_validator") String fieldsString,
                                           @RequestParam(value = "show_hidden", required = false, defaultValue = "false") boolean showHidden)
    {
        HashSet<String> fields = FieldFilteringUtils.parseFieldsList(fieldsString);
        HashSet<String> imageFields = FieldFilteringUtils.getImageFields(fields);
        if (!imageFields.isEmpty())
        {
            fields.add("image");
        }
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            ProjectsMapper projectsMapper = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
            ExternalResourcesMapper externalResourcesMapper = CachingFacade.getCacheableMapper(session, ExternalResourcesMapper.class);

            Collection<Project> projects = projectsMapper.getRootProjects(1);
            projects.removeIf(project -> (!LoginSession.get().isProjectEditAllowedByUser(project.getUrl()) || !showHidden)
                                         && project.getProjectHidden());
            for (Project project : projects)
            {
                ProperifingUtils.properifyProject(project, fields, imageFields, projectsMapper, externalResourcesMapper);
            }
            return projects;
        }
    }

    @MethodMapping("/{projectId}")
    public Project getProject(Integer projectId,
                              @RequestParam(value = "fields", required = false, defaultValue = DEFAULT_PROJECT_FIELDS)
                              @ValidatorKey("project_fields_validator") String fieldsString)
    {
        HashSet<String> fields = FieldFilteringUtils.parseFieldsList(fieldsString);
        HashSet<String> imageFields = FieldFilteringUtils.getImageFields(fields);
        if (!imageFields.isEmpty())
        {
            fields.add("image");
        }
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            ProjectsMapper projectsMapper = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
            Project project = projectsMapper.getProjectById(1, projectId);
            if (project == null)
            {
                throw getNotFoundException();
            }
            if (project.getProjectHidden() && !LoginSession.get().isProjectEditAllowedByUser(project.getUrl()))
            {
                throw getUnauthorizedException();
            }
            ProperifingUtils.properifyProject(project, fields, imageFields, projectsMapper, CachingFacade.getCacheableMapper(session, ExternalResourcesMapper.class));
            return project;
        }
    }

    @MethodMapping("/{projectId}/subprojects")
    public Collection<Project> getSubProjects(Integer projectId,
                                              @RequestParam(value = "fields", required = false, defaultValue = DEFAULT_SUBPROJECT_FIELDS)
                                              @ValidatorKey("subproject_fields_validator") String fieldsString)
    {
        HashSet<String> fields = FieldFilteringUtils.parseFieldsList(fieldsString);
        HashSet<String> imageFields = FieldFilteringUtils.getImageFields(fields);
        if (!imageFields.isEmpty())
        {
            fields.add("image");
        }
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            ProjectsMapper projectsMapper = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
            ExternalResourcesMapper externalResourcesMapper = CachingFacade.getCacheableMapper(session, ExternalResourcesMapper.class);

            Project parentProject = projectsMapper.getProjectById(1, projectId);
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
                ProperifingUtils.properifyProject(project, fields, imageFields, projectsMapper, externalResourcesMapper);
            }
            return projects;
        }
    }

    @MethodMapping("/{projectId}/volumes")
    public Collection<Volume> getVolumes(Integer projectId,
                                         @RequestParam(value = "fields", required = false, defaultValue = DEFAULT_VOLUME_LIST_FIELDS)
                                         @ValidatorKey("volume_fields_validator") String fieldsString,
                                         @RequestParam(value = "subprojects", required = false, defaultValue = "true") boolean subprojects,
                                         @RequestParam(value = "show_hidden", required = false, defaultValue = "false") boolean showHidden)
    {
        HashSet<String> fields = FieldFilteringUtils.parseFieldsList(fieldsString);
        HashSet<String> imageFields = FieldFilteringUtils.getImageFields(fields);
        if (!imageFields.isEmpty())
        {
            fields.add("covers");
        }
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            ProjectsMapper projectsMapper = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
            ExternalResourcesMapper externalResourcesMapper = CachingFacade.getCacheableMapper(session, ExternalResourcesMapper.class);
            VolumesMapper volumesMapper = CachingFacade.getCacheableMapper(session, VolumesMapper.class);

            Project parentProject = projectsMapper.getProjectById(1, projectId);
            if (parentProject == null)
            {
                throw getNotFoundException();
            }
            if (parentProject.getProjectHidden() && !LoginSession.get().isProjectEditAllowedByUser(parentProject.getUrl()))
            {
                throw getUnauthorizedException();
            }
            List<Project> projects = new ArrayList<>();
            projects.add(parentProject);
            if (subprojects)
            {
                projects.addAll(projectsMapper.getSubProjectsByParentProjectId(projectId));
            }
            ArrayList<Volume> volumes = new ArrayList<>();
            for (Project project : projects)
            {
                volumes.addAll(volumesMapper.getVolumesByProjectId(project.getProjectId()));
            }
            volumes.removeIf(volume -> (!LoginSession.get().isProjectEditAllowedByUser(parentProject.getUrl()) || !showHidden)
                                       && volume.getVolumeStatus().equals(RuraConstants.VOLUME_STATUS_HIDDEN));
            for (Volume volume : volumes)
            {
                ProperifingUtils.properifyVolume(volume, fields, imageFields, volumesMapper, externalResourcesMapper);
            }
            return volumes;
        }
    }

    @MethodMapping("/{projectId}/updates")
    public Collection<Update> getUpdates(Integer projectId,
                                         @RequestParam(value = "fields", required = false, defaultValue = DEFAULT_UPDATE_FIELDS)
                                         @ValidatorKey("update_fields_validator") String fieldsString,
                                         @RequestParam(value = "page", required = false, defaultValue = "1")
                                         @ValidatorKey("page_validator") int page,
                                         @RequestParam(value = "limit", required = false, defaultValue = "20")
                                         @ValidatorKey("limit_validator") int limit)
    {
        HashSet<String> fields = FieldFilteringUtils.parseFieldsList(fieldsString);
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            ProjectsMapper projectsMapper = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
            UpdatesMapper updatesMapper = CachingFacade.getCacheableMapper(session, UpdatesMapper.class);


            Project parentProject = projectsMapper.getProjectById(1, projectId);
            if (parentProject == null)
            {
                throw getNotFoundException();
            }
            if (parentProject.getProjectHidden() && !LoginSession.get().isProjectEditAllowedByUser(parentProject.getUrl()))
            {
                throw getUnauthorizedException();
            }
            List<Update> updates = updatesMapper.getLastUpdatesBy(projectId, null, null, (page - 1) * limit, page * limit);
            for (Update update : updates)
            {
                FieldFilteringUtils.filterAllowedFields(update, fields);
            }
            return updates;
        }
    }

    @Override
    protected void onInitialize(JsonWebSerialDeserial objSerialDeserial)
    {
        super.onInitialize(objSerialDeserial);
        registerValidator("project_fields_validator", ProperifingUtils.ALLOWED_PROJECT_FIELD_VALIDATOR);
        registerValidator("subproject_fields_validator", ProperifingUtils.ALLOWED_SUBPROJECT_FIELD_VALIDATOR);
        registerValidator("volume_fields_validator", ProperifingUtils.ALLOWED_VOLUMES_FIELD_VALIDATOR);
        registerValidator("update_fields_validator", ProperifingUtils.ALLOWED_UPDATES_FIELD_VALIDATOR);
        registerValidator("limit_validator", ProperifingUtils.RESULT_LIMIT_VALIDATOR);
        registerValidator("page_validator", ProperifingUtils.RESULT_PAGE_VALIDATOR);

    }
    private static final String DEFAULT_PROJECT_LIST_FIELDS = "projectId|imageUrl|title|url|orderNumber";
    private static final String DEFAULT_PROJECT_FIELDS = "title|nameJp|nameEn|nameRu|nameRomaji|author|illustrator|originalDesign|originalStory|onevolume";
    private static final String DEFAULT_SUBPROJECT_FIELDS = "projectId|title";
    private static final String DEFAULT_VOLUME_LIST_FIELDS = "volumeId|url|projectId|nameTitle|nameShort|volumeStatus";
    private static final String DEFAULT_UPDATE_FIELDS = "volumeId|chapterId|updateType|showTime";*/
}