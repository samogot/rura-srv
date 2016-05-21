package ru.ruranobe.wicket.webpages.common;

import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.ExternalResource;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.components.sidebar.TwitterWidgetSidebarModule;
import ru.ruranobe.wicket.components.sidebar.VKWidgetSidebarModule;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FullProjects extends SidebarLayoutPage
{
    @Override
    protected void onInitialize()
    {
        super.onInitialize();
        try(SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            List<ProjectInfo> projectsList = new ArrayList<>();
            Collection<Project> projects = CachingFacade.getCacheableMapper(session, ProjectsMapper.class)
                    .getAllProjects();
            ExternalResourcesMapper externalResourcesMapperCacheable = CachingFacade.
                    getCacheableMapper(session, ExternalResourcesMapper.class);
            VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);

            for (Project project : projects)
            {
                if (!project.getProjectHidden() && !project.getWorks())
                {
                    ExternalResource image = (project.getImageId() != null) ? externalResourcesMapperCacheable.getExternalResourceById(project.getImageId())
                                                                            : null;
                    int volumesCount = volumesMapperCacheable.getVolumesCountByProjectId(project.getProjectId());
                    projectsList.add(new ProjectInfo(project, image, volumesCount));
                }
            }

            Collections.sort(projectsList);

            add(new ListView<ProjectInfo>("viewTypeOne", projectsList)
            {
                @Override
                protected void populateItem(final ListItem<ProjectInfo> listItem)
                {
                    ProjectInfo projectInfo = listItem.getModelObject();
                    ExternalResource projectImage = projectInfo.getProjectImage();
                    final Project project = projectInfo.getProject();

                    WebMarkupContainer imageViewTypeOne = new WebMarkupContainer("imageViewTypeOne");
                    imageViewTypeOne.add(new AttributeModifier("title", project.getTitle()));
                    if (projectImage != null)
                    {
                        imageViewTypeOne.add(new AttributeModifier("src", projectImage.getUrl()));
                    }

                    listItem.add(
                            project.makeBookmarkablePageLink("linkOneViewTypeOne").add(imageViewTypeOne)
                    );

                    listItem.add(
                            project.makeBookmarkablePageLink("linkTwoViewTypeOne").
                                    add(new Label("titleViewTypeOne", project.getTitle()))
                    );

                    listItem.add(new Label("authorViewTypeOne", project.getAuthor()));

                    listItem.add(new Label("volumeCountViewTypeOne", projectInfo.getVolumesCount()));

                    String classStatusViewTypeOne = ("Окончен".equals(project.getStatus()))
                                                    ? "stateRed"
                                                    : "stateGreen";
                    listItem.add(
                            new WebMarkupContainer("statusViewTypeOne")
                                    .add(new Label("statusViewTypeOneText", project.getStatus()))
                                    .add(new AttributeAppender("class", classStatusViewTypeOne))
                    );

                    listItem.add(project.makeBookmarkablePageLink("linkThreeViewTypeOne"));
                }
            });

            add(new ListView<ProjectInfo>("viewTypeTwo", projectsList)
            {
                @Override
                protected void populateItem(final ListItem<ProjectInfo> listItem)
                {
                    ProjectInfo projectInfo = listItem.getModelObject();
                    ExternalResource projectImage = projectInfo.getProjectImage();
                    final Project project = projectInfo.getProject();

                    WebMarkupContainer imageViewTypeTwo = new WebMarkupContainer("imageViewTypeTwo");
                    imageViewTypeTwo.add(new AttributeModifier("title", project.getTitle()));
                    if (projectImage != null)
                    {
                        imageViewTypeTwo.add(new AttributeModifier("src", projectImage.getUrl()));
                    }

                    listItem.add(
                            project.makeBookmarkablePageLink("linkOneViewTypeTwo").add(imageViewTypeTwo)
                    );

                    listItem.add(
                            project.makeBookmarkablePageLink("linkTwoViewTypeTwo").
                                    add(new Label("titleViewTypeTwo", project.getTitle()))
                    );

                    listItem.add(new Label("authorViewTypeTwo", project.getAuthor()));

                    listItem.add(new Label("illustratorViewTypeTwo", project.getIllustrator()));

                    listItem.add(new Label("volumeCountViewTypeTwo", projectInfo.getVolumesCount()));
                }
            });
        }
        sidebarModules.add(new VKWidgetSidebarModule());
        sidebarModules.add(new TwitterWidgetSidebarModule());
    }

	@Override
	protected String getPageTitle() {
		return "Проекты - РуРанобэ";
	}

    private class ProjectInfo implements Comparable<ProjectInfo>
    {
        private final Project project;
        private final ExternalResource projectImage;
        private final int volumesCount;

        public ProjectInfo(Project project, ExternalResource image, int volumesCount)
        {
            this.project = project;
            this.projectImage = image;
            this.volumesCount = volumesCount;
        }

        @Override
        public int compareTo(@Nonnull ProjectInfo o)
        {
            return getProject().getOrderNumber() - o.getProject().getOrderNumber();
        }

        public ExternalResource getProjectImage()
        {
            return projectImage;
        }

        public Project getProject()
        {
            return project;
        }

        public int getVolumesCount()
        {
            return volumesCount;
        }
    }
}
