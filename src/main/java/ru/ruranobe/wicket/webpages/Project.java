package ru.ruranobe.wicket.webpages;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.*;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.UpdatesMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.tables.ExternalResource;
import ru.ruranobe.mybatis.tables.ProjectInfo;
import ru.ruranobe.mybatis.tables.Update;
import ru.ruranobe.mybatis.tables.Volume;
import ru.ruranobe.wicket.RuraConstants;
import ru.ruranobe.wicket.webpages.base.RuraHeaderAndFooter;

public class Project extends RuraHeaderAndFooter
{
    public Project(PageParameters parameters)
    {
        if (parameters.getNamedKeys().size() != 1)
        {
            throw REDIRECT_TO_404;
        }

        String projectUrl = parameters.getNamedKeys().iterator().next();
        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        
        ProjectsMapper projectsMapperCacheable = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
        Collection<ru.ruranobe.mybatis.tables.Project> projects = projectsMapperCacheable.getProjectsByUrl(projectUrl);
        
        ru.ruranobe.mybatis.tables.Project mainProject = null;
        for (ru.ruranobe.mybatis.tables.Project project : projects)
        {
            if (project.getParentId() == null)
            {
                mainProject = project;
                break;
            }
        }
        
        if (mainProject == null)
        {
            throw REDIRECT_TO_404;
        }
        
        VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
        ProjectInfo projectInfo = volumesMapperCacheable.getInfoByProjectId(mainProject.getProjectId());
        
        Label projectTitle = new Label("projectTitle", mainProject.getTitle());
        add(projectTitle);
        
        ExternalResourcesMapper externalResourcesMapperCacheable = CachingFacade.
                getCacheableMapper(session, ExternalResourcesMapper.class);
        ExternalResource imageResource = externalResourcesMapperCacheable.getExternalResourceById(mainProject.getImageId());
        Image projectMainImage = (imageResource == null) ? (new Image("projectMainImage", new PackageResourceReference(HomePage.class,"undefined.png")))
                                                         : (new Image("projectMainImage", imageResource.getUrl()));
        add(projectMainImage);
        
        Label projectName = new Label("projectName", mainProject.getTitle());
        add(projectName);
        
        Label projectAuthor = new Label("projectAuthor", projectInfo.getAuthor());
        add(projectAuthor);
        
        Label projectIllustrator = new Label("projectIllustrator", projectInfo.getIllustrator());
        add(projectIllustrator);
        
        Label projectFranchise = new Label("projectFranchise", mainProject.getFranchise());
        projectFranchise.setEscapeModelStrings(false);
        add(projectFranchise);
        
        Label projectAnnotation = new Label("projectAnnotation", mainProject.getAnnotation());
        add(projectAnnotation);
        
        final Map<String, ArrayList<Volume>> volumeTypeToVolumes = new HashMap<String, ArrayList<Volume>>();        
        for (ru.ruranobe.mybatis.tables.Project project : projects)
        {
            Collection<Volume> volumes = volumesMapperCacheable.getVolumesByProjectId(project.getProjectId());
            for (Volume volume : volumes)
            {
                String type = volume.getVolumeType();
                if (volumeTypeToVolumes.get(type) == null)
                {
                    volumeTypeToVolumes.put(type, Lists.newArrayList(volume));
                }
                else
                {
                    volumeTypeToVolumes.get(type).add(volume);
                }
            }
        }
        
        ListView<String> volumeTypeRepeater = new ListView<String> ("volumeTypeRepeater", DISPLAYABLE_NAMES)
        {
            @Override
            protected void populateItem(final ListItem <String> listItem)
            {
                String displayableName = listItem.getModelObject();
                ArrayList<Volume> volumes = new ArrayList<Volume>();
                ArrayList<String> volumeTypes = DISPLAYABLE_NAME_TO_VOLUME_TYPES.get(displayableName);
                if (volumeTypes != null)
                {
                    for (String volumeType : volumeTypes)
                    {
                        if (volumeTypeToVolumes.get(volumeType) != null)
                        {
                            volumes.addAll(volumeTypeToVolumes.get(volumeType));
                        }
                    }
                }
                
                Collections.sort(volumes, COMPARATOR);
                
                Label volumeType = new Label("volumeType", displayableName);
                listItem.add(volumeType);
                
                ListView<Volume> volumeRepeater = new ListView<Volume>("volumeRepeater", volumes)
                {

                    @Override
                    protected void populateItem(final ListItem <Volume> listItem)
                    {
                        Volume volume = listItem.getModelObject();
                        String name = volume.getNameShort();
                        Label volumeName = new Label("volumeName", name);
                        listItem.add(volumeName);
                        ExternalLink volumeLink = new ExternalLink("volumeLink", "/"+volume.getUrl(), volume.getNameRu());
                        listItem.add(volumeLink);
                    }
                    
                };
                listItem.add(volumeRepeater);
            }
        };
        add(volumeTypeRepeater);
        
        Collection<ru.ruranobe.mybatis.tables.Project> allProjects = projectsMapperCacheable.getAllProjects();
        List<ProjectExtended> projectsList = new ArrayList<ProjectExtended>();
        
        int count = 0;
        for (ru.ruranobe.mybatis.tables.Project project : allProjects)
        {
            if (!project.isProjectHidden())
            {
                ExternalResource image = (project.getImageId() != null) ? externalResourcesMapperCacheable.getExternalResourceById(project.getImageId())
                                                                        : null;
                projectsList.add(new ProjectExtended(project, image));
                count++;
            }
        }
        
        ListView<ProjectExtended> projectsView = new ListView<ProjectExtended> ("projectsView", projectsList)
        {
            @Override
            protected void populateItem(final ListItem <ProjectExtended> listItem)
            {
                ProjectExtended projectExtended = listItem.getModelObject();
                ExternalResource imageResource = projectExtended.getExternalResource();
                final ru.ruranobe.mybatis.tables.Project project = projectExtended.getProject();
                
                ExternalLink projectLink = new ExternalLink("projectLink", "/"+project.getUrl());
                Image projectImage = (imageResource == null) ? (new Image("projectImage", new PackageResourceReference(HomePage.class,"undefined.png")))
                                                             : (new Image("projectImage", imageResource.getUrl()));
                projectImage.add(new Behavior() 
                {
                    @Override
                    public void onComponentTag(Component component, ComponentTag tag) 
                    {
                        tag.put("alt", project.getTitle());
                        tag.put("width", 220);
                        tag.put("height", 73);
                    }
                });
                projectLink.add(projectImage);
                listItem.add(projectLink);
            }
        };
        add(projectsView);
        
        UpdatesMapper updatesMapperCacheable = session.getMapper(UpdatesMapper.class);
        List<Update> updates = updatesMapperCacheable.
                getLastUpdatesBy(mainProject.getProjectId(), null, null, 0, UPDATES_BY_PROJECT_ON_PAGE);
        
        ListView<Update> updatesView = new ListView<Update> ("updatesView", updates)
        {
            @Override
            protected void populateItem(ListItem<Update> listItem)
            {
                Update update = listItem.getModelObject();
                String iconDivClassValue = UPDATE_TYPE_TO_ICON_DIV_CLASS.get(update.getUpdateType());
                WebMarkupContainer iconDivClass = new WebMarkupContainer("iconDivClass");
                iconDivClass.add(new AttributeAppender("class", Model.of(iconDivClassValue)));
                ExternalLink updateLink = new ExternalLink("updateLink", "/"+update.getChapterUrl(), update.getVolumeTitle());
                if (update.getChapterId() == null)
                {
                    updateLink = new ExternalLink("updateLink", "/"+update.getVolumeUrl(), update.getVolumeTitle());                    
                }
                iconDivClass.add(updateLink);
                listItem.add(iconDivClass);
            }
        };
        add(updatesView);
        
        final int projectId = mainProject.getProjectId();
        StatelessLink moreUpdates = new StatelessLink("moreUpdates")
        {

            @Override
            public void onClick()
            {
                PageParameters p = new PageParameters();
                p.add("project", projectId);
                setResponsePage(Updates.class,p);
            }
        };
        add(moreUpdates);
        
        session.close();
    }
    
