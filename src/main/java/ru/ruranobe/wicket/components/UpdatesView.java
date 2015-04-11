package ru.ruranobe.wicket.components;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import ru.ruranobe.mybatis.tables.Update;
import ru.ruranobe.wicket.RuraConstants;

public class UpdatesView extends ListView<Update> 
{
    public UpdatesView(String id, List<Update> updates)
    {
        super(id, updates);
    }
    
    @Override
    protected void populateItem(ListItem<Update> listItem)
    {
        Update update = listItem.getModelObject();

        String iconDivClassValue = RuraConstants.UPDATE_TYPE_TO_ICON_DIV_CLASS.get(update.getUpdateType());
        Date updateDateValue = update.getShowTime();                
        String updateTitleValue = update.getChapterTitle();

        WebMarkupContainer iconDivClass = new WebMarkupContainer("iconDivClass");
        iconDivClass.add(new AttributeAppender("class", new Model<String>(iconDivClassValue)));
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Label updateDate = new Label("updateDate", sdf.format(updateDateValue));
        ExternalLink updateLink = new ExternalLink("updateLink", "/"+update.getChapterUrl(), update.getVolumeTitle());
        if (update.getChapterId() == null)
        {
            updateLink = new ExternalLink("updateLink", "/"+update.getVolumeUrl(), update.getVolumeTitle());                    
            updateTitleValue = update.getVolumeTitle();
        }
        Label updateTitle = new Label("updateTitle", updateTitleValue);

        listItem.add(iconDivClass);
        listItem.add(updateDate);
        listItem.add(updateLink);
        listItem.add(updateTitle);
    }
}
