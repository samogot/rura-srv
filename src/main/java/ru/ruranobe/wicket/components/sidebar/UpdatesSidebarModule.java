package ru.ruranobe.wicket.components.sidebar;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Update;
import ru.ruranobe.mybatis.mappers.UpdatesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.RuraConstants;
import ru.ruranobe.wicket.webpages.common.Updates;

import java.text.SimpleDateFormat;
import java.util.List;

public class UpdatesSidebarModule extends SidebarModuleBase {
    private static final int UPDATES_BY_PROJECT_ON_PAGE = 5;

    public UpdatesSidebarModule(Integer projectId) {
        super("sidebarModule", "updates", "Обновления серии");
        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        try (SqlSession session = sessionFactory.openSession()) {
            UpdatesMapper updatesMapperCacheable = CachingFacade.getCacheableMapper(session, UpdatesMapper.class);
            List<Update> updates = updatesMapperCacheable.
                    getLastUpdatesBy(projectId, null, null, 0, UPDATES_BY_PROJECT_ON_PAGE);

            ListView<Update> updatesView = new ListView<Update>("updatesList", updates) {
                @Override
                protected void populateItem(ListItem<Update> listItem) {
                    Update update = listItem.getModelObject();
                    String iconClass = RuraConstants.UPDATE_TYPE_TO_ICON_CLASS.get(update.getUpdateType());
                    WebMarkupContainer updateContainerWithIcon = new WebMarkupContainer("updateContainerWithIcon");
                    updateContainerWithIcon.add(new AttributeModifier("class", iconClass));
                    BookmarkablePageLink updateLink = update.makeBookmarkablePageLink("updateLink");
                    updateLink.setBody(new Model<>(update.getShortTitle()));
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    updateLink.add(new AttributeModifier("title", String.format("%s %s: %s%s",
                            sdf.format(update.getShowTime()),
                            update.getUpdateType(),
                            update.getVolumeTitleShort(),
                            update.getChapterId() == null ? "" : " - " + update.getChapterTitle())));
                    updateContainerWithIcon.add(updateLink);
                    listItem.add(updateContainerWithIcon);
                }
            };
            moduleBody.add(updatesView);
            PageParameters p = new PageParameters();
            p.add("project", projectId);
            BookmarkablePageLink<Updates> moreLink = new BookmarkablePageLink<>("moreLink", Updates.class, p);
            moduleBody.add(moreLink);
        }
    }
}
