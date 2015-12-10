package ru.ruranobe.wicket.webpages;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.entities.tables.ExternalResource;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.mybatis.entities.tables.ProjectInfo;
import ru.ruranobe.wicket.RuraConstants;
import ru.ruranobe.wicket.webpages.base.BaseLayoutPage;

import java.util.*;


public class FullProjects extends BaseLayoutPage
{
    @Override
    protected void onInitialize()
    {
        super.onInitialize();
        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        try
        {
            ProjectsMapper projectsMapperCacheable = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
            Collection<ru.ruranobe.mybatis.entities.tables.Project> projects = projectsMapperCacheable.getAllProjects();
            List<ProjectExtendedWithInfo> projectsList = new ArrayList<ProjectExtendedWithInfo>();
            ExternalResourcesMapper externalResourcesMapperCacheable = CachingFacade.
                                                                                            getCacheableMapper(session, ExternalResourcesMapper.class);
            VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);

            for (ru.ruranobe.mybatis.entities.tables.Project project : projects)
            {
                if (!project.isProjectHidden())
                {
                    ExternalResource image = (project.getImageId() != null) ? externalResourcesMapperCacheable.getExternalResourceById(project.getImageId())
                                                                            : null;
                    ProjectInfo projectInfo = volumesMapperCacheable.getInfoByProjectId(project.getProjectId());
                    projectsList.add(new ProjectExtendedWithInfo(project, image, projectInfo));
                }
            }
            Collections.sort(projectsList, new Comparator<ProjectExtendedWithInfo>()
            {
                @Override
                public int compare(ProjectExtendedWithInfo o1, ProjectExtendedWithInfo o2)
                {
                    return o1.getProject().getOrderNumber() - o2.getProject().getOrderNumber();
                }
            });

            ListView<ProjectExtendedWithInfo> viewTypeOne = new ListView<ProjectExtendedWithInfo>("viewTypeOne", projectsList)
            {
                @Override
                protected void populateItem(final ListItem<ProjectExtendedWithInfo> listItem)
                {
                    ProjectExtendedWithInfo projectExtended = listItem.getModelObject();
                    ExternalResource imageResource = projectExtended.getExternalResource();
                    final Project project = projectExtended.getProject();

                    BookmarkablePageLink linkOneViewTypeOne = project.makeBookmarkablePageLink("linkOneViewTypeOne");
                    WebMarkupContainer imageViewTypeOne = new WebMarkupContainer("imageViewTypeOne");
                    if (imageResource != null)
                    {
                        imageViewTypeOne.add(new AttributeModifier("src", imageResource.getUrl()));
                    }
                    imageViewTypeOne.add(new AttributeModifier("title", project.getTitle()));
                    linkOneViewTypeOne.add(imageViewTypeOne);
                    listItem.add(linkOneViewTypeOne);

                    BookmarkablePageLink linkTwoViewTypeOne = project.makeBookmarkablePageLink("linkTwoViewTypeOne");
                    Label titleViewTypeOne = new Label("titleViewTypeOne", project.getTitle());
                    linkTwoViewTypeOne.add(titleViewTypeOne);
                    listItem.add(linkTwoViewTypeOne);

                    ProjectInfo projectInfo = projectExtended.projectInfo;
                    Label authorViewTypeOne = new Label("authorViewTypeOne", (projectInfo == null) ? "unknown" : projectInfo.getAuthor());
                    listItem.add(authorViewTypeOne);

                    Label volumeCountViewTypeOne = new Label("volumeCountViewTypeOne", (projectInfo == null) ? 0 : projectInfo.getVolumesCount());
                    listItem.add(volumeCountViewTypeOne);

                    WebMarkupContainer statusViewTypeOne = new WebMarkupContainer("statusViewTypeOne");
                    String classStatusViewTypeOne = (RuraConstants.VOLUME_STATUS_DONE.equals((projectInfo == null) ? "unknown" : projectInfo.getVolumeStatus()))
                                                    ? "stateRed"
                                                    : "stateGreen";
                    AttributeAppender appender = new AttributeAppender("class", classStatusViewTypeOne);
                    statusViewTypeOne.add(appender);

                    String status = (RuraConstants.VOLUME_STATUS_DONE.equals((projectInfo == null) ? "unknown" : projectInfo.getVolumeStatus()))
                                    ? "Окончен"
                                    : "Выпускается";
                    Label statusViewTypeOneText = new Label("statusViewTypeOneText", status);
                    statusViewTypeOne.add(statusViewTypeOneText);
                    listItem.add(statusViewTypeOne);

                    BookmarkablePageLink linkThreeViewTypeOne = project.makeBookmarkablePageLink("linkThreeViewTypeOne");
                    listItem.add(linkThreeViewTypeOne);
                }
            };
            add(viewTypeOne);

            ListView<ProjectExtendedWithInfo> viewTypeTwo = new ListView<ProjectExtendedWithInfo>("viewTypeTwo", projectsList)
            {
                @Override
                protected void populateItem(final ListItem<ProjectExtendedWithInfo> listItem)
                {
                    ProjectExtendedWithInfo projectExtended = listItem.getModelObject();
                    ExternalResource imageResource = projectExtended.getExternalResource();
                    final Project project = projectExtended.getProject();

                    BookmarkablePageLink linkOneViewTypeTwo = project.makeBookmarkablePageLink("linkOneViewTypeTwo");
                    WebMarkupContainer imageViewTypeTwo = new WebMarkupContainer("imageViewTypeTwo");
                    if (imageResource != null)
                    {
                        imageViewTypeTwo.add(new AttributeModifier("src", imageResource.getUrl()));
                    }
                    imageViewTypeTwo.add(new AttributeModifier("title", project.getTitle()));
                    linkOneViewTypeTwo.add(imageViewTypeTwo);
                    listItem.add(linkOneViewTypeTwo);

                    BookmarkablePageLink linkTwoViewTypeTwo = project.makeBookmarkablePageLink("linkTwoViewTypeTwo");
                    Label titleViewTypeTwo = new Label("titleViewTypeTwo", project.getTitle());
                    linkTwoViewTypeTwo.add(titleViewTypeTwo);
                    listItem.add(linkTwoViewTypeTwo);

                    ProjectInfo projectInfo = projectExtended.projectInfo;
                    Label authorViewTypeTwo = new Label("authorViewTypeTwo", (projectInfo == null) ? "unknown" : projectInfo.getAuthor());
                    listItem.add(authorViewTypeTwo);

                    Label illustratorViewTypeTwo = new Label("illustratorViewTypeTwo", (projectInfo == null) ? "unknown" : projectInfo.getIllustrator());
                    listItem.add(illustratorViewTypeTwo);

                    Label volumeCountViewTypeTwo = new Label("volumeCountViewTypeTwo", (projectInfo == null) ? "unknown" : projectInfo.getVolumesCount());
                    listItem.add(volumeCountViewTypeTwo);

    //                BookmarkablePageLink linkThreeViewTypeTwo = project.makeBookmarkablePageLink("linkThreeViewTypeTwo");
    //                listItem.add(linkThreeViewTypeTwo);
                }
            };
            add(viewTypeTwo);
        }
        finally
        {
            session.close();
        }
    }

    private class ProjectExtendedWithInfo
    {
        private final Project project;
        private final ExternalResource imageResource;
        private final ProjectInfo projectInfo;

        public ProjectExtendedWithInfo(Project project, ExternalResource image, ProjectInfo projectInfo)
        {
            this.project = project;
            this.imageResource = image;
            this.projectInfo = projectInfo;
        }

        public ExternalResource getExternalResource()
        {
            return imageResource;
        }

        public Project getProject()
        {
            return project;
        }

        public ProjectInfo getProjectInfo()
        {
            return projectInfo;
        }
    }
}
