package ru.ruranobe.wicket.webpages;

import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
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
import ru.ruranobe.mybatis.mappers.*;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.tables.*;
import ru.ruranobe.wicket.RuraConstants;
import ru.ruranobe.wicket.webpages.base.RuraHeaderAndFooter;

public class Release extends RuraHeaderAndFooter
{
    public Release(PageParameters parameters)
    {
        if (parameters.getNamedKeys().size() != 1)
        {
            throw REDIRECT_TO_404;
        }
        
        final String projectUrlValue = parameters.getNamedKeys().iterator().next();
        String volumeShortUrl = parameters.get(projectUrlValue).toString();
        
        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        
        VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
        String volumeUrl = projectUrlValue + "/" + volumeShortUrl;
        final Volume volume = volumesMapperCacheable.getVolumeNextPrevByUrl(volumeUrl); 
        
        if (volume == null)
        {
            session.close();
            throw REDIRECT_TO_404;
        }

        Label volumeTitle = new Label("volumeTitle", volume.getNameTitle());
        add(volumeTitle);
        
        ExternalLink prevUrl = new ExternalLink("prevUrl", "/release/"+
                ((volume.getPrevUrl() == null) ? "null" : volume.getPrevUrl()));
        Label prevShortName = new Label("prevShortName", 
                ((volume.getPrevNameShort() == null) ? "null" : volume.getPrevNameShort()));
        prevUrl.add(prevShortName);
        
        prevUrl.setVisible(volume.getPrevUrl()!=null);
        add(prevUrl);
        
        ExternalLink nextUrl = new ExternalLink("nextUrl", "/release/"+
                ((volume.getNextUrl() == null) ? "null" : volume.getNextUrl()));
        Label nextShortName = new Label("nextShortName", 
                ((volume.getNextNameShort() == null) ? "null" : volume.getNextNameShort()));
        nextUrl.add(nextShortName);
        
        nextUrl.setVisible(volume.getNextUrl()!=null);
        add(nextUrl);
        
        ExternalResourcesMapper externalResourcesMapperCacheable = CachingFacade.
                getCacheableMapper(session, ExternalResourcesMapper.class);
        ExternalResource volumeCover = externalResourcesMapperCacheable.getExternalResourceById(volume.getImageOne());
        Image volumeImg = (volumeCover == null) ? (new Image("volumeImg", new PackageResourceReference(HomePage.class,"undefined.png")))
                                                : (new Image("volumeImg", volumeCover.getUrl()));
        volumeImg.add(new Behavior() 
        {
            @Override
            public void onComponentTag(Component component, ComponentTag tag) 
            {
                tag.put("alt", volume.getNameRu());
            }
        });
        add(volumeImg);
        
        StatelessLink projectUrl = new StatelessLink("projectUrl") 
        {
            @Override
            public void onClick()
            {
                PageParameters projectLink = new PageParameters();
                projectLink.add(projectUrlValue, null);
                setResponsePage(Project.class, projectLink);
            }
        };
        add(projectUrl);
        
        Label volumeName = new Label("volumeName", volume.getNameTitle());
        add(volumeName);
        
        Label volumeAuthor = new Label("volumeAuthor", volume.getAuthor());
        add(volumeAuthor);
        
        Label volumeIllustrator = new Label("volumeIllustrator", volume.getIllustrator());
        add(volumeIllustrator);
        
        ExternalLink volumeIsbn = new ExternalLink("volumeIsbn",
                "http://www.amazon.co.jp/s?search-alias=stripbooks&language=en_JP&field-isbn=" + volume.getIsbn(),
                volume.getIsbn());
        add(volumeIsbn);
        
        Label volumeStatus = new Label("volumeStatus", volume.getVolumeStatus());
        add(volumeStatus);
        
        VolumeReleaseActivitiesMapper volumeReleaseActivitiesMapperCacheable = 
                CachingFacade.getCacheableMapper(session, VolumeReleaseActivitiesMapper.class);
        Collection<VolumeReleaseActivity> volumeReleaseActivities = 
                volumeReleaseActivitiesMapperCacheable.getVolumeReleaseActivitiesByVolumeId(volume.getVolumeId());
        VolumeActivitiesMapper volumeActivitiesMapperCacheable = 
                CachingFacade.getCacheableMapper(session, VolumeActivitiesMapper.class);
        
        List<VolumeReleaseActivityExtended> releaseActivitiesList = new ArrayList<VolumeReleaseActivityExtended>();
        for (VolumeReleaseActivity volumeReleaseActivity: volumeReleaseActivities)
        {
            releaseActivitiesList.add(new VolumeReleaseActivityExtended(volumeReleaseActivity.getAssigneeTeamMember(), 
                    volumeActivitiesMapperCacheable.getVolumeActivityById(volumeReleaseActivity.getActivityId()).toString()));
        }
        
        ListView<VolumeReleaseActivityExtended> volumeReleaseActivitiesView = 
                new ListView<VolumeReleaseActivityExtended>("volumeReleaseActivitiesView", releaseActivitiesList)
        {

            @Override
            protected void populateItem(ListItem<VolumeReleaseActivityExtended> item)
            {
                VolumeReleaseActivityExtended activity = item.getModelObject();
                Label activityName = new Label("activityName", activity.getActivityName());
                Label teamMember = new Label("teamMember", activity.getAssigneeTeamMember());
                item.add(activityName);
                item.add(teamMember);
            }    
        };
        add(volumeReleaseActivitiesView);
        
        Label volumeAnnotation = new Label("volumeAnnotation", volume.getAnnotation());
        add(volumeAnnotation);
        
        ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
        List<Chapter> chapters = chaptersMapperCacheable.getChaptersByVolumeId(volume.getVolumeId());
        ListView <Chapter> chaptersView = new ListView<Chapter>("chaptersView", chapters)
        {
            @Override
            protected void populateItem(ListItem<Chapter> item)
            {
                Chapter chapter = item.getModelObject();
                Label chapterName = new Label("chapterName", chapter.getTitle());
                ExternalLink chapterLink = new ExternalLink("chapterLink", chapter.getUrl());
                chapterLink.add(chapterName);
                item.add(chapterLink);
            }
        };
        add(chaptersView);
        
        UpdatesMapper updatesMapperCacheable = session.getMapper(UpdatesMapper.class);
        List<Update> updates = updatesMapperCacheable.
                getLastUpdatesBy(null, volume.getVolumeId(), null, 0, UPDATES_BY_VOLUME_ON_PAGE);
        
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
        
        ProjectsMapper projectsMapperCacheable = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
        Collection<ru.ruranobe.mybatis.tables.Project> projects = projectsMapperCacheable.getAllProjects();
        List<ProjectExtended> projectsList = new ArrayList<ProjectExtended>();
        
        int count = 0;
        for (ru.ruranobe.mybatis.tables.Project project : projects)
        {
            if (!project.isProjectHidden() && !project.isBannerHidden())
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
        
        StatelessLink moreUpdates = new StatelessLink("moreUpdates")
        {

            @Override
            public void onClick()
            {
                PageParameters p = new PageParameters();
                p.add("volume", volume.getVolumeId());
                setResponsePage(Updates.class, p);
            }
        };
        add(moreUpdates);
        
        session.close();
    }
    
    private class VolumeReleaseActivityExtended implements Serializable
    {
        public VolumeReleaseActivityExtended(String assigneeTeamMember, String activityName)
        {
            this.assigneeTeamMember = assigneeTeamMember;
            this.activityName = activityName;
        }

        public String getActivityName()
        {
            return activityName;
        }

        public String getAssigneeTeamMember()
        {
            return assigneeTeamMember;
        }
               
        private String assigneeTeamMember;
        private String activityName;
    }
    
    private static final RedirectToUrlException REDIRECT_TO_404 = new RedirectToUrlException("http://404");
    private static final int UPDATES_BY_VOLUME_ON_PAGE = 3;
    private static final Map<String, String> UPDATE_TYPE_TO_ICON_DIV_CLASS = 
            new ImmutableMap.Builder<String, String>()
            .put(RuraConstants.UPDATE_TYPE_TRANSLATE, "background-image:url(img/updIcons/icon4.png)")
            .put(RuraConstants.UPDATE_TYPE_IMAGES, "background-image:url(img/updIcons/icon5.png)")
            .put(RuraConstants.UPDATE_TYPE_PROOFREAD, "background-image:url(img/updIcons/icon3.png)")
            .put(RuraConstants.UPDATE_TYPE_OTHER, "background-image:url(img/updIcons/icon1.png)")
            .build();
}
