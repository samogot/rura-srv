package ru.ruranobe.wicket.webpages;

import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.tables.Project;
import ru.ruranobe.mybatis.tables.Volume;
import ru.ruranobe.wicket.components.admin.AdminAffixedListPanel;
import ru.ruranobe.wicket.components.admin.AdminInfoFormPanel;
import ru.ruranobe.wicket.components.admin.formitems.ProjectInfoPanel;
import ru.ruranobe.wicket.components.admin.formitems.SubProjectSelectorItemPanel;
import ru.ruranobe.wicket.webpages.base.AdminLayoutPage;

import java.util.*;

public class ProjectEdit extends AdminLayoutPage
{

    private Project getProject(final PageParameters parameters)
    {
        String projectUrl = parameters.get("project").toOptionalString();
        SqlSession session = MybatisUtil.getSessionFactory().openSession();
        try
        {
            return CachingFacade.getCacheableMapper(session, ProjectsMapper.class).getProjectByUrl(projectUrl);
        }
        finally
        {
            session.close();
        }
    }


    public ProjectEdit(final PageParameters parameters)
    {
        project = getProject(parameters);

        if (project == null)
        {
            throw RuranobeUtils.REDIRECT_TO_404;
        }

        SqlSession session = MybatisUtil.getSessionFactory().openSession();
        try
        {
            ExternalResourcesMapper externalResourcesMapperCacheable = CachingFacade.getCacheableMapper(session, ExternalResourcesMapper.class);
            VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
            if (project.getImageId() != null)
            {
                project.setImage(externalResourcesMapperCacheable.getExternalResourceById(project.getImageId()));
            }

            subProjects = CachingFacade.getCacheableMapper(session, ProjectsMapper.class).getSubProjectsByParentProjectId(project.getProjectId());
            allProjects = new ArrayList<Project>();
            volumes = new ArrayList<Volume>();
            allProjects.add(project);
            allProjects.addAll(subProjects);
            for (Project project : allProjects)
            {
                List<Volume> volumesByProjectId = volumesMapperCacheable.getVolumesByProjectId(project.getProjectId());
                for (Volume volume : volumesByProjectId)
                {
                    volume.setProject(project);
                }
                volumes.addAll(volumesByProjectId);
            }
        }
        finally
        {
            session.close();
        }


        final Comparator<Project> projectComparator = new Comparator<Project>()
        {
            @Override
            public int compare(Project o1, Project o2)
            {
                if (o1.getParentId() == null && o2.getParentId() != null)
                {
                    return -1;
                }
                else if (o2.getParentId() == null && o1.getParentId() != null)
                {
                    return 1;
                }
                else
                {
                    return o1.getOrderNumber() - o2.getOrderNumber();
                }
            }
        };
        Collections.sort(subProjects, projectComparator);
        Collections.sort(allProjects, projectComparator);
        Collections.sort(volumes, new Comparator<Volume>()
        {
            @Override
            public int compare(Volume o1, Volume o2)
            {
                int compProj = projectComparator.compare(o1.getProject(), o2.getProject());
                if (compProj == 0)
                {
                    if (o1.getSequenceNumber() == null && o2.getSequenceNumber() == null)
                    {
                        return 0;
                    }
                    else if (o1.getSequenceNumber() == null)
                    {
                        return 1;
                    }
                    else if (o2.getSequenceNumber() == null)
                    {
                        return -1;
                    }
                    else
                    {
                        return o1.getSequenceNumber() - o2.getSequenceNumber();
                    }
                }
                else
                {
                    return compProj;
                }
            }
        });


        add(new AdminInfoFormPanel<Project>("info", "Информация", new CompoundPropertyModel<Project>(project))
        {
            @Override
            public void onSubmit()
            {
                SqlSession session = MybatisUtil.getSessionFactory().openSession();
                try
                {
                    ProjectsMapper mapper = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
                    mapper.updateProject(project);
                    session.commit();
                }
                finally
                {
                    session.close();
                }
            }

            @Override
            protected Component getContentItemLabelComponent(String id, IModel<Project> model)
            {
                return new ProjectInfoPanel(id, model);
            }
        });

        add(new AdminAffixedListPanel<Project>("subprojects", "Подсерии", new ListModel<Project>(subProjects))
        {
            @Override
            public void onSubmit()
            {
                SqlSession session = MybatisUtil.getSessionFactory().openSession();
                try
                {
                    ProjectsMapper mapper = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
                    for (Project item : model.getObject())
                    {
                        if (!removed.contains(item))
                        {
                            if (item.getProjectId() != null)
                            {
                                mapper.updateProject(item);
                            }
                            else
                            {
                                mapper.insertProject(item);
                            }
                        }
                    }
                    for (Project removedItem : removed)
                    {
                        if (removedItem.getProjectId() != null)
                        {
                            mapper.deleteProject(removedItem.getProjectId());
                        }
                    }
                    session.commit();
                }
                finally
                {
                    session.close();
                }
            }

            @Override
            protected Project makeItem()
            {
                Project new_project = new Project();
                new_project.setParentId(project.getProjectId());
                new_project.setBannerHidden(true);
                new_project.setProjectHidden(true);
                return new_project;
            }

            @Override
            protected Component getSelectorItemLabelComponent(String id, IModel<Project> model)
            {
                return new SubProjectSelectorItemPanel(id, model);
            }

            @Override
            protected Component getFormItemLabelComponent(String id, IModel<Project> model)
            {
                return new WebMarkupContainer(id, model);
            }

            @Override
            protected void onInitialize()
            {
                super.onInitialize();
                form.get("selectorBlock").add(new AttributeModifier("class", "col-xs-12 list-group select sortable"));
                form.get("formBlock").add(new AttributeModifier("class", "hidden-lg admin-affix"));
            }
        }.setSortable(true));
    }

    private static final long serialVersionUID = 1L;
    private final List<Project> subProjects;
    private final List<Project> allProjects;
    private final List<Volume> volumes;
    private final Map<Integer, Volume> volumeTableOrderNumberToVolume = new HashMap<Integer, Volume>();
    private final Map<Integer, Project> projectIdToProject = new HashMap<Integer, Project>();
    private final Project project;
    private final Set<Integer> deletedVolumeIds = new HashSet<Integer>();
}
