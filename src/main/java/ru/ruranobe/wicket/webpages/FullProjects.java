package ru.ruranobe.wicket.webpages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.resource.PackageResourceReference;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.tables.ExternalResource;
import ru.ruranobe.mybatis.tables.Project;
import ru.ruranobe.mybatis.tables.ProjectInfo;
import ru.ruranobe.mybatis.tables.Volume;
import ru.ruranobe.wicket.RuraConstants;
import ru.ruranobe.wicket.webpages.base.RuraHeaderAndFooter;


public class FullProjects extends RuraHeaderAndFooter
{
    @Override
    protected void onInitialize() 		
    {
        super.onInitialize(); 
        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        ProjectsMapper projectsMapperCacheable = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
        Collection<ru.ruranobe.mybatis.tables.Project> projects = projectsMapperCacheable.getAllProjects();
        List<ProjectExtendedWithInfo> projectsList = new ArrayList<ProjectExtendedWithInfo>();
        ExternalResourcesMapper externalResourcesMapperCacheable = CachingFacade.
                getCacheableMapper(session, ExternalResourcesMapper.class);
        VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
        
        for (ru.ruranobe.mybatis.tables.Project project : projects)
        {
            if (!project.isProjectHidden())
            {
                ExternalResource image = (project.getImageId() != null) ? externalResourcesMapperCacheable.getExternalResourceById(project.getImageId())
                                                                        : null;
                ProjectInfo projectInfo = volumesMapperCacheable.getInfoByProjectId(project.getProjectId());
                projectsList.add(new ProjectExtendedWithInfo(project, image, projectInfo));
            }
        }
        
        ListView<ProjectExtendedWithInfo> viewTypeOne = new ListView<ProjectExtendedWithInfo> ("viewTypeOne", projectsList)
        {
            @Override
            protected void populateItem(final ListItem <ProjectExtendedWithInfo> listItem)
            {
                ProjectExtendedWithInfo projectExtended = listItem.getModelObject();
                ExternalResource imageResource = projectExtended.getExternalResource();
                final Project project = projectExtended.getProject();
                
                ExternalLink linkOneViewTypeOne = new ExternalLink("linkOneViewTypeOne", "/"+project.getUrl());
                Image imageViewTypeOne = (imageResource == null) ? (new Image("imageViewTypeOne", new PackageResourceReference(HomePage.class,"undefined.png")))
                                                                 : (new Image("imageViewTypeOne", imageResource.getUrl()));
                imageViewTypeOne.add(new Behavior() 
                {
                    @Override
                    public void onComponentTag(Component component, ComponentTag tag) 
                    {
                        tag.put("alt", project.getTitle());
                        tag.put("width", "100%");
                    }
                });
                linkOneViewTypeOne.add(imageViewTypeOne);
                listItem.add(linkOneViewTypeOne);
                
                ExternalLink linkTwoViewTypeOne = new ExternalLink("linkTwoViewTypeOne", "/"+project.getUrl());
                Label titleViewTypeOne = new Label("titleViewTypeOne", project.getTitle());
                linkTwoViewTypeOne.add(titleViewTypeOne);
                listItem.add(linkTwoViewTypeOne);
                
                ProjectInfo projectInfo = projectExtended.projectInfo;
                Label authorViewTypeOne = new Label("authorViewTypeOne", (projectInfo == null) ? "unknown" : projectInfo.getAuthor());
                listItem.add(authorViewTypeOne);
                
                Label volumeCountViewTypeOne = new Label("volumeCountViewTypeOne", (projectInfo == null) ? 0 :  projectInfo.getVolumesCount());
                listItem.add(volumeCountViewTypeOne);
                
                WebMarkupContainer statusViewTypeOne = new WebMarkupContainer("statusViewTypeOne");
                String classStatusViewTypeOne = (RuraConstants.VOLUME_STATUS_DONE.equals( (projectInfo == null) ? "unknown" :  projectInfo.getVolumeStatus())) 
                        ? "stateRed"
                        : "stateGreen";
                AttributeAppender appender = new AttributeAppender("class", classStatusViewTypeOne);
                statusViewTypeOne.add(appender);
                
                String status = (RuraConstants.VOLUME_STATUS_DONE.equals((projectInfo == null) ? "unknown" :  projectInfo.getVolumeStatus())) 
                        ? "Окончен" 
                        : "Выпускается";
                Label statusViewTypeOneText = new Label("statusViewTypeOneText", status);
                statusViewTypeOne.add(statusViewTypeOneText);
                listItem.add(statusViewTypeOne);
                
                ExternalLink linkThreeViewTypeOne = new ExternalLink("linkThreeViewTypeOne", "/"+project.getUrl());
                listItem.add(linkThreeViewTypeOne);
            }
        };
        add(viewTypeOne);
        
        ListView<ProjectExtendedWithInfo> viewTypeTwo = new ListView<ProjectExtendedWithInfo> ("viewTypeTwo", projectsList)
        {
            @Override
            protected void populateItem(final ListItem <ProjectExtendedWithInfo> listItem)
            {
                ProjectExtendedWithInfo projectExtended = listItem.getModelObject();
                ExternalResource imageResource = projectExtended.getExternalResource();
                final Project project = projectExtended.getProject();
                
                ExternalLink linkOneViewTypeTwo = new ExternalLink("linkOneViewTypeTwo", "/"+project.getUrl());
                Image imageViewTypeTwo = (imageResource == null) ? (new Image("imageViewTypeTwo", new PackageResourceReference(HomePage.class,"undefined.png")))
                                                                 : (new Image("imageViewTypeTwo", imageResource.getUrl()));
                imageViewTypeTwo.add(new Behavior() 
                {
                    @Override
                    public void onComponentTag(Component component, ComponentTag tag) 
                    {
                        tag.put("alt", project.getTitle());
                        tag.put("width", "220px");
                    }
                });
                linkOneViewTypeTwo.add(imageViewTypeTwo);
                listItem.add(linkOneViewTypeTwo);
                
                ExternalLink linkTwoViewTypeTwo = new ExternalLink("linkTwoViewTypeTwo", "/"+project.getUrl());
                Label titleViewTypeTwo = new Label("titleViewTypeTwo", project.getTitle());
                linkTwoViewTypeTwo.add(titleViewTypeTwo);
                listItem.add(linkTwoViewTypeTwo);
                
                ProjectInfo projectInfo = projectExtended.projectInfo;
                Label authorViewTypeTwo = new Label("authorViewTypeTwo", (projectInfo == null) ? "unknown" :  projectInfo.getAuthor());
                listItem.add(authorViewTypeTwo);
                
                Label illustratorViewTypeTwo = new Label("illustratorViewTypeTwo", (projectInfo == null) ? "unknown" :  projectInfo.getIllustrator());
                listItem.add(illustratorViewTypeTwo);
                
                Label volumeCountViewTypeTwo = new Label("volumeCountViewTypeTwo", (projectInfo == null) ? "unknown" :  projectInfo.getVolumesCount());
                listItem.add(volumeCountViewTypeTwo);
                
                ExternalLink linkThreeViewTypeTwo = new ExternalLink("linkThreeViewTypeTwo", "/"+project.getUrl());
                listItem.add(linkThreeViewTypeTwo);
            }
        };
        add(viewTypeTwo);
        
        session.close();
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
