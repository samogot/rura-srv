package ru.ruranobe.wicket.webpages;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;
import ru.ruranobe.mybatis.mappers.VolumeReleaseActivitiesMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.tables.*;
import ru.ruranobe.wicket.components.CoverCarousel;
import ru.ruranobe.wicket.components.LabelHideableOnNull;
import ru.ruranobe.wicket.components.sidebar.FriendsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ProjectsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.UpdatesSidebarModule;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;


public class VolumePage extends SidebarLayoutPage
{
    private static final RedirectToUrlException REDIRECT_TO_404 = new RedirectToUrlException("http://404");

    public VolumePage(PageParameters parameters)
    {
        setStatelessHint(true);
        final String projectUrlValue = parameters.get("project").toString();
        String volumeShortUrl = parameters.get("volume").toString();
        if (volumeShortUrl == null || projectUrlValue == null)
        {
            throw REDIRECT_TO_404;
        }

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

        setDefaultModel(new CompoundPropertyModel<Volume>(volume));

        BookmarkablePageLink prevUrl = new BookmarkablePageLink("prevUrl", VolumePage.class, volume.getPrevUrlParameters());
        prevUrl.add(new Label("prevNameShort"));
        prevUrl.setVisible(volume.getPrevUrl() != null);
        add(prevUrl);
        BookmarkablePageLink nextUrl = new BookmarkablePageLink("nextUrl", VolumePage.class, volume.getNextUrlParameters());
        nextUrl.add(new Label("nextNameShort"));
        nextUrl.setVisible(volume.getNextUrl() != null);
        add(nextUrl);

        ExternalResourcesMapper externalResourcesMapperCacheable = CachingFacade.
                getCacheableMapper(session, ExternalResourcesMapper.class);
        ExternalResource volumeCover;
        List<SimpleEntry<String, String>> covers = new ArrayList<SimpleEntry<String, String>>();
        volumeCover = externalResourcesMapperCacheable.getExternalResourceById(volume.getImageOne());
        if (volumeCover != null) covers.add(new SimpleEntry<String, String>("", volumeCover.getUrl()));
        volumeCover = externalResourcesMapperCacheable.getExternalResourceById(volume.getImageTwo());
        if (volumeCover != null) covers.add(new SimpleEntry<String, String>("", volumeCover.getUrl()));
        volumeCover = externalResourcesMapperCacheable.getExternalResourceById(volume.getImageThree());
        if (volumeCover != null) covers.add(new SimpleEntry<String, String>("", volumeCover.getUrl()));
        volumeCover = externalResourcesMapperCacheable.getExternalResourceById(volume.getImageFour());
        if (volumeCover != null) covers.add(new SimpleEntry<String, String>("", volumeCover.getUrl()));
        add(new CoverCarousel("volumeCoverCarousel", covers));

        BookmarkablePageLink projectUrl = new BookmarkablePageLink("projectUrl", ProjectPage.class, Project.makeUrlParameters(projectUrlValue));
        projectUrl.add(new Label("subProjectName"));
        add(projectUrl);

        add(new Label("nameTitle"));
        add(new LabelHideableOnNull("nameJp"));
        add(new LabelHideableOnNull("nameRomaji"));
        add(new LabelHideableOnNull("nameEn"));
        add(new LabelHideableOnNull("nameRu"));
        add(new LabelHideableOnNull("author"));
        add(new LabelHideableOnNull("illustrator"));
        add(new LabelHideableOnNull("originalDesign"));
        add(new LabelHideableOnNull("releaseDate"));
        add(new LabelHideableOnNull("fullStatus"));
        add(new LabelHideableOnNull("volumeStatusHint"));
        Label annotationParsed;
        add(annotationParsed = new LabelHideableOnNull("annotationParsed"));
        annotationParsed.setEscapeModelStrings(false);

        ExternalLink isbn = new ExternalLink("isbn",
                "http://www.amazon.co.jp/s?search-alias=stripbooks&language=en_JP&field-isbn=" + volume.getIsbn(), volume.getIsbn());
        isbn.setVisible(volume.getIsbn() != null);
        add(isbn);

        VolumeReleaseActivitiesMapper volumeReleaseActivitiesMapperCacheable =
                CachingFacade.getCacheableMapper(session, VolumeReleaseActivitiesMapper.class);
        List<VolumeReleaseActivity> volumeReleaseActivities = new ArrayList<VolumeReleaseActivity>(
                volumeReleaseActivitiesMapperCacheable.getVolumeReleaseActivitiesByVolumeId(volume.getVolumeId()));

        add(new ListView<VolumeReleaseActivity>("volumeReleaseActivitiesView", volumeReleaseActivities)
        {
            @Override
            protected void populateItem(ListItem<VolumeReleaseActivity> item)
            {
                item.setDefaultModel(new CompoundPropertyModel<VolumeReleaseActivity>(item.getModelObject()));
                item.add(new Label("activityName"));
                item.add(new Label("memberName"));
            }

            @Override
            public boolean isVisible()
            {
                return !getModelObject().isEmpty();
            }
        });

        AbstractLink readAllLink;
        if (volume.isStatusExternal() && volume.getExternalUrl() != null)
            readAllLink = new ExternalLink("readAllLink", volume.getExternalUrl());
        else
            readAllLink = new BookmarkablePageLink("readAllLink", VolumeTextPage.class, volume.getFullTextUrlParameters());
        add(readAllLink);

        ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
        List<Chapter> chapters = chaptersMapperCacheable.getChaptersByVolumeId(volume.getVolumeId());
        ListView<Chapter> chaptersView = new ListView<Chapter>("chaptersView", chapters)
        {
            @Override
            protected void populateItem(ListItem<Chapter> item)
            {
                Chapter chapter = item.getModelObject();
                WebMarkupContainer chapterLink;
                if (chapter.isPublished())
                    chapterLink = new BookmarkablePageLink("chapterLink", VolumeTextPage.class, chapter.getUrlParameters());
                else chapterLink = new WebMarkupContainer("chapterLink");
                if (chapter.isNested()) chapterLink.add(new AttributeAppender("class", " nested"));
                chapterLink.add(new Label("chapterName", chapter.getTitle()));
                item.add(chapterLink);
            }
        };
        add(chaptersView);

/*        UpdatesMapper updatesMapperCacheable = session.getMapper(UpdatesMapper.class);
        List<Update> updates = updatesMapperCacheable.
                getLastUpdatesBy(null, volume.getVolumeId(), null, 0, UPDATES_BY_VOLUME_ON_PAGE);

        ListView<Update> updatesView = new ListView<Update>("updatesView", updates)
        {
            @Override
            protected void populateItem(ListItem<Update> listItem)
            {
                Update update = listItem.getModelObject();
                String iconDivClassValue = UPDATE_TYPE_TO_ICON_DIV_CLASS.get(update.getUpdateType());
                WebMarkupContainer iconDivClass = new WebMarkupContainer("iconDivClass");
                iconDivClass.add(new AttributeAppender("class", Model.of(iconDivClassValue)));
                ExternalLink updateLink = new ExternalLink("updateLink", "/" + update.getChapterUrl(), update.getVolumeTitle());
                if (update.getChapterId() == null)
                {
                    updateLink = new ExternalLink("updateLink", "/" + update.getVolumeUrl(), update.getVolumeTitle());
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

        ListView<ProjectExtended> projectsView = new ListView<ProjectExtended>("projectsView", projectsList)
        {
            @Override
            protected void populateItem(final ListItem<ProjectExtended> listItem)
            {
                ProjectExtended projectExtended = listItem.getModelObject();
                ExternalResource imageResource = projectExtended.getExternalResource();
                final ru.ruranobe.mybatis.tables.Project project = projectExtended.getProject();

                ExternalLink projectLink = new ExternalLink("projectLink", "/" + project.getUrl());
                Image projectImage = (imageResource == null) ? (new Image("projectImage", new PackageResourceReference(HomePage.class, "undefined.png")))
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
        add(moreUpdates);*/

//        session.close();
        sidebarModules.add(new UpdatesSidebarModule("sidebarModule", volume.getProjectId()));
        sidebarModules.add(new ProjectsSidebarModule("sidebarModule"));
        sidebarModules.add(new FriendsSidebarModule("sidebarModule"));
    }

    private class VolumeReleaseActivityExtended implements Serializable
    {
        private String assigneeTeamMember;
        private String activityName;

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
    }
}
