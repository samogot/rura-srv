package ru.ruranobe.wicket.webpages;

import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.misc.smtp.Email;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.misc.Token;
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

        SqlSession session = MybatisUtil.getSessionFactory().openSession();
        try
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
                    SqlSession session = MybatisUtil.getSessionFactory().openSession();
                    try
                    {
                        ChapterUrlDetailsMapper chapterUrlDetailsMapperCacheable = CachingFacade.getCacheableMapper(session, ChapterUrlDetailsMapper.class);
                        chapterUrlDetails = chapterUrlDetailsMapperCacheable.getChapterUrlDetailsByChapter(bookmark.getChapterId());
                    }
                    finally
                    {
                        session.close();
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
                    BookmarkablePageLink chapterUrl = new BookmarkablePageLink("chapterUrl", Text.class,
                            chapterPageParameters);
                    Label chapterUrlText = new Label("chapterUrlText", chapterUrlDetails.getChapterTitle());
                    chapterUrlText.setRenderBodyOnly(true);
                    chapterUrl.add(chapterUrlText);
                    bookmarkForm.add(chapterUrl);

                    Label bookmarkText = new Label("bookmarkText", bookmark.getParagraph().getParagraphText());
                    bookmarkForm.add(bookmarkText);

                    final String paragraphId = bookmark.getParagraphId();
                    BookmarkablePageLink bookmarkUrl = new BookmarkablePageLink("bookmarkUrl", Text.class,
                            Chapter.makeUrlParameters(chapterUrlDetails.getChapterUrl().split("/", -1)))
                    {
                        @Override
                        protected CharSequence getURL()
                        {
                            CharSequence url = super.getURL();
                            return url+"#"+paragraphId;
                        }
                    };
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
        finally
        {
            session.close();
        }

        StatelessForm userSettings = new StatelessForm("userSettings");
        add(userSettings);

        DropDownChoice<String> converterType = new DropDownChoice<String>("converterType", Arrays.asList("fb2", "docx", "epub"));
        userSettings.add(converterType);

        DropDownChoice<String> navigationType = new DropDownChoice<String>("navigationType", Arrays.asList("Главам", "Подглавам"));
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


        StatelessForm<EmailFormModelObject> emailForm = new StatelessForm<EmailFormModelObject>("emailForm",
                new CompoundPropertyModel<EmailFormModelObject>(new EmailFormModelObject()));
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

                if (((LoginSession) LoginSession.get()).validatePassword(password))
                {
                    error("Введенный пароль не верен.");
                }
                else if (!Strings.isEmpty(email) && !Email.isEmailSyntaxValid(email))
                {
                    error("Указан неверный адрес электронной почты.");
                }
                else if (email != null && email.length() > 255)
                {
                    error("Длина электронного адреса не должна превышать 255 символов.");
                }
                else
                {
                    SqlSession session = MybatisUtil.getSessionFactory().openSession();
                    try
                    {
                        UsersMapper usersMapperCacheable = CachingFacade.getCacheableMapper(session, UsersMapper.class);
                        if (usersMapperCacheable.getUserByEmail(email) != null)
                        {
                            error("Пользователь с таким электронным адресом уже зарегистрирован в системе.");
                        }
                        else
                        {
                            User user = new User(((LoginSession) LoginSession.get()).getUser());

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
                    finally
                    {
                        session.close();
                    }
                }
            }
        };
        emailForm.add(updateEmail);

        add(new EmailPasswordRecoveryPanel("emailPasswordRecoveryPanel"));

        sidebarModules.add(new ProjectsSidebarModule("sidebarModule"));
        sidebarModules.add(new FriendsSidebarModule("sidebarModule"));
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
}
