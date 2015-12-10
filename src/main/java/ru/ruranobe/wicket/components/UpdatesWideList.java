package ru.ruranobe.wicket.components;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.UpdatesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.entities.tables.Update;
import ru.ruranobe.wicket.RuraConstants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UpdatesWideList extends Panel
{

    public UpdatesWideList(String id, Integer projectId, Integer volumeId, String updateType, Integer limitFrom, Integer limitTo)
    {
        super(id);

        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        try
        {
            UpdatesMapper updatesMapperCacheable = CachingFacade.getCacheableMapper(session, UpdatesMapper.class);
            List<Update> updates = updatesMapperCacheable.getLastUpdatesBy(projectId, volumeId, updateType, limitFrom, limitTo);
            ListView<Update> updatesView = new ListView<Update>("updatesView", updates)
            {

                @Override
                protected void populateItem(ListItem<Update> listItem)
                {
                    Update update = listItem.getModelObject();

                    String iconDivClassValue = RuraConstants.UPDATE_TYPE_TO_ICON_CLASS.get(update.getUpdateType());
                    Date updateDateValue = update.getShowTime();
                    String updateTitleValue = update.getChapterShortTitle();
                    if (updateTitleValue == null)
                    {
                        updateTitleValue = "Весь том";
                    }

                    WebMarkupContainer iconDivClass = new WebMarkupContainer("iconDivClass");
                    iconDivClass.add(new AttributeAppender("class", " " + iconDivClassValue));
                    listItem.add(iconDivClass);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    listItem.add(new Label("updateDate", sdf.format(updateDateValue)));
                    BookmarkablePageLink updateLink = update.makeBookmarkablePageLink("updateLink");
                    updateLink.setBody(new Model<String>(update.getVolumeTitle()));
                    listItem.add(updateLink);
                    listItem.add(new Label("updateTitle", updateTitleValue));
                }
            };
            add(updatesView);
        }
        finally
        {
            session.close();
        }
    }
}
