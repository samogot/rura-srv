package ru.ruranobe.wicket.components;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.tables.ExternalResource;
import ru.ruranobe.mybatis.tables.Project;
import ru.ruranobe.wicket.webpages.ProjectExtended;

import java.util.*;

/**
 * Created by Samogot on 03.05.2015.
 */
public class ProjectsSidebarModule extends Panel
{

    public ProjectsSidebarModule(String id)
    {
        super(id);
    }

    @Override
    protected void onInitialize()
    {
        super.onInitialize();

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        ProjectsMapper projectsMapperCacheable = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
        Collection<Project> projects = projectsMapperCacheable.getAllProjects();
        List<ProjectExtended> projectsList = new ArrayList<ProjectExtended>();
        ExternalResourcesMapper externalResourcesMapperCacheable = CachingFacade.
                getCacheableMapper(session, ExternalResourcesMapper.class);
        for (Project project : projects)
        {
            if (!project.isProjectHidden() && !project.isBannerHidden())
            {
                ExternalResource image = externalResourcesMapperCacheable.getExternalResourceById(project.getImageId());
                projectsList.add(new ProjectExtended(project, image));
            }
        }
        Collections.sort(projectsList, new Comparator<ProjectExtended>()
        {
            @Override
            public int compare(ProjectExtended o1, ProjectExtended o2)
            {
                return o1.getProject().getOrderNumber() - o2.getProject().getOrderNumber();
            }
        });

        ListView<ProjectExtended> bannersList = new ListView<ProjectExtended>("bannersList", projectsList)
        {
            @Override
            protected void populateItem(final ListItem<ProjectExtended> listItem)
            {
                ProjectExtended projectExtended = listItem.getModelObject();
                final ExternalResource imageResource = projectExtended.getExternalResource();
                final Project project = projectExtended.getProject();

                ExternalLink bannerLink = new ExternalLink("bannerLink", "/project/" + project.getUrl());
                WebMarkupContainer bannerImage = new WebMarkupContainer("bannerImage");
                if(imageResource != null) bannerImage.add(new AttributeModifier("src", imageResource.getUrl()));
                bannerImage.add(new AttributeModifier("alt", project.getTitle()));
                bannerLink.add(new AttributeModifier("title", project.getTitle()));
                bannerLink.add(bannerImage);
                listItem.add(bannerLink);
            }
        };
        add(bannersList);
    }
}
