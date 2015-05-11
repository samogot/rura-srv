package ru.ruranobe.wicket.webpages.base;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import ru.ruranobe.wicket.components.sidebar.SidebarMiniSearch;
import ru.ruranobe.wicket.components.sidebar.SidebarModuleBase;
import ru.ruranobe.wicket.webpages.VolumePage;
import ru.ruranobe.wicket.webpages.VolumeTextPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samogot on 04.05.2015.
 */
public class SidebarLayoutPage extends BaseLayoutPage
{
    protected List<SidebarModuleBase> sidebarModules = new ArrayList<SidebarModuleBase>();
    protected WebMarkupContainer textPageUtils;
    protected BookmarkablePageLink homeTextLink;
    protected BookmarkablePageLink prevTextLink;
    protected BookmarkablePageLink nextTextLink;

    public SidebarLayoutPage()
    {
        add(new SidebarMiniSearch("miniSearch"));
        add(new ListView<SidebarModuleBase>("sidebarModuleRepeater", sidebarModules)
        {
            @Override
            protected void populateItem(ListItem<SidebarModuleBase> item)
            {
                item.add(item.getModelObject());
            }
        });
        add(textPageUtils = new WebMarkupContainer("textPageUtils"));
        textPageUtils.setVisible(false);
        textPageUtils.add(homeTextLink = new BookmarkablePageLink("homeTextLink", VolumePage.class));
        textPageUtils.add(prevTextLink = new BookmarkablePageLink("prevTextLink", VolumeTextPage.class));
        textPageUtils.add(nextTextLink = new BookmarkablePageLink("nextTextLink", VolumeTextPage.class));
    }
}
