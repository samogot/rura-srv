package ru.ruranobe.wicket.webpages;

import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.additional.ChapterUrlDetails;
import ru.ruranobe.mybatis.mappers.BookmarksMapper;
import ru.ruranobe.mybatis.mappers.ChapterUrlDetailsMapper;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.UsersMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.entities.tables.Bookmark;
import ru.ruranobe.mybatis.entities.tables.Chapter;
import ru.ruranobe.mybatis.entities.tables.User;
import ru.ruranobe.wicket.LoginSession;
import ru.ruranobe.wicket.components.LabelHideableOnNull;
import ru.ruranobe.wicket.components.sidebar.FriendsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ProjectsSidebarModule;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

import javax.swing.plaf.nimbus.State;
import java.util.Arrays;
import java.util.List;

public class Cabinet extends SidebarLayoutPage
{
    public Cabinet()
    {
        setStatelessHint(true);

        final User user = ((LoginSession) LoginSession.get()).getUser();
        if (user == null)
        {
            throw RuranobeUtils.getRedirectTo404Exception(this);
        }

        setDefaultModel(new CompoundPropertyModel<User>(user));

        WebMarkupContainer avatarNoImageText = new WebMarkupContainer("avatarNoImageText")
        {
            @Override
            public boolean isVisible()
            {
                return true;
            }
        };
        add(avatarNoImageText);

        WebMarkupContainer avatarImage = new WebMarkupContainer("avatarImage");
        avatarImage.add(new AttributeAppender("src", "ololo"));
        add(avatarImage);

        Label username = new Label("username");
        username.setRenderBodyOnly(true);
        add(username);

        Label realname = new LabelHideableOnNull("realname");
        realname.setRenderBodyOnly(true);
        add(realname);

        Label email = new LabelHideableOnNull("email");
        email.setRenderBodyOnly(true);
        add(email);

        Label registrationDate = new LabelHideableOnNull("registrationDate");
        registrationDate.setRenderBodyOnly(true);
        add(registrationDate);

        SqlSession session = MybatisUtil.getSessionFactory().openSession();
        try
        {
            BookmarksMapper bookmarksMapperCacheable = CachingFacade.getCacheableMapper(session, BookmarksMapper.class);
            final List<Bookmark> bookmarks = bookmarksMapperCacheable.getBookmarksByUser(user.getUserId());
            final ChapterUrlDetailsMapper chapterUrlDetailsMapperCacheable = CachingFacade.getCacheableMapper(session, ChapterUrlDetailsMapper.class);

            ListView<Bookmark> userBookmarksRepeater = new ListView<Bookmark>("userBookmarksRepeater", bookmarks)
            {
                @Override
                protected void populateItem(final ListItem<Bookmark> listItem)
                {
                    final Bookmark bookmark = listItem.getModelObject();

                    StatelessForm bookmarkForm = new StatelessForm("bookmarkForm");
                    listItem.add(bookmarkForm);

                    ChapterUrlDetails chapterUrlDetails = chapterUrlDetailsMapperCacheable.getChapterUrlDetailsByChapter(bookmark.getChapterId());

                    BookmarkablePageLink projectUrl = new BookmarkablePageLink("projectUrl", ProjectPage.class,
                            new PageParameters().set("project", chapterUrlDetails.getChapterUrl()));
                    Label projectUrlText = new Label("projectUrlText", chapterUrlDetails.getChapterTitle());
                    projectUrlText.setRenderBodyOnly(true);
                    projectUrl.add(projectUrlText);
                    bookmarkForm.add(projectUrl);

                    BookmarkablePageLink volumeUrl = new BookmarkablePageLink("volumeUrl", VolumePage.class,
                            new PageParameters().set("volume", chapterUrlDetails.getVolumeUrl()));
                    Label volumeUrlText = new Label("volumeUrlText", chapterUrlDetails.getVolumeTitle());
                    volumeUrlText.setRenderBodyOnly(true);
                    volumeUrl.add(volumeUrlText);
                    bookmarkForm.add(volumeUrl);

                    BookmarkablePageLink chapterUrl = new BookmarkablePageLink("chapterUrl", Text.class,
                            new PageParameters().set("chapter", chapterUrlDetails.getChapterUrl()));
                    Label chapterUrlText = new Label("chapterUrlText", chapterUrlDetails.getChapterTitle());
                    chapterUrlText.setRenderBodyOnly(true);
                    chapterUrl.add(chapterUrlText);
                    bookmarkForm.add(chapterUrl);

                    Label bookmarkText = new Label("bookmarkText", "ololo");
                    bookmarkForm.add(bookmarkText);

                    BookmarkablePageLink bookmarkUrl = new BookmarkablePageLink("bookmarkUrl", Text.class,
                            new PageParameters().set("chapter", chapterUrlDetails.getChapterUrl()));
                    bookmarkForm.add(bookmarkUrl);

                    Button deleteBookmark = new Button("deleteBookmark")
                    {
                        @Override
                        public void onSubmit()
                        {
                            SqlSession session = MybatisUtil.getSessionFactory().openSession();
                            try
                            {
                                BookmarksMapper bookmarksMapperCacheable = CachingFacade.getCacheableMapper(session, BookmarksMapper.class);
                                bookmarksMapperCacheable.deleteBookmark(bookmark.getBookmarkId());

                                session.commit();
                            }
                            finally
                            {
                                session.close();
                            }
                        }
                    };
                    bookmarkForm.add(deleteBookmark);
                }

                @Override
                public boolean isVisible()
                {
                    return bookmarks != null && !bookmarks.isEmpty();
                }
            };
            add(userBookmarksRepeater);
        }
        finally
        {
            session.close();
        }

        StatelessForm userSettings = new StatelessForm("userSettings");
        add(userSettings);

        DropDownChoice<String> converterType = new DropDownChoice<String>("converterType", Arrays.asList("fb2", "docx", "epub"));
        userSettings.add(converterType);

        DropDownChoice<String> navigationType = new DropDownChoice<String>("navigationType", Arrays.asList("Главам", "Подлавам"));
        userSettings.add(navigationType);

        CheckBox convertWithImgs = new CheckBox("convertWithImgs");
        userSettings.add(convertWithImgs);

        CheckBox adult = new CheckBox("adult");
        userSettings.add(adult);

        CheckBox preferColoredImgs = new CheckBox("preferColoredImgs");
        userSettings.add(preferColoredImgs);

        TextField<Integer> convertImgsSize = new TextField<Integer>("convertImgsSize");
        userSettings.add(convertImgsSize);

        Button saveUserSettings = new Button("saveUserSettings")
        {
            @Override
            public void onSubmit()
            {
                SqlSession session = MybatisUtil.getSessionFactory().openSession();
                try
                {
                    UsersMapper usersMapperCacheable = CachingFacade.getCacheableMapper(session, UsersMapper.class);
                    usersMapperCacheable.updateUser(user);

                    session.commit();
                }
                finally
                {
                    session.close();
                }
            }
        };
        userSettings.add(saveUserSettings);

        sidebarModules.add(new ProjectsSidebarModule("sidebarModule"));
        sidebarModules.add(new FriendsSidebarModule("sidebarModule"));
    }
}
