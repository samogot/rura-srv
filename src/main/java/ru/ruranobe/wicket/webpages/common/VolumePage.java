package ru.ruranobe.wicket.webpages.common;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.*;
import ru.ruranobe.mybatis.mappers.*;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.LoginSession;
import ru.ruranobe.wicket.RuraConstants;
import ru.ruranobe.wicket.components.CommentsPanel;
import ru.ruranobe.wicket.components.CoverCarousel;
import ru.ruranobe.wicket.components.LabelHideableOnNull;
import ru.ruranobe.wicket.components.sidebar.*;
import ru.ruranobe.wicket.webpages.admin.VolumeEdit;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class VolumePage extends SidebarLayoutPage
{

    protected String titleName;

    public VolumePage(PageParameters parameters)
    {
        setStatelessHint(true);
        final String projectUrlValue = parameters.get("project").toString();
        String volumeShortUrl = parameters.get("volume").toString();
        redirectTo404(volumeShortUrl == null || projectUrlValue == null);

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();

        try (SqlSession session = sessionFactory.openSession())
        {
            ProjectsMapper projectsMapperCacheable = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
            Project project = projectsMapperCacheable.getProjectByUrl(projectUrlValue);

            redirectTo404IfArgumentIsNull(project);

            if (project.isWorks())
            {
                addBodyClassAttribute("works");
            }

            VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
            String volumeUrl = projectUrlValue + "/" + volumeShortUrl;
            final Volume volume = volumesMapperCacheable.getVolumeNextPrevByUrl(volumeUrl);

            redirectTo404IfArgumentIsNull(volume);
            redirectTo404((project.isProjectHidden() && !project.isWorks()
                           || volume.getVolumeStatus().equals(RuraConstants.VOLUME_STATUS_HIDDEN))
                          && !LoginSession.get().isProjectEditAllowedByUser(projectUrlValue));

            setDefaultModel(new CompoundPropertyModel<>(volume));
            titleName = volume.getNameTitle();

            BookmarkablePageLink prevUrl =
                    new BookmarkablePageLink("prevUrl", VolumePage.class, volume.getPrevUrlParameters());
            prevUrl.add(new Label("prevNameShort"));
            prevUrl.setVisible(volume.getPrevUrl() != null);
            add(prevUrl);
            BookmarkablePageLink nextUrl =
                    new BookmarkablePageLink("nextUrl", VolumePage.class, volume.getNextUrlParameters());
            nextUrl.add(new Label("nextNameShort"));
            nextUrl.setVisible(volume.getNextUrl() != null);
            add(nextUrl);

            ExternalResourcesMapper externalResourcesMapperCacheable = CachingFacade.
                                                                                            getCacheableMapper(session, ExternalResourcesMapper.class);
            ExternalResource volumeCover;
            List<SimpleEntry<String, ExternalResource>> covers = new ArrayList<>();
            volumeCover = externalResourcesMapperCacheable.getExternalResourceById(volume.getImageOne());
            if (volumeCover != null)
            {
                covers.add(new SimpleEntry<>("", volumeCover));
            }
            volumeCover = externalResourcesMapperCacheable.getExternalResourceById(volume.getImageTwo());
            if (volumeCover != null)
            {
                covers.add(new SimpleEntry<>("", volumeCover));
            }
            volumeCover = externalResourcesMapperCacheable.getExternalResourceById(volume.getImageThree());
            if (volumeCover != null)
            {
                covers.add(new SimpleEntry<>("", volumeCover));
            }
            volumeCover = externalResourcesMapperCacheable.getExternalResourceById(volume.getImageFour());
            if (volumeCover != null)
            {
                covers.add(new SimpleEntry<>("", volumeCover));
            }
            add(new CoverCarousel("volumeCoverCarousel", covers));
            if (!covers.isEmpty()) {
                WebMarkupContainer ogImage = new WebMarkupContainer("ogImage");
                add(ogImage);
                ogImage.add(
                        new AttributeModifier("content", covers.iterator().next().getValue().getThumbnail(240)));
            }

            BookmarkablePageLink projectUrl =
                    new BookmarkablePageLink("projectUrl", ProjectPage.class, Project.makeUrlParameters(projectUrlValue));
            projectUrl.add(new Label("subProjectName"));
            add(projectUrl);

            add(new Label("nameTitle"));
            add(new LabelHideableOnNull("nameJp"));
            add(new LabelHideableOnNull("nameRomaji"));
            add(new LabelHideableOnNull("nameEn"));
            add(new LabelHideableOnNull("nameRu"));
            add(new LabelHideableOnNull("author"));
            add(new LabelHideableOnNull("illustrator"));
            add(new LabelHideableOnNull("originalStory"));
            add(new LabelHideableOnNull("originalDesign"));
            add(new LabelHideableOnNull("releaseDate"));
            add(new LabelHideableOnNull("fullStatus"));
            add(new LabelHideableOnNull("volumeStatusHint"));
            Label annotationParsed;
            add(annotationParsed = new LabelHideableOnNull("annotationParsed"));
            annotationParsed.setEscapeModelStrings(false);

            ExternalLink isbn = new ExternalLink(
                    "isbn",
                    "https://www.amazon.co.jp/s?search-alias=stripbooks&language=en_JP&field-isbn=" + volume.getIsbn(),
                    volume.getIsbn());
            isbn.setVisible(volume.getIsbn() != null);
            add(isbn);

            VolumeReleaseActivitiesMapper volumeReleaseActivitiesMapperCacheable =
                    CachingFacade.getCacheableMapper(session, VolumeReleaseActivitiesMapper.class);


            ArrayList<VolumeReleaseActivity> volumeReleaseActivities =
                    volumeReleaseActivitiesMapperCacheable.getVolumeReleaseActivitiesByVolumeId(volume.getVolumeId());
            String teamText = groupUpVolumeReleaseActivitiesAndGetTeamText(volumeReleaseActivities);

            add(new LabelHideableOnNull("team", teamText).setEscapeModelStrings(false));
            add(new PropertyListView<VolumeReleaseActivity>("volumeReleaseActivitiesView", volumeReleaseActivities)
            {
                @Override
                protected void populateItem(ListItem<VolumeReleaseActivity> item)
                {
                    item.add(new Label("activityName"));
                    item.add(new Label("memberName").setEscapeModelStrings(false));
                }

                @Override
                public boolean isVisible()
                {
                    return !getModelObject().isEmpty();
                }
            });

            ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            final List<Chapter> chapters = chaptersMapperCacheable.getChaptersByVolumeId(volume.getVolumeId());
            ListView<Chapter> chaptersView = new ListView<Chapter>("chaptersView", chapters)
            {
                @Override
                protected void populateItem(ListItem<Chapter> item)
                {
                    Chapter chapter = item.getModelObject();
                    WebMarkupContainer chapterLink;
                    if (chapter.isPublished() || LoginSession.get().isProjectShowHiddenAllowedByUser(projectUrlValue))
                    {
                        chapterLink = chapter.makeBookmarkablePageLink("chapterLink");
                    }
                    else
                    {
                        chapterLink = new WebMarkupContainer("chapterLink");
                    }
                    if (!chapter.isPublished())
                    {
                        chapterLink.add(new AttributeModifier("class", "unpublished"));
                    }
                    if (chapter.isNested())
                    {
                        chapterLink.add(new AttributeAppender("class", " nested"));
                    }
                    if (chapter.getUrlPart().equals("text"))
                    {
                        chapterLink.setVisible(false);
                    }
                    chapterLink.add(new Label("chapterName", chapter.getTitle()));
                    item.add(chapterLink);
                }
            };
            add(chaptersView);

            AbstractLink readAllLink;
            if (volume.isStatusExternal() && volume.getExternalUrl() != null)
            {
                readAllLink = new ExternalLink("readAllLink", volume.getExternalUrl());
            }
            else
            {
                readAllLink = new BookmarkablePageLink("readAllLink", TextPage.class, volume.getUrlParameters())
                {
                    @Override
                    public boolean isVisible()
                    {
                        return chapters != null && !chapters.isEmpty();
                    }
                };
            }
            add(readAllLink);

            add(new ExternalLink("mobilefb2pic", "/d/fb2/" + volumeUrl));
            add(new ExternalLink("mobilefb2nopic", "/d/fb2/" + volumeUrl + "?pic=0"));
            add(new ExternalLink("mobiledocx", "/d/docx/" + volumeUrl));
            add(new ExternalLink("mobileepub", "/d/epub/" + volumeUrl));

            add(new CommentsPanel("comments", volume.getTopicId()));
            sidebarModules.add(new DownloadsSidebarModule(volume.getUrlParameters()));
            sidebarModules.add(new ActionsSidebarModule(VolumeEdit.class, volume.getUrlParameters()));
            sidebarModules.add(new UpdatesSidebarModule(volume.getProjectId()));
            sidebarModules.add(new RequisitesSidebarModule());
            sidebarModules.add(new ProjectsSidebarModule());
            sidebarModules.add(new FriendsSidebarModule());
        }
    }

    private String groupUpVolumeReleaseActivitiesAndGetTeamText(ArrayList<VolumeReleaseActivity> vraList)
    {
        ArrayList<String> teamEntities = new ArrayList<>();
        int i = 0;
        while (i < vraList.size())
        {
            VolumeReleaseActivity vra = vraList.get(i);
            if (vra.getTeamName() != null && !vra.getTeamShowStatus().equals(VolumeReleaseActivity.TEAM_SHOW_NONE))
            {
                String teamEntry;
                if (vra.getTeamShowStatus().equals(VolumeReleaseActivity.TEAM_SHOW_NICK))
                {
                    teamEntry = StringEscapeUtils.unescapeHtml4(vra.getMemberName());
                }
                else if (!Strings.isEmpty(vra.getTeamLink()))
                {
                    teamEntry = vra.getTeamLinkTag();
                }
                else
                {
                    teamEntry = StringEscapeUtils.unescapeHtml4(vra.getTeamName());
                }
                if (!teamEntities.contains(teamEntry))
                {
                    teamEntities.add(teamEntry);
                }
            }
            StringBuilder vraCurEntry = new StringBuilder(StringEscapeUtils.escapeHtml4(vra.getMemberName()));
            if (vra.isTeamShowLabel())
            {
                vraCurEntry.append(" (");
                if (!Strings.isEmpty(vra.getTeamLink()) && !vra.getTeamShowStatus().equals(VolumeReleaseActivity.TEAM_SHOW_TEAM))
                {
                    vraCurEntry.append(vra.getTeamLinkTag());
                }
                else
                {
                    vraCurEntry.append(StringEscapeUtils.unescapeHtml4(vra.getTeamName()));
                }
                vraCurEntry.append(")");
            }

            List<VolumeReleaseActivity> vraPrevList = vraList.subList(0, i);
            Optional<VolumeReleaseActivity> vraFirstPrev = vraPrevList.stream().filter(
                    vraPrev -> vraPrev.getActivityName().equals(vra.getActivityName())).findFirst();
            if (vraFirstPrev.isPresent())
            {
                vraCurEntry.insert(0, ", ");
                vraCurEntry.insert(0, vraFirstPrev.get().getMemberName());
                vraFirstPrev.get().setMemberName(vraCurEntry.toString());
                vraList.remove(i);
                continue;
            }
            else
            {
                vra.setMemberName(vraCurEntry.toString());
            }
            ++i;
        }
        if (teamEntities.isEmpty())
        {
            return null;
        }

        StringBuilder teamText = new StringBuilder(teamEntities.get(0));
        if (teamEntities.size() > 1)
        {
            teamText.append(" совместно с ");
            teamText.append(teamEntities.stream().skip(1).collect(Collectors.joining(", ")));
        }
        return teamText.toString();
    }

    @Override
    protected String getPageTitle()
    {
        return titleName != null ? titleName + " - РуРанобэ" : super.getPageTitle();
    }
}