    private static class VolumesComparator implements Comparator<Volume>
    {
        
        @Override
        public int compare(Volume volume1, Volume volume2)
        {
            int projectComparison = volume1.getProjectId().compareTo(volume2.getProjectId());
            if (projectComparison != 0)
            {
                return projectComparison;
            }
            
            return ((volume1.getOrderNumber() == null) ? 0 : volume1.getOrderNumber().compareTo(volume2.getOrderNumber()));
        }
        
    }
    
    private static final RedirectToUrlException REDIRECT_TO_404 = new RedirectToUrlException("http://404");
    private static final ArrayList<String> DISPLAYABLE_NAMES = Lists.newArrayList("Ранобэ", "Побочные истории", "Авторские додзинси");
    private static final Map<String, ArrayList<String>> DISPLAYABLE_NAME_TO_VOLUME_TYPES = new HashMap<String, ArrayList<String>>();
    static 
    {
        DISPLAYABLE_NAME_TO_VOLUME_TYPES.put(DISPLAYABLE_NAMES.get(0), Lists.newArrayList(RuraConstants.VOLUME_TYPE_RANOBE));
        DISPLAYABLE_NAME_TO_VOLUME_TYPES.put(DISPLAYABLE_NAMES.get(1), Lists.newArrayList(RuraConstants.VOLUME_TYPE_SIDE_STORY));
        DISPLAYABLE_NAME_TO_VOLUME_TYPES.put(DISPLAYABLE_NAMES.get(2), 
                Lists.newArrayList(
                    RuraConstants.VOLUME_TYPE_DOUJINSHI, 
                    RuraConstants.VOLUME_TYPE_DOUJINSHI_SIDE_STORY,
                    RuraConstants.VOLUME_TYPE_MATERIALS)
                );
    }
    private static final VolumesComparator COMPARATOR = new VolumesComparator();
    private static final int UPDATES_BY_PROJECT_ON_PAGE = 3;
    private static final Map<String, String> UPDATE_TYPE_TO_ICON_DIV_CLASS = 
            new ImmutableMap.Builder<String, String>()
            .put(RuraConstants.UPDATE_TYPE_TRANSLATE, "background-image:url(img/updIcons/icon4.png)")
            .put(RuraConstants.UPDATE_TYPE_IMAGES, "background-image:url(img/updIcons/icon5.png)")
            .put(RuraConstants.UPDATE_TYPE_PROOFREAD, "background-image:url(img/updIcons/icon3.png)")
            .put(RuraConstants.UPDATE_TYPE_OTHER, "background-image:url(img/updIcons/icon1.png)")
            .build();
}
