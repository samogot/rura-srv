package ru.ruranobe.wicket.webpages;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.markup.html.form.select.IOptionRenderer;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOptions;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.*;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.tables.*;
import ru.ruranobe.wicket.RuraConstants;
import ru.ruranobe.wicket.components.admin.AdminAffixedListPanel;
import ru.ruranobe.wicket.components.admin.AdminInfoFormPanel;
import ru.ruranobe.wicket.webpages.base.AdminLayoutPage;

import java.util.*;

public class VolumeEdit extends AdminLayoutPage
{

    private void reinitAllChapters()
    {
        allChapters.clear();
        allChapters.addAll(chapters);
        allChapters.add(stubСhapter);
        Collections.sort(allChapters, chapterComparator);
    }

    public VolumeEdit(final PageParameters parameters)
    {
        String projectName = parameters.get("project").toOptionalString();
        String volumeName = parameters.get("volume").toOptionalString();
        if (Strings.isEmpty(projectName) || Strings.isEmpty(volumeName))
        {
            throw RuranobeUtils.REDIRECT_TO_404;
        }

        String volumeUrl = projectName + "/" + volumeName;
        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        try
        {
            VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
            volume = volumesMapperCacheable.getVolumeByUrl(volumeUrl);

            if (volume == null)
            {
                throw RuranobeUtils.REDIRECT_TO_404;
            }

            VolumeReleaseActivitiesMapper volumeReleaseActivitiesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumeReleaseActivitiesMapper.class);
            volumeReleaseActivities = Lists.newArrayList(volumeReleaseActivitiesMapperCacheable.getVolumeReleaseActivitiesByVolumeId(volume.getVolumeId()));

            VolumeActivitiesMapper activitiesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumeActivitiesMapper.class);
            activities = activitiesMapperCacheable.getAllVolumeActivities();

            TeamMembersMapper teamMembersMapperCacheable = CachingFacade.getCacheableMapper(session, TeamMembersMapper.class);
            teamMembers = teamMembersMapperCacheable.getAllTeamMembers();

            ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            chapters = chaptersMapperCacheable.getChaptersByVolumeId(volume.getVolumeId());

            UpdatesMapper updatesMapperCacheable = CachingFacade.getCacheableMapper(session, UpdatesMapper.class);
            updates = updatesMapperCacheable.getUpdatesByVolumeId(volume.getVolumeId());

            ProjectsMapper projectsMapperCacheable = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
            projects = Lists.newArrayList(projectsMapperCacheable.getAllProjects());

            ChapterImagesMapper chapterImagesMapperCacheable = CachingFacade.getCacheableMapper(session, ChapterImagesMapper.class);
            volumeImages = chapterImagesMapperCacheable.getChapterImagesByVolumeId(volume.getVolumeId());
        }
        finally
        {
            session.close();
        }

        final Map<Integer, Project> projectIdToProject = new HashMap<Integer, Project>();
        for (Project project : projects)
        {
            projectIdToProject.put(project.getProjectId(), project);
        }
        volume.setProject(projectIdToProject.get(volume.getProjectId()));
        Collections.sort(projects, new Comparator<Project>()
        {
            @Override
            public int compare(Project p1, Project p2)
            {
                if (p1.getParentId() == null && p2.getParentId() == null)
                {
                    return ObjectUtils.compare(p1.getOrderNumber(), p2.getOrderNumber(), true);
                }
                else if (p1.getParentId() == null)
                {
                    int parentComp = ObjectUtils.compare(p1.getOrderNumber(), projectIdToProject.get(p2.getParentId()).getOrderNumber(), true);
                    return parentComp == 0 ? -1 : parentComp;
                }
                else if (p2.getParentId() == null)
                {
                    int parentComp = ObjectUtils.compare(projectIdToProject.get(p1.getParentId()).getOrderNumber(), p2.getOrderNumber(), true);
                    return parentComp == 0 ? 1 : parentComp;
                }
                else
                {
                    int parentComp = ObjectUtils.compare(projectIdToProject.get(p1.getParentId()).getOrderNumber(),
                            projectIdToProject.get(p2.getParentId()).getOrderNumber(), true);
                    return parentComp == 0 ? ObjectUtils.compare(p1.getOrderNumber(), p2.getOrderNumber(), true) : parentComp;
                }
            }
        });
        Map<Integer, VolumeActivity> activityIdToActivity = new HashMap<Integer, VolumeActivity>();
        for (VolumeActivity activity : activities)
        {
            activityIdToActivity.put(activity.getActivityId(), activity);
        }
        for (VolumeReleaseActivity volumeReleaseActivity : volumeReleaseActivities)
        {
            volumeReleaseActivity.setActivity(activityIdToActivity.get(volumeReleaseActivity.getActivityId()));
        }
        for (TeamMember member : teamMembers)
        {
            memberNickToId.put(member.getNikname(), member.getMemberId());
        }

        stubСhapter = new Chapter();
        stubСhapter.setOrderNumber(-1);
        stubСhapter.setTitle("Весь том");
        Collections.sort(chapters, chapterComparator);
        reinitAllChapters();
