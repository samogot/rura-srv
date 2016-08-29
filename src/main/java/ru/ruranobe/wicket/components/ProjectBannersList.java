package ru.ruranobe.wicket.components;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import ru.ruranobe.cache.Cache;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.ExternalResource;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.mybatis.entities.tables.SectionProject;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.MetaDataKeys;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

		    Integer sectionId = Integer.valueOf(getRequestCycle().getMetaData(MetaDataKeys.DOMAIN));

        List<SectionProject> sectionProjects = Cache.SECTION_PROJECTS.get(sectionId);
        List<Project> projects = sectionProjects.stream().filter(
            sectionProject -> !sectionProject.getBannerHidden() && !sectionProject.getProjectHidden() &&
                              sectionProject.getMain())
            .map(sectionProject -> Cache.PROJECTS.get(sectionProject.getProjectId())).collect(Collectors.toList());

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        try (SqlSession session = sessionFactory.openSession())
        {
            List<SimpleEntry<Project, ExternalResource>> projectsList = new ArrayList<>();
            ExternalResourcesMapper externalResourcesMapperCacheable = CachingFacade.getCacheableMapper(session, ExternalResourcesMapper.class);
            for (Project project : projects)
            {
                ExternalResource image = externalResourcesMapperCacheable.getExternalResourceById(project.getImageId());
	              projectsList.add(new SimpleEntry<>(project, image));
            }
            Collections.sort(projectsList, (o1, o2) -> o1.getKey().getOrderNumber() - o2.getKey().getOrderNumber());
            if (limit != null && projectsList.size() > limit)
            {
                projectsList.subList(limit, projectsList.size()).clear();
            }
            ListView<SimpleEntry<Project, ExternalResource>> bannersList = new ListView<SimpleEntry<Project, ExternalResource>>("bannersList", projectsList)
            {
                @Override
                protected void populateItem(final ListItem<SimpleEntry<Project, ExternalResource>> listItem)
                {
                    SimpleEntry<Project, ExternalResource> projectExtended = listItem.getModelObject();
                    final ExternalResource imageResource = projectExtended.getValue();
                    final Project project = projectExtended.getKey();
                    BookmarkablePageLink bannerLink = project.makeBookmarkablePageLink("bannerLink");
                    WebMarkupContainer bannerImage = new WebMarkupContainer("bannerImage");
                    if (imageResource != null)
                    {
                        bannerImage.add(new AttributeModifier("src", imageResource.getUrl()));
                    }
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
}
