package ru.ruranobe.wicket.webpages.common;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.ExternalResource;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.mybatis.entities.tables.Volume;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.LoginSession;
import ru.ruranobe.wicket.RuraConstants;
import ru.ruranobe.wicket.components.CoverCarousel;
import ru.ruranobe.wicket.components.LabelHideableOnNull;
import ru.ruranobe.wicket.components.sidebar.*;
import ru.ruranobe.wicket.webpages.admin.ProjectEdit;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;

public class ProjectPage extends SidebarLayoutPage
{

    private static final ArrayList<String> DISPLAYABLE_NAMES =
            Lists.newArrayList("Ранобэ", "Побочные истории", "Авторские додзинси", "Другое");
    private static final Map<String, String> VOLUME_STATUS_LABEL_COLOR_CLASS =
            new ImmutableMap.Builder<String, String>()
                    .put(RuraConstants.VOLUME_STATUS_EXTERNAL_DROPPED, "danger")
                    .put(RuraConstants.VOLUME_STATUS_EXTERNAL_ACTIVE, "warning")
                    .put(RuraConstants.VOLUME_STATUS_EXTERNAL_DONE, "success")
                    .put(RuraConstants.VOLUME_STATUS_NO_ENG, "danger")
                    .put(RuraConstants.VOLUME_STATUS_FREEZE, "danger")
                    .put(RuraConstants.VOLUME_STATUS_ON_HOLD, "info")
                    .put(RuraConstants.VOLUME_STATUS_QUEUE, "info")
                    .put(RuraConstants.VOLUME_STATUS_ONGOING, "warning")
                    .put(RuraConstants.VOLUME_STATUS_TRANSLATING, "warning")
                    .put(RuraConstants.VOLUME_STATUS_PROOFREAD, "warning")
                    .put(RuraConstants.VOLUME_STATUS_DECOR, "success")
                    .put(RuraConstants.VOLUME_STATUS_DONE, "success")
                    .put(RuraConstants.VOLUME_STATUS_LICENSE, "success")
                    .build();
    private static final VolumesComparator COMPARATOR = new VolumesComparator();
    protected String titleName;

    public ProjectPage(PageParameters parameters)
    {
        String projectUrl = parameters.get("project").toString();
        redirectTo404IfArgumentIsNull(projectUrl);

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();

        try (SqlSession session = sessionFactory.openSession())
        {
            ProjectsMapper projectsMapperCacheable = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);

            final Project mainProject = projectsMapperCacheable.getProjectByUrl(projectUrl);

            redirectTo404IfArgumentIsNull(mainProject);
            redirectTo404(mainProject.isProjectHidden() && !mainProject.isWorks()
                          && !LoginSession.get().isProjectEditAllowedByUser(mainProject.getUrl()));

            if (mainProject.isWorks())
            {
                addBodyClassAttribute("works");
            }

            titleName = mainProject.getTitle();
            Collection<Project> subProjects =
                    projectsMapperCacheable.getSubProjectsByParentProjectId(mainProject.getProjectId());
            final ArrayList<Project> projects = new ArrayList<>();
            projects.add(mainProject);
            projects.addAll(subProjects);

            VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);

            ExternalResourcesMapper externalResourcesMapperCacheable = CachingFacade.getCacheableMapper(session, ExternalResourcesMapper.class);

            setDefaultModel(new CompoundPropertyModel<>(mainProject));

            add(new Label("title"));
            add(new LabelHideableOnNull("nameJp"));
            add(new LabelHideableOnNull("nameRomaji"));
            add(new LabelHideableOnNull("nameEn"));
            add(new LabelHideableOnNull("nameRu"));
            add(new LabelHideableOnNull("author"));
            add(new LabelHideableOnNull("illustrator"));
            add(new LabelHideableOnNull("originalStory"));
            add(new LabelHideableOnNull("originalDesign"));
            add(new LabelHideableOnNull("issueStatus"));
            add(new LabelHideableOnNull("translationStatus"));
            add(new Label("franchiseParsed").setEscapeModelStrings(false));
            add(new Label("annotationParsed").setEscapeModelStrings(false));

            ArrayList<Volume> mainProjectFirstCovers = new ArrayList<>();
            ArrayList<Volume> mainProjectActiveCovers = new ArrayList<>();
            ArrayList<Volume> firstCovers = new ArrayList<>();
            ArrayList<Volume> activeCovers = new ArrayList<>();
            Volume mainProjectLastCover = null;
            Volume lastCover = null;

