package ru.ruranobe.wicket.webpages.personal;

import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.additional.ChapterUrlDetails;
import ru.ruranobe.mybatis.entities.tables.*;
import ru.ruranobe.mybatis.mappers.BookmarksMapper;
import ru.ruranobe.mybatis.mappers.ChapterUrlDetailsMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.LoginSession;
import ru.ruranobe.wicket.components.LabelHideableOnNull;
import ru.ruranobe.wicket.components.modals.ModalChangeEmail;
import ru.ruranobe.wicket.components.modals.ModalChangePassword;
import ru.ruranobe.wicket.components.modals.ModalUserSettings;
import ru.ruranobe.wicket.components.sidebar.FriendsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ProjectsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.RequisitesSidebarModule;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;
import ru.ruranobe.wicket.webpages.common.ProjectPage;
import ru.ruranobe.wicket.webpages.common.TextPage;
import ru.ruranobe.wicket.webpages.common.VolumePage;

import java.util.List;

public class Cabinet extends SidebarLayoutPage
{
    public Cabinet()
    {
        setStatelessHint(true);

        final User user = LoginSession.get().getUser();
        redirectTo404IfArgumentIsNull(user);

        setDefaultModel(new CompoundPropertyModel<>(user));

        add(new WebMarkupContainer("avatarNoImageText"));
        add(new WebMarkupContainer("avatarImage"));
        add(new Label("username").setRenderBodyOnly(true));
        add(new LabelHideableOnNull("realname").setRenderBodyOnly(true));
        add(new LabelHideableOnNull("email").setRenderBodyOnly(true));
        add(new LabelHideableOnNull("registrationDate").setRenderBodyOnly(true));

        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            final List<Bookmark> bookmarks = CachingFacade.getCacheableMapper(session, BookmarksMapper.class)
                    .getBookmarksExtendedByUser(user.getUserId());

            add(new ListView<Bookmark>("userBookmarksRepeater", bookmarks)
            {
                @Override
                protected void populateItem(final ListItem<Bookmark> listItem)
                {
                    final Bookmark bookmark = listItem.getModelObject();

                    StatelessForm bookmarkForm = new StatelessForm("bookmarkForm");
                    listItem.add(bookmarkForm);

                    ChapterUrlDetails chapterUrlDetails;
                    try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
                    {
                        ChapterUrlDetailsMapper chapterUrlDetailsMapperCacheable = CachingFacade.getCacheableMapper(session, ChapterUrlDetailsMapper.class);
                        chapterUrlDetails = chapterUrlDetailsMapperCacheable.getChapterUrlDetailsByChapter(bookmark.getChapterId());
                    }

                    PageParameters projectPageParameters = Project.makeUrlParameters(chapterUrlDetails.getProjectUrl());
                    bookmarkForm.add(
                            new BookmarkablePageLink("projectUrl", ProjectPage.class, projectPageParameters)
                                    .add(new Label("projectUrlText", chapterUrlDetails.getProjectTitle()).setRenderBodyOnly(true))
                    );

                    PageParameters volumePageParameters = Volume.makeUrlParameters(chapterUrlDetails.getVolumeUrl().split("/", -1));
                    bookmarkForm.add(
                            new BookmarkablePageLink("volumeUrl", VolumePage.class, volumePageParameters)
                                    .add(new Label("volumeUrlText", chapterUrlDetails.getVolumeTitle()).setRenderBodyOnly(true))
                    );

                    PageParameters chapterPageParameters = Chapter.makeUrlParameters(chapterUrlDetails.getChapterUrl().split("/", -1));
                    bookmarkForm.add(
                            new BookmarkablePageLink("chapterUrl", TextPage.class, chapterPageParameters)
                                    .add(new Label("chapterUrlText", chapterUrlDetails.getChapterTitle()).setRenderBodyOnly(true))
                    );

                    bookmarkForm.add(new Label("bookmarkText", bookmark.getParagraph().getParagraphText()));
                    bookmarkForm.add(
                            new BookmarkablePageLink("bookmarkUrl", TextPage.class,
                            Chapter.makeUrlParameters(chapterUrlDetails.getChapterUrl().split("/", -1)))
                            {
                                @Override
                                protected CharSequence getURL()
                                {
                                    return super.getURL() + "#" + bookmark.getParagraphId();
                                }
                            }
                    );

                    bookmarkForm.add(
                            new Button("deleteBookmark")
                            {
                                @Override
                                public void onSubmit()
                                {
                                    try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
                                    {
                                        CachingFacade.getCacheableMapper(session, BookmarksMapper.class).deleteBookmark(bookmark.getBookmarkId());
                                        session.commit();
                                    }
                                    throw new RedirectToUrlException(super.getPage().urlFor(super.getPage().getPageClass(), null).toString());
                                }
                            }
                    );
                }

                @Override
                public boolean isVisible()
                {
                    return bookmarks != null && !bookmarks.isEmpty();
                }
            });
        }

        // Add modal dialogues
        add(new ModalUserSettings("settings"));
        add(new ModalChangeEmail("settingsEmailModal"));
        add(new ModalChangePassword("settingsPassModal"));

        sidebarModules.add(RequisitesSidebarModule.makeDefault());
        sidebarModules.add(new ProjectsSidebarModule());
        sidebarModules.add(new FriendsSidebarModule());
    }

	@Override
	protected String getPageTitle() {
		return "Личный кабинет - РуРанобэ";
	}
}
