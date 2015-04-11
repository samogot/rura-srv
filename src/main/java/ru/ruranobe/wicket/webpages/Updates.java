package ru.ruranobe.wicket.webpages;

import java.util.*;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.AttributeModifier;
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
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.UpdatesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.tables.ExternalResource;
import ru.ruranobe.mybatis.tables.Update;
import ru.ruranobe.wicket.RuraConstants;
import ru.ruranobe.wicket.components.UpdatesView;
import ru.ruranobe.wicket.webpages.base.RuraHeaderAndFooter;

public class Updates extends RuraHeaderAndFooter
{
    public Updates()
    {
        initComponents(null, 1, null, null);
    }    
    
    public Updates(final PageParameters parameters)
    {
        String searchType = parameters.get("type").toOptionalString();
        searchType = ((!SEARCH_TYPES.contains(searchType)) ? null : searchType); 
        String pageString = parameters.get("page").toOptionalString();
        pageString = ((pageString == null) ? "1" : pageString);
        String projectString = parameters.get("project").toOptionalString();
        String volumeString = parameters.get("volume").toOptionalString();
        int page;
        Integer projectId = null;
        Integer volumeId = null;
        try
        {
            page = Integer.parseInt(pageString);
            projectId = projectString != null ? Integer.parseInt(projectString) : null;
            volumeId = volumeString != null ? Integer.parseInt(volumeString) : null;
        }
        catch (Exception ex)
        {
            page = 1;
        }
        initComponents(searchType, page, volumeId, projectId);
    }   
    
