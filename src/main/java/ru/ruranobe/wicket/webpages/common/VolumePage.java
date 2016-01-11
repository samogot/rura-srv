package ru.ruranobe.wicket.webpages.common;

import org.apache.commons.lang3.StringUtils;
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
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.*;
import ru.ruranobe.mybatis.mappers.*;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.components.CommentsPanel;
import ru.ruranobe.wicket.components.CoverCarousel;
import ru.ruranobe.wicket.components.LabelHideableOnNull;
import ru.ruranobe.wicket.components.sidebar.*;
import ru.ruranobe.wicket.webpages.admin.VolumeEdit;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VolumePage extends SidebarLayoutPage {

	protected String titleName;

	public VolumePage(PageParameters parameters) {
		setStatelessHint(true);
		final String projectUrlValue = parameters.get("project").toString();
		String volumeShortUrl = parameters.get("volume").toString();
		if (volumeShortUrl == null || projectUrlValue == null) {
			throw RuranobeUtils.getRedirectTo404Exception(this);
		}

		SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();

		try (SqlSession session = sessionFactory.openSession())
		{
			ProjectsMapper projectsMapperCacheable = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
			Project project = projectsMapperCacheable.getProjectByUrl(projectUrlValue);

			if (project == null || project.isProjectHidden())
			{
				throw RuranobeUtils.getRedirectTo404Exception(this);
			}

			VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
			String volumeUrl = projectUrlValue + "/" + volumeShortUrl;
			final Volume volume = volumesMapperCacheable.getVolumeNextPrevByUrl(volumeUrl);

			if (volume == null)
			{
				throw RuranobeUtils.getRedirectTo404Exception(this);
			}

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
			List<SimpleEntry<String, String>> covers = new ArrayList<>();
			volumeCover = externalResourcesMapperCacheable.getExternalResourceById(volume.getImageOne());
			if (volumeCover != null)
			{
				covers.add(new SimpleEntry<>("", volumeCover.getUrl()));
			}
			volumeCover = externalResourcesMapperCacheable.getExternalResourceById(volume.getImageTwo());
			if (volumeCover != null)
			{
				covers.add(new SimpleEntry<>("", volumeCover.getUrl()));
			}
			volumeCover = externalResourcesMapperCacheable.getExternalResourceById(volume.getImageThree());
			if (volumeCover != null)
			{
				covers.add(new SimpleEntry<>("", volumeCover.getUrl()));
			}
			volumeCover = externalResourcesMapperCacheable.getExternalResourceById(volume.getImageFour());
			if (volumeCover != null)
			{
				covers.add(new SimpleEntry<>("", volumeCover.getUrl()));
			}
			add(new CoverCarousel("volumeCoverCarousel", covers));

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
			List<VolumeReleaseActivity> volumeReleaseActivities = new ArrayList<>(
					volumeReleaseActivitiesMapperCacheable.getVolumeReleaseActivitiesByVolumeId(volume.getVolumeId()));

			final Map<String, ArrayList<String>> activityNameToMemberName = new HashMap<>();
			for (VolumeReleaseActivity activity : volumeReleaseActivities)
			{
				String activityName = activity.getActivityName();
				String memberName = activity.getMemberName();

				if (activityNameToMemberName.get(activityName) == null)
				{
					ArrayList<String> temp = new ArrayList<>();
					temp.add(memberName);
					activityNameToMemberName.put(activityName, temp);
				}
				else
				{
					activityNameToMemberName.get(activityName).add(memberName);
				}
			}

			List<String> activityNames = new ArrayList<>(activityNameToMemberName.keySet());
			add(new ListView<String>("volumeReleaseActivitiesView", activityNames)
			{
				@Override
				protected void populateItem(ListItem<String> item)
				{
					String activityName = item.getModelObject();
					item.add(new Label("activityName", activityName));
					ArrayList<String> membersList = activityNameToMemberName.get(activityName);
					item.add(new Label("memberName", StringUtils.join(membersList, ',')));
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
					if (chapter.isPublished())
					{
						chapterLink = chapter.makeBookmarkablePageLink("chapterLink");
					}
					else
					{
						chapterLink = new WebMarkupContainer("chapterLink");
						chapterLink.add(new AttributeModifier("class", "unpublished"));
					}
					if (chapter.isNested())
					{
						chapterLink.add(new AttributeAppender("class", " nested"));
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
				readAllLink = new BookmarkablePageLink("readAllLink", ru.ruranobe.wicket.webpages.common.Text.class, volume.getUrlParameters())
				{
					@Override
					public boolean isVisible()
					{
						return chapters != null && !chapters.isEmpty();
					}
				};
			}
			add(readAllLink);

			add(new CommentsPanel("comments", volume.getTopicId()));
			sidebarModules.add(new DownloadsSidebarModule(volume.getUrlParameters()));
			sidebarModules.add(new ActionsSidebarModule(VolumeEdit.class, volume.getUrlParameters()));
			sidebarModules.add(new UpdatesSidebarModule(volume.getProjectId()));
			sidebarModules.add(new ProjectsSidebarModule());
			sidebarModules.add(new FriendsSidebarModule());
		}
	}

	@Override
	protected String getPageTitle() {
		return titleName != null ? titleName + " - РуРанобэ" : super.getPageTitle();
	}

	private class VolumeReleaseActivityExtended implements Serializable {

		private String assigneeTeamMember;
		private String activityName;

		public VolumeReleaseActivityExtended(String assigneeTeamMember, String activityName) {
			this.assigneeTeamMember = assigneeTeamMember;
			this.activityName = activityName;
		}

		public String getActivityName() {
			return activityName;
		}

		public String getAssigneeTeamMember() {
			return assigneeTeamMember;
		}
	}
}