            final Map<SimpleEntry<String, String>, Integer>
                    shortNameLabelWidthMap =
                    new HashMap<>();
            final Map<String, ArrayList<SimpleEntry<String, ArrayList<Volume>>>>
                    volumeTypeToSubProjectToVolumes =
                    new HashMap<>();
            for (Project project : projects)
            {
                String subProjectTitle = project.getTitle();
                if (project == mainProject)
                {
                    subProjectTitle = "";
                }
                Collection<Volume> volumes = volumesMapperCacheable.getVolumesByProjectId(project.getProjectId());
                for (Volume volume : volumes)
                {
                    String type = volume.getVolumeType();
                    if (!type.isEmpty())
                    {
                        if (volume.getImageOne() != null)
                        {
                            if (subProjectTitle.isEmpty() && type.equals(DISPLAYABLE_NAMES.get(0))
                                && mainProjectFirstCovers.size() < 3)
                            {
                                mainProjectFirstCovers.add(volume);
                            }
                            else if (mainProjectFirstCovers.isEmpty() && firstCovers.size() < 3)
                            {
                                firstCovers.add(volume);
                            }

                            if (volume.getVolumeStatus().equals(RuraConstants.VOLUME_STATUS_ONGOING)
                                || volume.getVolumeStatus().equals(RuraConstants.VOLUME_STATUS_TRANSLATING)
                                || volume.getVolumeStatus().equals(RuraConstants.VOLUME_STATUS_PROOFREAD))
                            {
                                if (subProjectTitle.isEmpty() && type.equals(DISPLAYABLE_NAMES.get(0))
                                    && mainProjectActiveCovers.size() < 3)
                                {
                                    mainProjectActiveCovers.add(volume);
                                }
                                else if (mainProjectActiveCovers.isEmpty() && activeCovers.size() < 3)
                                {
                                    activeCovers.add(volume);
                                }
                            }

                            if (subProjectTitle.isEmpty() && type.equals(DISPLAYABLE_NAMES.get(0))
                                && (mainProjectLastCover == null || mainProjectLastCover.getProjectId() < volume.getProjectId()))
                            {
                                mainProjectLastCover = volume;
                            }
                            if (lastCover == null || lastCover.getProjectId() < volume.getProjectId())
                            {
                                lastCover = volume;
                            }
                        }

                        if (volumeTypeToSubProjectToVolumes.get(type) == null)
                        {
                            volumeTypeToSubProjectToVolumes.put(type, new ArrayList<SimpleEntry<String, ArrayList<Volume>>>());
                        }
                        ArrayList<SimpleEntry<String, ArrayList<Volume>>>
                                subProjectToVolumes =
                                volumeTypeToSubProjectToVolumes.get(type);
                        if (subProjectToVolumes.isEmpty() || !subProjectToVolumes.get(subProjectToVolumes.size() - 1).getKey()
                                                                                 .equals(subProjectTitle))
                        {
                            subProjectToVolumes
                                    .add(new SimpleEntry<>(subProjectTitle, new ArrayList<Volume>()));
                        }
                        subProjectToVolumes.get(subProjectToVolumes.size() - 1).getValue().add(volume);

                        SimpleEntry<String, String>
                                volumeTypeAndSubProject =
                                new SimpleEntry<>(type, subProjectTitle);
                        if (volume.getNameShort() != null)
                        {
                            if (shortNameLabelWidthMap.get(volumeTypeAndSubProject) == null
                                || shortNameLabelWidthMap.get(volumeTypeAndSubProject) < volume.getNameShort().length())
                            {
                                shortNameLabelWidthMap.put(volumeTypeAndSubProject, volume.getNameShort().length());
                            }
                        }
                    }
                }
            }

