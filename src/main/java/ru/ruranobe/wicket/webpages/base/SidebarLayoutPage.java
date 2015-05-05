package ru.ruranobe.wicket.webpages.base;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import ru.ruranobe.wicket.components.sidebar.SidebarMiniSearch;
import ru.ruranobe.wicket.components.sidebar.SidebarModuleBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samogot on 04.05.2015.
 */
public class SidebarLayoutPage extends BaseLayoutPage
{
    protected List<SidebarModuleBase> sidebarModules = new ArrayList<SidebarModuleBase>();

    public List<SidebarModuleBase> getSidebarModules()
    {
        return sidebarModules;
    }

    public void setSidebarModules(List<SidebarModuleBase> sidebarModules)
    {
        this.sidebarModules = sidebarModules;
    }

    @Override
    protected void onInitialize()
    {
        super.onInitialize();
        add(new SidebarMiniSearch("miniSearch"));
        add(new ListView<SidebarModuleBase>("sidebarModuleRepeater", sidebarModules)
        {
            @Override
            protected void populateItem(ListItem<SidebarModuleBase> item)
            {
                item.add(item.getModelObject());
            }
        });
    }
}