    private void initComponents(final String searchType, final int page, final Integer volumeId, final Integer projectId)
    {
        StatelessLink first = new StatelessLink("first") 
        {
            @Override
            public void onClick() 
            {
                PageParameters p = new PageParameters();
                p.add("page", page);
                if (projectId != null)
                {
                    p.add("project", projectId);
                }
                if (volumeId != null)
                {
                    p.add("volume", volumeId);
                }
                setResponsePage(Updates.class, p);
            }
        };
        StatelessLink third = new StatelessLink("third") 
        {
            @Override
            public void onClick() 
            {
                PageParameters p = new PageParameters();
                p.add("type", RuraConstants.UPDATE_TYPE_PROOFREAD);
                p.add("page", page);
                if (projectId != null)
                {
                    p.add("project", projectId);
                }
                if (volumeId != null)
                {
                    p.add("volume", volumeId);
                }
                setResponsePage(Updates.class, p);
            }
        };
        StatelessLink fourth = new StatelessLink("fourth") 
        {
            @Override
            public void onClick() 
            {
                PageParameters p = new PageParameters();
                p.add("type", RuraConstants.UPDATE_TYPE_TRANSLATE);
                p.add("page", page);
                if (projectId != null)
                {
                    p.add("project", projectId);
                }
                if (volumeId != null)
                {
                    p.add("volume", volumeId);
                }
                setResponsePage(Updates.class, p);
            }
        };
        StatelessLink fifth = new StatelessLink("fifth") 
        {
            @Override
            public void onClick() 
            {
                PageParameters p = new PageParameters();
                p.add("type", RuraConstants.UPDATE_TYPE_IMAGES);
                p.add("page", page);
                if (projectId != null)
                {
                    p.add("project", projectId);
                }
                if (volumeId != null)
                {
                    p.add("volume", volumeId);
                }
                setResponsePage(Updates.class, p);
            }
        };
        
        if (searchType == null)
        {
            AttributeModifier modifier = new AttributeModifier("class", "first active");
            first.add(modifier);
        }
        else if (RuraConstants.UPDATE_TYPE_PROOFREAD.equals(searchType))
        {
            AttributeModifier modifier = new AttributeModifier("class", "third active");
            third.add(modifier);
        }
        else if (RuraConstants.UPDATE_TYPE_TRANSLATE.equals(searchType))
        {
            AttributeModifier modifier = new AttributeModifier("class", "fourth active");
            fourth.add(modifier);
        }
        else if (RuraConstants.UPDATE_TYPE_IMAGES.equals(searchType))
        {
            AttributeModifier modifier = new AttributeModifier("class", "fifth active");
            fifth.add(modifier);
        }
        
        add(first);
        add(third);
        add(fourth);
        add(fifth);
        
        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        UpdatesMapper updatesMapperCacheable = CachingFacade.getCacheableMapper(session, UpdatesMapper.class);
        
        int updatesCount = updatesMapperCacheable.getUpdatesCountBy(projectId, volumeId, searchType);
        
        WebMarkupContainer firstPage = new WebMarkupContainer("firstPage");
        WebMarkupContainer lastPage = new WebMarkupContainer("lastPage");
        final int numberOfPages = (updatesCount / UPDATES_COUNT_ON_PAGE) + (((updatesCount % UPDATES_COUNT_ON_PAGE) == 0) ? 0 : 1);
        StatelessLink firstPageLink = new StatelessLink("firstPageLink") 
        {
            @Override
            public void onClick() 
            {
                PageParameters p = new PageParameters();
                p.add("page", 1);
                if (searchType != null)
                {
                    p.add("type", searchType);
                }
                if (projectId != null)
                {
                    p.add("project", projectId);
                }
                if (volumeId != null)
                {
                    p.add("volume", volumeId);
                }
                setResponsePage(Updates.class, p);
            }
        };
        
        StatelessLink lastPageLink = new StatelessLink("lastPageLink") 
        {
            @Override
            public void onClick() 
            {
                PageParameters p = new PageParameters();
                p.add("page", numberOfPages);
                if (searchType != null)
                {
                    p.add("type", searchType);
                }
                if (projectId != null)
                {
                    p.add("project", projectId);
                }
                if (volumeId != null)
                {
                    p.add("volume", volumeId);
                }
                setResponsePage(Updates.class, p);
            }
        };

        if (page == 1)
        {
            AttributeAppender appender = new AttributeAppender("class", "disabled");
            firstPage.add(appender);
            firstPageLink = new StatelessLink("firstPageLink") 
            {
                @Override
                public void onClick() 
                { }
            };
            firstPageLink.setVisible(false);
        }
        if (page == numberOfPages)
        {
            AttributeAppender appender = new AttributeAppender("class", "disabled");
            lastPage.add(appender);
            lastPageLink = new StatelessLink("lastPageLink") 
            {
                @Override
                public void onClick() 
                { }
            };
            lastPageLink.setVisible(false);
        }

        firstPage.add(firstPageLink);
        lastPage.add(lastPageLink);
        add(firstPage);
        add(lastPage);
        
        List<StatelessLink> references = new ArrayList<StatelessLink>();
        AttributeAppender activeAppender = new AttributeAppender("class", "active");
        if (page <= NUMBER_OF_PAGES_ON_UPDATE_LIST)
        {
            int endLoop = Math.min(NUMBER_OF_PAGES_ON_UPDATE_LIST, numberOfPages);
            for (int i = 1; i <= endLoop; ++i)
            {
                StatelessLink link = new StatelessLinkToPage("updatesPageLink", Integer.toString(i), searchType,"updatesPageText",Integer.toString(i));
                if (i == page)
                {
                    link.add(activeAppender);
                }
                references.add(link);
            }
            if (numberOfPages > NUMBER_OF_PAGES_ON_UPDATE_LIST )
            {
                StatelessLink linkToNextPage = new StatelessLinkToPage("updatesPageLink", Integer.toString(NUMBER_OF_PAGES_ON_UPDATE_LIST+1), searchType,"updatesPageText", "...");
                references.add(linkToNextPage);
                StatelessLink linkToLastPage = new StatelessLinkToPage("updatesPageLink", Integer.toString(numberOfPages), searchType,"updatesPageText", Integer.toString(numberOfPages));
                references.add(linkToLastPage);
            }
        }
        else if (page < numberOfPages - NUMBER_OF_PAGES_ON_UPDATE_LIST )
        {
            StatelessLink linkToFirstPage = new StatelessLinkToPage("updatesPageLink", "1", searchType,"updatesPageText", "1");
            references.add(linkToFirstPage);
            StatelessLink linkToPrevPage = new StatelessLinkToPage("updatesPageLink", Integer.toString(page-NUMBER_OF_PAGES_ON_UPDATE_LIST), searchType,"updatesPageText", "...");
            references.add(linkToPrevPage);
            for (int i = page; i <= page+NUMBER_OF_PAGES_ON_UPDATE_LIST-1; ++i)
            {
                StatelessLink link = new StatelessLinkToPage("updatesPageLink", Integer.toString(i), searchType,"updatesPageText", Integer.toString(i));
                if (i == page)
                {
                    link.add(activeAppender);
                }
                references.add(link);
            }
            StatelessLink linkToNextPage = new StatelessLinkToPage("updatesPageLink", Integer.toString(page+NUMBER_OF_PAGES_ON_UPDATE_LIST), searchType,"updatesPageText", "...");
            references.add(linkToNextPage);
            StatelessLink linkToLastPage = new StatelessLinkToPage("updatesPageLink", Integer.toString(numberOfPages), searchType,"updatesPageText", Integer.toString(numberOfPages));
            references.add(linkToLastPage);
        }
        else
        {
            StatelessLink linkToFirstPage = new StatelessLinkToPage("updatesPageLink", "1", searchType,"updatesPageText", "1");
            references.add(linkToFirstPage);
            StatelessLink linkToPrevPage = new StatelessLinkToPage("updatesPageLink", Integer.toString(page-NUMBER_OF_PAGES_ON_UPDATE_LIST), searchType,"updatesPageText", "...");
            references.add(linkToPrevPage);
            for (int i = page; i <= numberOfPages; ++i)
            {
                StatelessLink link = new StatelessLinkToPage("updatesPageLink", Integer.toString(i), searchType, "updatesPageText", Integer.toString(i));
                if (i == page)
                {
                    link.add(activeAppender);
                }
                references.add(link);
            }
        }
        
        ListView<StatelessLink> updatesPaginator = new ListView<StatelessLink> ("updatesPaginator", references)
        {
            @Override
            protected void populateItem(final ListItem<StatelessLink> listItem)
            {
                listItem.add(listItem.getModelObject());
            }
        };
        add(updatesPaginator);  
        
        List<Update> updates = updatesMapperCacheable.getLastUpdatesBy(projectId, volumeId, searchType, (page-1)*UPDATES_COUNT_ON_PAGE, UPDATES_COUNT_ON_PAGE);
        ListView<Update> updatesView = new UpdatesView("updatesView", updates);
        add(updatesView);
        
        ProjectsMapper projectsMapperCacheable = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
        Collection<ru.ruranobe.mybatis.tables.Project> projects = projectsMapperCacheable.getAllProjects();
        List<ProjectExtended> projectsList = new ArrayList<ProjectExtended>();
        ExternalResourcesMapper externalResourcesMapperCacheable = CachingFacade.
                getCacheableMapper(session, ExternalResourcesMapper.class);
        
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
        session.close();
    }
    
    private static class StatelessLinkToPage extends StatelessLink
    {
        public StatelessLinkToPage(String id, String page, String searchType)
        {
            super(id);
            this.page = page;
            this.searchType = searchType;
        }
        
        public StatelessLinkToPage(String id, String page, String searchType, String textId, String text)
        {
            super(id);
            this.page = page;
            this.searchType = searchType;
            this.add(new Label(textId, text));
        }
        
        @Override
        public void onClick()
        {
            PageParameters p = new PageParameters();
            p.add("page", page);
            if (searchType != null)
            {
                p.add("type", searchType);
            }
            setResponsePage(Updates.class, p);
        }
        
        private final String page;
        private final String searchType;
    }
    
    private static final Set<String> SEARCH_TYPES = new HashSet<String>();
    private static final int UPDATES_COUNT_ON_PAGE = 10;
    private static final int NUMBER_OF_PAGES_ON_UPDATE_LIST = 5;
    static
    {
        SEARCH_TYPES.add(RuraConstants.UPDATE_TYPE_IMAGES);
        SEARCH_TYPES.add(RuraConstants.UPDATE_TYPE_PROOFREAD);
        SEARCH_TYPES.add(RuraConstants.UPDATE_TYPE_TRANSLATE);
    }
}
