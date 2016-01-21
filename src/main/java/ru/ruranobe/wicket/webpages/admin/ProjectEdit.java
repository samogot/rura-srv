package ru.ruranobe.wicket.webpages.admin;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.markup.html.form.select.IOptionRenderer;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOptions;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.AbstractRepeater;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.mybatis.entities.tables.Volume;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.RuraConstants;
import ru.ruranobe.wicket.components.admin.*;
import ru.ruranobe.wicket.webpages.base.AdminLayoutPage;

import java.util.*;

@AuthorizeInstantiation({"ADMIN", "TEAM MEMBER"})
public class ProjectEdit extends AdminLayoutPage
{

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

    public ProjectEdit(final PageParameters parameters)
    {
        project = getProject(parameters);

        redirectTo404IfArgumentIsNull(project);

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


        Collections.sort(subProjects, PROJECT_COMPARATOR);
        Collections.sort(allProjects, PROJECT_COMPARATOR);
        Collections.sort(volumes, VOLUME_COMPARATOR);

        add(new Label("breadcrumbActive", project.getTitle()));
        add(new AdminInfoFormPanel<Project>("info", "Информация", new CompoundPropertyModel<>(project))
        {
            @Override
            public boolean onSubmit()
            {
                try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
                {
                    ProjectsMapper mapper = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
                    mapper.updateProject(project);
                    session.commit();
                }
                return true;
            }

            @Override
            protected Component getContentItemLabelComponent(String id, final IModel<Project> model)
            {
                return new Fragment(id, "projectInfoFragment", ProjectEdit.this, model)
                {
                    @Override
                    protected void onInitialize()
                    {
                        super.onInitialize();
                        add(new TextField<String>("url").setRequired(true).setLabel(Model.of("Ссылка")));
                        add(new BannerUploadComponent("image").setProject(model.getObject()));
                        add(new TextField<String>("title").setRequired(true).setLabel(Model.of("Заголовок")));
                        add(new TextField<String>("nameJp"));
                        add(new TextField<String>("nameEn"));
                        add(new TextField<String>("nameRu"));
                        add(new TextField<String>("nameRomaji"));
                        add(new TextField<String>("author"));
                        add(new TextField<String>("illustrator"));
                        add(new TextField<String>("originalDesign"));
                        add(new TextField<String>("originalStory"));
                        add(new CheckBox("onevolume"));
                        add(new CheckBox("projectHidden"));
                        add(new CheckBox("bannerHidden"));
                        add(new TextField<String>("issueStatus"));
                        add(new TextField<String>("translationStatus"));
                        add(new DropDownChoice<>("status", RuraConstants.PROJECT_STATUS_LIST));
                        add(new TextArea<String>("franchise"));
                        add(new TextArea<String>("annotation"));
                        add(new NumberTextField<Integer>("forumId").setMinimum(1));
                    }
                };
            }
        });

        add(new AdminTableListPanel<Volume>("volumes", "Все тома", new ListModel<>(volumes), VOLUMES_TABLE_COLUMNS)
        {
            @Override
            public boolean onSubmit()
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
                return true;
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
                            new_volume.setImageOne(null);
                            new_volume.setImageTwo(null);
                            new_volume.setImageThree(null);
                            new_volume.setImageFour(null);
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
            protected Component getRowComponent(String id, final IModel<Volume> model)
            {
                return new Fragment(id, "volumeTableRowFragment", ProjectEdit.this, model)
                {
                    @Override
                    protected void onInitialize()
                    {
                        super.onInitialize();
                        add(new TextField<String>("urlPart").setRequired(true).setLabel(Model.of("Ссылка")));
                        add(new TextField<String>("nameFile").setRequired(true).setLabel(Model.of("Имя для файлов")));
                        add(new TextField<String>("nameTitle").setRequired(true).setLabel(Model.of("Заголовок")));
                        add(new TextField<String>("nameJp"));
                        add(new TextField<String>("nameEn"));
                        add(new TextField<String>("nameRu"));
                        add(new TextField<String>("nameRomaji"));
                        add(new TextField<String>("nameShort"));
                        add(new DropDownChoice<>("project", allProjects).setChoiceRenderer(new ChoiceRenderer<Project>("title", "projectId"))
                                                                        .setOutputMarkupId(true));
                        add(new TextField<Float>("sequenceNumber"));
                        add(new TextField<String>("author"));
                        add(new TextField<String>("illustrator"));
                        add(new DateTextField("releaseDate", "dd.MM.yyyy"));
                        add(new TextField<String>("isbn"));
                        add(new DropDownChoice<>("volumeType", RuraConstants.VOLUME_TYPE_LIST));
                        add(new Select<String>("volumeStatus")
                                .add(new SelectOptions<>("basic", RuraConstants.VOLUME_STATUS_BASIC_LIST, STATUS_OPTION_RENDERER))
                                .add(new SelectOptions<>("external", RuraConstants.VOLUME_STATUS_EXTERNAL_LIST, STATUS_OPTION_RENDERER))
                                .add(new SelectOptions<>("not_in_work", RuraConstants.VOLUME_STATUS_IN_WORK_LIST, STATUS_OPTION_RENDERER))
                                .add(new SelectOptions<>("in_work", RuraConstants.VOLUME_STATUS_NOT_IN_WORK_LIST, STATUS_OPTION_RENDERER))
                                .add(new SelectOptions<>("published", RuraConstants.VOLUME_STATUS_PUBLISHED_LIST, STATUS_OPTION_RENDERER)));
                        add(new TextField<String>("externalUrl"));
                        add(new TextArea<String>("annotation"));
                        add(new CheckBox("adult"));
                        add(new BookmarkablePageLink("link", VolumeEdit.class)
                        {
                            @Override
                            public PageParameters getPageParameters()
                            {
                                return model.getObject().getUrlParameters();
                            }

                            @Override
                            public boolean isVisible()
                            {
                                return !Strings.isEmpty(model.getObject().getUrlPart());
                            }
                        });
                    }
                };
            }

        });

        add(new AdminAffixedListPanel<Project>("subprojects", "Подсерии", new ListModel<>(subProjects))
        {
            @Override
            public boolean onSubmit()
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
                return true;
            }

            @Override
            protected void onAjaxSubmit(AjaxRequestTarget target)
            {
                super.onAjaxProcess(target);
                reinitAllProjects();
                Collections.sort(allProjects, PROJECT_COMPARATOR);
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
                new_project.setOnevolume(false);
                new_project.setStatus(RuraConstants.PROJECT_STATUS_LIST.get(0));
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

        }.setSortable(true));
    }