            ListView<String> volumeTypeRepeater = new ListView<String>("volumeTypeRepeater", DISPLAYABLE_NAMES)
            {
                @Override
                protected void populateItem(final ListItem<String> listItem1)
                {
                    final String displayableName = listItem1.getModelObject();
                    Label volumeType = new Label("volumeType", displayableName);
                    if (volumeTypeToSubProjectToVolumes.get(displayableName) == null)
                    {
                        volumeType.setVisible(false);
                    }
                    listItem1.add(volumeType);
                    ListView<SimpleEntry<String, ArrayList<Volume>>> volumeSubProjectRepeater = new ListView<SimpleEntry<String, ArrayList<Volume>>>("volumeSubProjectRepeater", volumeTypeToSubProjectToVolumes.get(displayableName))
                    {
                        @Override
                        protected void populateItem(ListItem<SimpleEntry<String, ArrayList<Volume>>> listItem2)
                        {
                            SimpleEntry<String, ArrayList<Volume>> projectTitleAndVolumes = listItem2.getModelObject();
                            String subProjectNameString = projectTitleAndVolumes.getKey();
                            Label projectName = new Label("projectName", subProjectNameString);
                            if (subProjectNameString.isEmpty())
                            {
                                projectName.setVisible(false);
                            }
                            listItem2.add(projectName);
                            ArrayList<Volume> volumes = projectTitleAndVolumes.getValue();
                            Collections.sort(volumes, COMPARATOR);
                            final Integer shortNameLabelWidth = shortNameLabelWidthMap.get(new SimpleEntry<>(displayableName, subProjectNameString));
                            ListView<Volume> volumeRepeater = new ListView<Volume>("volumeRepeater", projectTitleAndVolumes.getValue())
                            {
                                @Override
                                protected void populateItem(final ListItem<Volume> listItem3)
                                {
                                    Volume volume = listItem3.getModelObject();
                                    String nameShort = volume.getNameShort();
                                    Label volumeName = new Label("volumeName", nameShort);
                                    if (shortNameLabelWidth != null)
                                    {
                                        volumeName.add(new AttributeModifier("style", "width:" + (shortNameLabelWidth * 7.5 + 10) + "px;"));
                                    }
                                    listItem3.add(volumeName);
                                    String volumeStatusStr = volume.getVolumeStatus();
                                    Label volumeStatus = new Label("volumeStatus", RuraConstants.VOLUME_STATUS_TO_LABEL_TEXT.get(volumeStatusStr));
                                    volumeStatus.add(new AttributeAppender("class", " label-" + VOLUME_STATUS_LABEL_COLOR_CLASS.get(volumeStatusStr)));
                                    listItem3.add(volumeStatus);
                                    if (volumeStatusStr.equals(RuraConstants.VOLUME_STATUS_HIDDEN)
                                        && !LoginSession.get().isProjectShowHiddenAllowedByUser(mainProject.getUrl()))
                                    {
                                        listItem3.setVisible(false);
                                    }
                                    BookmarkablePageLink volumeLink = volume.makeBookmarkablePageLink("volumeLink");
                                    volumeLink.setBody(new Model<>(volume.getNameTitle()));
                                    listItem3.add(volumeLink);
                                }
                            };
                            listItem2.add(volumeRepeater);
                        }
                    };
                    listItem1.add(volumeSubProjectRepeater);
                }
            };
            add(volumeTypeRepeater);

            Set<Volume> allCoversSet = new HashSet<>();
            if (mainProjectFirstCovers.isEmpty())
            {
                allCoversSet.addAll(firstCovers);
            }
            else
            {
                allCoversSet.addAll(mainProjectFirstCovers);
            }
            if (mainProjectActiveCovers.isEmpty())
            {
                allCoversSet.addAll(activeCovers);
            }
            else
            {
                allCoversSet.addAll(mainProjectActiveCovers);
            }
            if (mainProjectLastCover != null)
            {
                allCoversSet.add(mainProjectLastCover);
            }
            if (lastCover != null)
            {
                allCoversSet.add(lastCover);
            }
            ArrayList<Volume> allCovers = new ArrayList<>(allCoversSet);
            Collections.sort(allCovers, COMPARATOR);
            ArrayList<SimpleEntry<String, ExternalResource>> allCoverIds = new ArrayList<>();
            for (Volume volume : allCovers)
            {
                allCoverIds.add(new SimpleEntry<>(volume.getNameTitle(),
                        externalResourcesMapperCacheable
                                .getExternalResourceById(volume.getImageOne())));
            }

            add(new CoverCarousel("projectCoverCarousel", allCoverIds));
            if (!allCoverIds.isEmpty()) {
                WebMarkupContainer ogImage = new WebMarkupContainer("ogImage");
                add(ogImage);
                ogImage.add(
                        new AttributeModifier("content", allCoverIds.iterator().next().getValue().getThumbnail(240)));
            }

            sidebarModules.add(new ActionsSidebarModule(ProjectEdit.class, mainProject.getUrlParameters()));
            sidebarModules.add(new UpdatesSidebarModule(mainProject.getProjectId()));
            sidebarModules.add(new RequisitesSidebarModule());
            sidebarModules.add(new ProjectsSidebarModule());
            sidebarModules.add(new FriendsSidebarModule());
        }
    }

    @Override
    protected String getPageTitle()
    {
        return titleName != null ? titleName + " - РуРанобэ" : super.getPageTitle();
    }

    private static class VolumesComparator implements Comparator<Volume>
    {

        @Override
        public int compare(Volume volume1, Volume volume2)
        {
            int projectComparison = volume1.getProjectId().compareTo(volume2.getProjectId());
            if (projectComparison != 0)
            {
                return projectComparison;
            }
            return ObjectUtils.compare(volume1.getSequenceNumber(), volume2.getSequenceNumber());
        }

    }
}
