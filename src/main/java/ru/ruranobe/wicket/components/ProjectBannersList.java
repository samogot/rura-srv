package ru.ruranobe.wicket.components;

import javafx.util.Pair;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.tables.ExternalResource;
import ru.ruranobe.mybatis.tables.Project;
import ru.ruranobe.wicket.webpages.ProjectPage;

import java.util.*;

/**
 * Created by Samogot on 04.05.2015.
 */
public class ProjectBannersList extends Panel
{
    private Integer limit;

    public ProjectBannersList(String id, Integer limit)
    {
        super(id);
        this.limit = limit;
    }

    @Override
    protected void onInitialize()
    {
        super.onInitialize();

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        ProjectsMapper projectsMapperCacheable = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
        Collection<Project> projects = projectsMapperCacheable.getAllProjects();
        List<Pair<Project, ExternalResource>> projectsList = new ArrayList<Pair<Project, ExternalResource>>();
        ExternalResourcesMapper externalResourcesMapperCacheable = CachingFacade.
                getCacheableMapper(session, ExternalResourcesMapper.class);
        for (Project project : projects)
        {
            if (!project.isProjectHidden() && !project.isBannerHidden())
            {
                ExternalResource image = externalResourcesMapperCacheable.getExternalResourceById(project.getImageId());
                projectsList.add(new Pair<Project, ExternalResource>(project, image));
            }
        }
        Collections.sort(projectsList, new Comparator<Pair<Project, ExternalResource>>()
        {
            @Override
            public int compare(Pair<Project, ExternalResource> o1, Pair<Project, ExternalResource> o2)
            {
                return o1.getKey().getOrderNumber() - o2.getKey().getOrderNumber();
            }
        });
        if (limit != null) projectsList.subList(limit, projectsList.size()).clear();
        ListView<Pair<Project, ExternalResource>> bannersList = new ListView<Pair<Project, ExternalResource>>("bannersList", projectsList)
        {
            @Override
            protected void populateItem(final ListItem<Pair<Project, ExternalResource>> listItem)
            {
                Pair<Project, ExternalResource> projectExtended = listItem.getModelObject();
                final ExternalResource imageResource = projectExtended.getValue();
                final Project project = projectExtended.getKey();
                BookmarkablePageLink bannerLink = new BookmarkablePageLink("bannerLink", ProjectPage.class, project.getUrlParameters());
                WebMarkupContainer bannerImage = new WebMarkupContainer("bannerImage");
                if (imageResource != null) bannerImage.add(new AttributeModifier("src", imageResource.getUrl()));
                bannerImage.add(new AttributeModifier("alt", project.getTitle()));
                bannerLink.add(new AttributeModifier("title", project.getTitle()));
                bannerLink.add(bannerImage);
                listItem.add(bannerLink);
            }
        };
        add(bannersList);
        add(new AttributeAppender("class", " banners"));
    }
}
