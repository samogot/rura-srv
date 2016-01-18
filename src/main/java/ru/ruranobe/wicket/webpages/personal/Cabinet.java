package ru.ruranobe.wicket.webpages.personal;

import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.misc.Token;
import ru.ruranobe.misc.smtp.Email;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.additional.ChapterUrlDetails;
import ru.ruranobe.mybatis.entities.tables.*;
import ru.ruranobe.mybatis.mappers.BookmarksMapper;
import ru.ruranobe.mybatis.mappers.ChapterUrlDetailsMapper;
import ru.ruranobe.mybatis.mappers.UsersMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.LoginSession;
import ru.ruranobe.wicket.components.EmailPasswordRecoveryPanel;
import ru.ruranobe.wicket.components.LabelHideableOnNull;
import ru.ruranobe.wicket.components.sidebar.FriendsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ProjectsSidebarModule;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;
import ru.ruranobe.wicket.webpages.common.ProjectPage;
import ru.ruranobe.wicket.webpages.common.TextPage;
import ru.ruranobe.wicket.webpages.common.VolumePage;

import javax.mail.MessagingException;
import java.util.Arrays;
import java.util.List;

public class Cabinet extends SidebarLayoutPage
{
    public Cabinet()
    {
        setStatelessHint(true);

        final User user = LoginSession.get().getUser();
        if (user == null)
        {
            throw RuranobeUtils.getRedirectTo404Exception(this);
        }

        setDefaultModel(new CompoundPropertyModel<>(user));

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
       // avatarImage.add(new AttributeAppender("src", "ololo"));
        add(avatarImage);

        Label username = new Label("username");
        username.setRenderBodyOnly(true);
        add(username);

        Label realname = new LabelHideableOnNull("realname");
        realname.setRenderBodyOnly(true);
        add(realname);

        final Label email = new LabelHideableOnNull("email");
        email.setRenderBodyOnly(true);
        add(email);

        Label registrationDate = new LabelHideableOnNull("registrationDate");
        registrationDate.setRenderBodyOnly(true);
        add(registrationDate);

        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            BookmarksMapper bookmarksMapperCacheable = CachingFacade.getCacheableMapper(session, BookmarksMapper.class);
            final List<Bookmark> bookmarks = bookmarksMapperCacheable.getBookmarksExtendedByUser(user.getUserId());

            ListView<Bookmark> userBookmarksRepeater = new ListView<Bookmark>("userBookmarksRepeater", bookmarks)
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
                    BookmarkablePageLink projectUrl = new BookmarkablePageLink("projectUrl", ProjectPage.class,
                            projectPageParameters);
                    Label projectUrlText = new Label("projectUrlText", chapterUrlDetails.getProjectTitle());
                    projectUrlText.setRenderBodyOnly(true);
                    projectUrl.add(projectUrlText);
                    bookmarkForm.add(projectUrl);

                    PageParameters volumePageParameters = Volume.makeUrlParameters(chapterUrlDetails.getVolumeUrl().split("/", -1));
                    BookmarkablePageLink volumeUrl = new BookmarkablePageLink("volumeUrl", VolumePage.class,
                            volumePageParameters);
                    Label volumeUrlText = new Label("volumeUrlText", chapterUrlDetails.getVolumeTitle());
                    volumeUrlText.setRenderBodyOnly(true);
                    volumeUrl.add(volumeUrlText);
                    bookmarkForm.add(volumeUrl);

                    PageParameters chapterPageParameters = Chapter.makeUrlParameters(chapterUrlDetails.getChapterUrl().split("/", -1));
                    BookmarkablePageLink chapterUrl = new BookmarkablePageLink("chapterUrl", TextPage.class,
                            chapterPageParameters);
                    Label chapterUrlText = new Label("chapterUrlText", chapterUrlDetails.getChapterTitle());
                    chapterUrlText.setRenderBodyOnly(true);
                    chapterUrl.add(chapterUrlText);
                    bookmarkForm.add(chapterUrl);

                    Label bookmarkText = new Label("bookmarkText", bookmark.getParagraph().getParagraphText());
                    bookmarkForm.add(bookmarkText);

                    final String paragraphId = bookmark.getParagraphId();
                    BookmarkablePageLink bookmarkUrl = new BookmarkablePageLink("bookmarkUrl", TextPage.class,
                            Chapter.makeUrlParameters(chapterUrlDetails.getChapterUrl().split("/", -1)))
                    {
                        @Override
                        protected CharSequence getURL()
                        {
                            CharSequence url = super.getURL();
                            return url + "#" + paragraphId;
                        }
                    };
                    bookmarkForm.add(bookmarkUrl);

                    Button deleteBookmark = new Button("deleteBookmark")
                    {
                        @Override
                        public void onSubmit()
                        {
                            try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
                            {
                                BookmarksMapper bookmarksMapperCacheable = CachingFacade.getCacheableMapper(session, BookmarksMapper.class);
                                bookmarksMapperCacheable.deleteBookmark(bookmark.getBookmarkId());

                                session.commit();
                            }
                            throw new RedirectToUrlException(super.getPage().urlFor(super.getPage().getPageClass(), null).toString());
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

        StatelessForm userSettings = new StatelessForm("userSettings");
        add(userSettings);

        DropDownChoice<String> converterType = new DropDownChoice<>("converterType", Arrays.asList("fb2", "docx", "epub"));
        userSettings.add(converterType);

        DropDownChoice<String> navigationType = new DropDownChoice<>("navigationType", Arrays.asList("Главам", "Подглавам"));
        userSettings.add(navigationType);

        CheckBox convertWithImgs = new CheckBox("convertWithImgs");
        userSettings.add(convertWithImgs);

        CheckBox adult = new CheckBox("adult");
        userSettings.add(adult);

        CheckBox preferColoredImgs = new CheckBox("preferColoredImgs");
        userSettings.add(preferColoredImgs);

        TextField<Integer> convertImgsSize = new TextField<>("convertImgsSize");
        userSettings.add(convertImgsSize);

        Button saveUserSettings = new Button("saveUserSettings")
        {
            @Override
            public void onSubmit()
            {
                try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
                {
                    UsersMapper usersMapperCacheable = CachingFacade.getCacheableMapper(session, UsersMapper.class);
                    usersMapperCacheable.updateUser(user);

                    session.commit();
                }
            }
        };
        userSettings.add(saveUserSettings);


        StatelessForm<EmailFormModelObject> emailForm = new StatelessForm<>("emailForm",
                new CompoundPropertyModel<>(new EmailFormModelObject()));
        emailForm.setOutputMarkupId(true);
        emailForm.setMarkupId("settingsEmail");
        add(emailForm);

        PasswordTextField currentPassword = new PasswordTextField("currentPassword");
        emailForm.add(currentPassword);

        TextField newEmail = new TextField("newEmail");
        emailForm.add(newEmail);

        Button updateEmail = new Button("updateEmail")
        {
            @Override
            public void onSubmit()
            {
                EmailFormModelObject emailFormModelObject = (EmailFormModelObject) getForm().getModelObject();
                String password = emailFormModelObject.getCurrentPassword();
                String email = emailFormModelObject.getNewEmail();

                if (LoginSession.get().validatePassword(password))
                {
                    error("Введенный пароль не верен.");
                }
                else if (!Strings.isEmpty(email) && Email.isEmailSyntaxInvalid(email))
                {
                    error("Указан неверный адрес электронной почты.");
                }
                else if (email != null && email.length() > 255)
                {
                    error("Длина электронного адреса не должна превышать 255 символов.");
                }
                else
                {
                    try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
                    {
                        UsersMapper usersMapperCacheable = CachingFacade.getCacheableMapper(session, UsersMapper.class);
                        if (usersMapperCacheable.getUserByEmail(email) != null)
                        {
                            error("Пользователь с таким электронным адресом уже зарегистрирован в системе.");
                        }
                        else
                        {
                            User user = new User(LoginSession.get().getUser());

                            Token token = Token.valueOf(user.getUserId(), Email.ETERNITY_EXPIRATION_TIME);
                            user.setEmailToken(token.getTokenValue());
                            user.setEmailTokenDate(token.getTokenExpirationDate());
                            user.setEmailActivated(false);
                            try
                            {
                                Email.sendEmailActivationMessage(user.getEmail(), user.getEmailToken());
                                usersMapperCacheable.updateUser(user);
                            }
                            catch (Exception ex)
                            {
                                error("Отправка сообщения на указанный электронный адрес не удалась. Свяжитесь, пожалуйста, с администрацией сайта.");
                                session.rollback();
                            }
                            usersMapperCacheable.updateUser(user);
                            session.commit();
                            LoginSession.get().authenticate(user.getUsername(), user.getPass());

                            info("Электронный адрес был успешно изменен");
                        }
                    }
                }
            }
        };
        emailForm.add(updateEmail);

        add(new StatelessForm<Cabinet>("changePassword")
        {
            @Override
            public final void onSubmit()
            {
                try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
                {
                    User user = LoginSession.get().getUser();
                    if (!user.isEmailActivated())
                    {
                        error("Электронный адрес пользователя не был подтвержден.");
                    }
                    else if (user.getPassRecoveryToken() != null
                            && user.getPassRecoveryTokenDate().getTime() > System.currentTimeMillis())
                    {
                        error("На указанный электронной адрес уже было отправлено письмо.");
                    }
                    else
                    {
                        Token token = Token.valueOf(user.getUserId(), EXPIRATION_TIME_6_HOURS);
                        user.setPassRecoveryToken(token.getTokenValue());
                        user.setPassRecoveryTokenDate(token.getTokenExpirationDate());
                        CachingFacade.getCacheableMapper(session, UsersMapper.class).updateUser(user);
                        try
                        {
                            Email.sendPasswordRecoveryMessage(user.getEmail(), user.getPassRecoveryToken());
                            session.commit();
                        }
                        catch (MessagingException ex)
                        {
                            error("Отправка сообщения на указанный электронный адрес не удалась. Свяжитесь, пожалуйста, с администрацией сайта.");
                        }
                    }
                }
            }
        });
        add(new FeedbackPanel("feedback"));

        sidebarModules.add(new ProjectsSidebarModule());
        sidebarModules.add(new FriendsSidebarModule());
    }

	@Override
	protected String getPageTitle() {
		return "Личный кабинет - РуРанобэ";
	}

    private class EmailFormModelObject
    {
        private String currentPassword;
        private String newEmail;

        public EmailFormModelObject() {
        }

        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewEmail() {
            return newEmail;
        }

        public void setNewEmail(String newEmail) {
            this.newEmail = newEmail;
        }
    }

    private static final long EXPIRATION_TIME_6_HOURS = 21600000L;
}
