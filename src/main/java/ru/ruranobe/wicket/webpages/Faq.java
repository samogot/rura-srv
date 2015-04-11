package ru.ruranobe.wicket.webpages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.resource.PackageResourceReference;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.tables.ExternalResource;
import ru.ruranobe.mybatis.tables.Project;
import ru.ruranobe.wicket.webpages.base.RuraHeaderAndFooter;

public class Faq extends RuraHeaderAndFooter
{
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
        
        int count = 0;
        for (Project project : projects)
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
                final Project project = projectExtended.getProject();
                
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
    }
    
}
