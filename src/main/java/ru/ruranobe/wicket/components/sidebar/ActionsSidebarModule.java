package ru.ruranobe.wicket.components.sidebar;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.wicket.webpages.Orphus;

@AuthorizeAction(action = "RENDER", roles = {"ADMIN"})
public class ActionsSidebarModule extends SidebarModuleBase
{
    private static final int UPDATES_BY_PROJECT_ON_PAGE = 5;

    public ActionsSidebarModule(Class editClass, PageParameters pageParameters)
    {
        super("sidebarModule", "actions", "Действия");
        moduleBody.add(new BookmarkablePageLink("edit", editClass, pageParameters));
        moduleBody.add(new BookmarkablePageLink("orphus", Orphus.class, pageParameters));
    }
}