    private static final List<String> VOLUMES_TABLE_COLUMNS = Arrays.asList("Ссылка", "Имя для файлов", "Заголовок",
            "Название (ориг.)", "Название (англ.)", "Название (рус.)", "Название (романдзи)", "Короткое название",
            "Серия", "Номер в серии", "Автор", "Иллюстратор", "Дата публикации", "ISBN", "Тип релиза", "Cтатус релиза",
            "Внешняя ссылка", "Аннотация", "18+", "Править");

    private static final Comparator<Project> PROJECT_COMPARATOR = (o1, o2) -> {
        int parentComp = ObjectUtils.compare(o1.getParentId(), o2.getParentId(), false);
        return parentComp == 0 ? ObjectUtils.compare(o1.getOrderNumber(), o2.getOrderNumber(), true) : parentComp;
    };

    private static final Comparator<Volume> VOLUME_COMPARATOR = (o1, o2) -> {
        int compProj = PROJECT_COMPARATOR.compare(o1.getProject(), o2.getProject());
        return compProj == 0 ? ObjectUtils.compare(o1.getSequenceNumber(), o2.getSequenceNumber(), true) : compProj;
    };

    private static final IOptionRenderer<String> STATUS_OPTION_RENDERER = new IOptionRenderer<String>()
    {
        @Override
        public String getDisplayValue(String object)
        {
            return RuraConstants.VOLUME_STATUS_TO_FULL_TEXT.get(object);
        }

        @Override
        public IModel<String> getModel(String value)
        {
            return Model.of(value);
        }
    };

    private final List<Project> subProjects;
    private final List<Project> allProjects;
    private final List<Volume> volumes;
    private final Project project;
}
