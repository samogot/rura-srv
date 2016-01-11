package ru.ruranobe.wicket.webpages;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.AbstractRepeater;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.mybatis.entities.tables.Volume;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.RuraConstants;
import ru.ruranobe.wicket.components.admin.AdminAffixedListPanel;
import ru.ruranobe.wicket.components.admin.AdminInfoFormPanel;
import ru.ruranobe.wicket.components.admin.AdminTableListPanel;
import ru.ruranobe.wicket.components.admin.AdminToolboxAjaxButton;
import ru.ruranobe.wicket.components.admin.formitems.ProjectInfoPanel;
import ru.ruranobe.wicket.components.admin.formitems.VolumeTableRowPanel;
import ru.ruranobe.wicket.webpages.base.AdminLayoutPage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@AuthorizeInstantiation("ADMIN")
public class ProjectEdit extends AdminLayoutPage
{

    private final List<Project> subProjects;
    private final List<Project> allProjects;
    private final List<Volume> volumes;
    private final Project project;
    private static final List<String> VOLUMES_TABLE_COLUMNS = new ImmutableList.Builder<String>()
            .add("Ссылка")
            .add("Имя для файлов")
            .add("Заголовок")
            .add("Название (ориг.)")
            .add("Название (англ.)")
            .add("Название (рус.)")
            .add("Название (романдзи)")
            .add("Короткое название")
            .add("Серия")
            .add("Номер в серии")
            .add("Автор")
            .add("Иллюстратор")
            .add("Дата публикации")
            .add("ISBN")
            .add("Тип релиза")
            .add("Cтатус релиза")
            .add("Внешняя ссылка")
            .add("Аннотация")
            .add("18+")
            .add("Править")
            .build();
    private static final Comparator<Project> projectComparator = new Comparator<Project>()
    {
        @Override
        public int compare(Project o1, Project o2)
        {
            int parentComp = ObjectUtils.compare(o1.getParentId(), o2.getParentId(), false);
            return parentComp == 0 ? ObjectUtils.compare(o1.getOrderNumber(), o2.getOrderNumber(), true) : parentComp;
        }
    };
    private static final Comparator<Volume> volumeComparator = new Comparator<Volume>()
    {
        @Override
        public int compare(Volume o1, Volume o2)
        {
            int compProj = projectComparator.compare(o1.getProject(), o2.getProject());
            return compProj == 0 ? ObjectUtils.compare(o1.getSequenceNumber(), o2.getSequenceNumber(), true) : compProj;
        }
    };

