package ru.ruranobe.wicket.webpages;

import ru.ruranobe.wicket.components.sidebar.FriendsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ProjectsSidebarModule;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

public class Cabinet extends SidebarLayoutPage
{
    public Cabinet()
    {
        sidebarModules.add(new ProjectsSidebarModule("sidebarModule"));
        sidebarModules.add(new FriendsSidebarModule("sidebarModule"));
    }
}
