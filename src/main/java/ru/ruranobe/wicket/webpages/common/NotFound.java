package ru.ruranobe.wicket.webpages.common;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import ru.ruranobe.wicket.WicketApplication;
import ru.ruranobe.wicket.components.sidebar.FriendsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ProjectsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.RequisitesSidebarModule;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

public class NotFound extends SidebarLayoutPage
{
    @Override
    protected void onInitialize()
    {
        add(new BookmarkablePageLink("mainPage", WicketApplication.get().getHomePage(), null));
        sidebarModules.add(RequisitesSidebarModule.makeDefault());
        sidebarModules.add(new ProjectsSidebarModule());
        sidebarModules.add(new FriendsSidebarModule());
        super.onInitialize();
    }
}
