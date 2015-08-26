package ru.ruranobe.wicket.webpages;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import ru.ruranobe.wicket.WicketApplication;
import ru.ruranobe.wicket.components.sidebar.FriendsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ProjectsSidebarModule;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

public class NotFound extends SidebarLayoutPage
{
    @Override
    protected void onInitialize()
    {
        add(new BookmarkablePageLink("mainPage", WicketApplication.get().getHomePage(), null));
        sidebarModules.add(new ProjectsSidebarModule("sidebarModule"));
        sidebarModules.add(new FriendsSidebarModule("sidebarModule"));
        super.onInitialize();
    }
}