//        final Dataset teamMembersDataset = new Dataset("teamMembers").withLocal(teamMembers).withValueKey("nikname");

        add(new AdminInfoFormPanel<Volume>("info", "Информация", new CompoundPropertyModel<Volume>(volume))
        {
            @Override
            public void onSubmit()
            {
                SqlSession session = MybatisUtil.getSessionFactory().openSession();
                try
                {
                    VolumesMapper mapper = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
                    mapper.updateVolume(volume);
                    session.commit();
                }
                finally
                {
                    session.close();
                }
            }

            @Override
            protected Component getContentItemLabelComponent(String id, IModel<Volume> model)
            {
                return new Fragment(id, "volumeInfoFragment", VolumeEdit.this, model)
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
                        add(new DropDownChoice<Project>("project", projects).setChoiceRenderer(new ChoiceRenderer<Project>("title", "projectId"))
                                                                            .setOutputMarkupId(true));
                        add(new TextField<Float>("sequenceNumber"));
                        add(new TextField<String>("author"));
                        add(new TextField<String>("illustrator"));
                        add(new DateTextField("releaseDate", "dd.MM.yyyy"));
                        add(new TextField<String>("isbn"));
                        add(new DropDownChoice<String>("volumeType", RuraConstants.VOLUME_TYPE_LIST));
                        add(new Select<String>("volumeStatus")
                                .add(new SelectOptions<String>("basic", RuraConstants.VOLUME_STATUS_BASIC_LIST, optionRenderer))
                                .add(new SelectOptions<String>("external", RuraConstants.VOLUME_STATUS_EXTERNAL_LIST, optionRenderer))
                                .add(new SelectOptions<String>("not_in_work", RuraConstants.VOLUME_STATUS_IN_WORK_LIST, optionRenderer))
                                .add(new SelectOptions<String>("in_work", RuraConstants.VOLUME_STATUS_NOT_IN_WORK_LIST, optionRenderer))
                                .add(new SelectOptions<String>("published", RuraConstants.VOLUME_STATUS_PUBLISHED_LIST, optionRenderer)));
                        add(new TextField<String>("externalUrl"));
                        add(new TextArea<String>("annotation"));
                        add(new CheckBox("adult"));
                    }
                };
            }
        });
        add(new AdminAffixedListPanel<VolumeReleaseActivity>("staff", "Этапы работы", new ListModel<VolumeReleaseActivity>(volumeReleaseActivities))
        {
            @Override
            public void onSubmit()
            {
                SqlSession session = MybatisUtil.getSessionFactory().openSession();
                try
                {
                    VolumeReleaseActivitiesMapper mapper = CachingFacade.getCacheableMapper(session, VolumeReleaseActivitiesMapper.class);
                    for (VolumeReleaseActivity item : model.getObject())
                    {
                        if (!removed.contains(item))
                        {
                            if (item.getActivityId() != null)
                            {
                                mapper.updateVolumeReleaseActivity(item);
                            }
                            else
                            {
                                mapper.insertVolumeReleaseActivity(item);
                            }
                        }
                    }
                    for (VolumeReleaseActivity removedItem : removed)
                    {
                        if (removedItem.getActivityId() != null)
                        {
                            mapper.deleteVolumeReleaseActivity(removedItem.getActivityId());
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
            protected VolumeReleaseActivity makeItem()
            {
                return new VolumeReleaseActivity();
            }

            @Override
            protected Component getSelectorItemLabelComponent(String id, IModel<VolumeReleaseActivity> model)
            {
                return new Label(id, new PropertyModel<VolumeReleaseActivity>(model, "title"));
            }

            @Override
            protected Component getFormItemLabelComponent(String id, IModel<VolumeReleaseActivity> model)
            {
                return new Fragment(id, "staffFormItemFragment", VolumeEdit.this, model)
                {
                    @Override
                    protected void onInitialize()
                    {
                        super.onInitialize();
                        add(new TextField<String>("memberName").setRequired(true).setLabel(Model.of("Участник")).setOutputMarkupId(true));
                        add(new DropDownChoice<VolumeActivity>("activity", activities)
                                .setChoiceRenderer(new ChoiceRenderer<VolumeActivity>("activityName", "activityId")));
                        add(new CheckBox("teamHidden"));
                    }
                };
            }
        }.setSortable(true));

        add(new AdminAffixedListPanel<Chapter>("chapters", "Главы", new ListModel<Chapter>(chapters))
        {
            @Override
            public void onSubmit()
            {
                SqlSession session = MybatisUtil.getSessionFactory().openSession();
                try
                {
                    ChaptersMapper mapper = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
                    for (Chapter item : model.getObject())
                    {
                        if (!removed.contains(item))
                        {
                            if (item.getChapterId() != null)
                            {
                                mapper.updateChapter(item);
                            }
                            else
                            {
                                mapper.insertChapter(item);
                            }
                        }
                    }
                    for (Chapter removedItem : removed)
                    {
                        if (removedItem.getChapterId() != null)
                        {
                            mapper.deleteChapter(removedItem.getChapterId());
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
            protected Chapter makeItem()
            {
                return new Chapter();
            }

            @Override
            protected Component getSelectorItemLabelComponent(String id, IModel<Chapter> model)
            {
                return new Label(id, new PropertyModel<VolumeReleaseActivity>(model, "title"))
                        .add(new AttributeAppender("class", model.getObject().isNested() ? " sub-chapter" : ""));
            }

            @Override
            protected Component getFormItemLabelComponent(String id, IModel<Chapter> model)
            {
                return new Fragment(id, "chapterFormItemFragment", VolumeEdit.this, model)
                {
                    @Override
                    protected void onInitialize()
                    {
                        super.onInitialize();
                        add(new CheckBox("nested"));
                        add(new TextField<String>("urlPart").setRequired(true).setLabel(Model.of("Ссылка")));
                        add(new TextField<String>("title").setRequired(true).setLabel(Model.of("Заголовок")));
                    }
                };
            }
        }.setSortable(true));

        add(new AdminAffixedListPanel<Update>("updates", "Обновления", new ListModel<Update>(updates))
        {
            @Override
            public void onSubmit()
            {
                SqlSession session = MybatisUtil.getSessionFactory().openSession();
                try
                {
                    UpdatesMapper mapper = CachingFacade.getCacheableMapper(session, UpdatesMapper.class);
                    for (Update item : model.getObject())
                    {
                        if (!removed.contains(item))
                        {
                            if (item.getUpdateId() != null)
                            {
                                mapper.updateUpdate(item);
                            }
                            else
                            {
                                mapper.insertUpdate(item);
                            }
                        }
                    }
                    for (Update removedItem : removed)
                    {
                        if (removedItem.getUpdateId() != null)
                        {
                            mapper.deleteUpdate(removedItem.getUpdateId());
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
            protected Update makeItem()
            {
                return new Update();
            }

            @Override
            protected Component getSelectorItemLabelComponent(String id, IModel<Update> model)
            {
                return new Label(id, new PropertyModel<VolumeReleaseActivity>(model, "title"));
            }

            @Override
            protected Component getFormItemLabelComponent(String id, IModel<Update> model)
            {
                return new Fragment(id, "updateFormItemFragment", VolumeEdit.this, model)
                {
                    @Override
                    protected void onInitialize()
                    {
                        super.onInitialize();
                        add(new DropDownChoice<String>("updateType", RuraConstants.UPDATE_TYPE_LIST));
                        add(new DropDownChoice<Chapter>("chapter", allChapters)
                                .setChoiceRenderer(new ChoiceRenderer<Chapter>("title", "chapterId")));
                        add(new DateTextField("showTime", "dd.MM.yyyy HH:mm"));
                        add(new TextField<String>("description"));
                    }
                };
            }
        });
    }

    private final IOptionRenderer<String> optionRenderer = new IOptionRenderer<String>()
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
    private final Comparator<Chapter> chapterComparator = new Comparator<Chapter>()
    {
        @Override
        public int compare(Chapter c1, Chapter c2)
        {
            return ObjectUtils.compare(c1.getOrderNumber(), c2.getOrderNumber());
        }
    };
    private final Chapter stubСhapter;
    private final Map<String, Integer> memberNickToId = new HashMap<String, Integer>();
    private Volume volume;
    private List<Project> projects;
    private List<VolumeReleaseActivity> volumeReleaseActivities;
    private List<VolumeActivity> activities;
    private List<TeamMember> teamMembers;
    private List<Chapter> chapters;
    private List<Chapter> allChapters = new ArrayList<Chapter>();
    private List<Update> updates;
    private List<ChapterImage> volumeImages;
}