    public ProjectEdit(final PageParameters parameters)
    {
        project = getProject(parameters);

        if (project == null)
        {
            throw RuranobeUtils.getRedirectTo404Exception(this);
        }

        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            ExternalResourcesMapper externalResourcesMapperCacheable = CachingFacade.getCacheableMapper(session, ExternalResourcesMapper.class);
            VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
            if (project.getImageId() != null)
            {
                project.setImage(externalResourcesMapperCacheable.getExternalResourceById(project.getImageId()));
            }

            subProjects = CachingFacade.getCacheableMapper(session, ProjectsMapper.class).getSubProjectsByParentProjectId(project.getProjectId());
            allProjects = new ArrayList<>();
            volumes = new ArrayList<>();
            reinitAllProjects();
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


        Collections.sort(subProjects, projectComparator);
        Collections.sort(allProjects, projectComparator);
        Collections.sort(volumes, volumeComparator);

        add(new Label("breadcrumbActive", project.getTitle()));
        add(new AdminInfoFormPanel<Project>("info", "Информация", new CompoundPropertyModel<>(project))
        {
            @Override
            public void onSubmit()
            {
                try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
                {
                    ProjectsMapper mapper = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
                    mapper.updateProject(project);
                    session.commit();
                }
            }

            @Override
            protected Component getContentItemLabelComponent(String id, IModel<Project> model)
            {
                return new ProjectInfoPanel(id, model);
            }
        });

        add(new AdminTableListPanel<Volume>("volumes", "Все тома", new ListModel<>(volumes), VOLUMES_TABLE_COLUMNS)
        {
            @Override
            public void onSubmit()
            {
                try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
                {
                    VolumesMapper mapper = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
                    for (Volume item : model.getObject())
                    {
                        if (!removed.contains(item))
                        {
                            if (item.getVolumeId() != null)
                            {
                                mapper.updateVolume(item);
                            }
                            else
                            {
                                mapper.insertVolume(item);
                            }
                        }
                    }
                    for (Volume removedItem : removed)
                    {
                        if (removedItem.getVolumeId() != null)
                        {
                            mapper.deleteVolume(removedItem.getVolumeId());
                        }
                    }
                    session.commit();
                }
            }

            @Override
            protected void onInitialize()
            {
                super.onInitialize();
                toolbarButtons.add(1, new AdminToolboxAjaxButton("Дублировать", "warning", "files-o", form)
                {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                    {
                        if (selectedItem == null)
                        {
                            target.appendJavaScript("alert('Ничего не выбрано!');");
                        }
                        else
                        {
                            Volume new_volume = SerializationUtils.clone(selectedItem);
                            new_volume.setProject(selectedItem.getProject());
                            new_volume.setVolumeId(null);
                            model.getObject().add(new_volume);
                            onAddItem(new_volume, target, form);
                        }
                    }
                }.setSelectableOnly());
            }

            @Override
            protected Volume makeItem()
            {
                Volume new_volume = new Volume();
                new_volume.setVolumeType(RuraConstants.VOLUME_TYPE_RANOBE);
                new_volume.setVolumeStatus(RuraConstants.VOLUME_STATUS_QUEUE);
                new_volume.setProject(project);
                new_volume.setUrl(project.getUrl() + "/");
                return new_volume;
            }

            @Override
            protected Component getRowComponent(String id, IModel<Volume> model)
            {
                return new VolumeTableRowPanel(id, model, allProjects);
            }

        });

        add(new AdminAffixedListPanel<Project>("subprojects", "Подсерии", new ListModel<>(subProjects))
        {
            @Override
            public void onSubmit()
            {
                try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
                {
                    ProjectsMapper mapper = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
                    VolumesMapper volumeMapper = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
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
                    for (Volume volume : volumes)
                    {
                        if (removed.contains(volume.getProject()))
                        {
                            volume.setProject(project);
                            if (volume.getProjectId() != null)
                            {
                                volumeMapper.updateVolume(volume);
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
            }

            @Override
            protected void onAjaxProcess(AjaxRequestTarget target)
            {
                super.onAjaxProcess(target);
                reinitAllProjects();
                Collections.sort(allProjects, projectComparator);
                for (Component component : ((AbstractRepeater) ProjectEdit.this.get("volumes:form:rowRepeater")))
                {
                    target.add(component.get("item:project"));
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
                return new Label(id, new PropertyModel<Project>(model, "title"));
            }

            @Override
            protected Component getFormItemLabelComponent(String id, IModel<Project> model)
            {
                return new Fragment(id, "subProjectFormItemFragment", ProjectEdit.this, model)
                {
                    @Override
                    protected void onInitialize()
                    {
                        super.onInitialize();
                        add(new TextField<String>("title").setRequired(true).setLabel(Model.of("Название")));
                        add(new NumberTextField<Integer>("forumId").setMinimum(1));
                    }
                };
            }

//            @Override
//            protected void onInitialize()
//            {
//                super.onInitialize();
//                form.get("selectorBlock").add(new AttributeModifier("class", "col-xs-12 list-group select sortable"));
//                form.get("formBlock").add(new AttributeModifier("class", "hidden-lg admin-affix"));
//            }
        }.setSortable(true));
    }

    private Project getProject(final PageParameters parameters)
    {
        String projectUrl = parameters.get("project").toOptionalString();
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            return CachingFacade.getCacheableMapper(session, ProjectsMapper.class).getProjectByUrl(projectUrl);
        }
    }

    private void reinitAllProjects()
    {
        allProjects.clear();
        allProjects.add(project);
        allProjects.addAll(subProjects);
    }
}
