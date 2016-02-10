package ru.ruranobe.wicket.components.sidebar;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.wicket.LoginSession;
import ru.ruranobe.wicket.webpages.admin.Orphus;

public class ActionsSidebarModule extends SidebarModuleBase
{
    private String project = null;

	public ActionsSidebarModule(Class editClass, PageParameters pageParameters) {
		super("sidebarModule", "actions", "Действия");
		project = pageParameters != null ? pageParameters.get("project").toOptionalString() : null;
		moduleBody.add(new BookmarkablePageLink("edit", editClass, pageParameters));
		moduleBody.add(new BookmarkablePageLink("orphus", Orphus.class, pageParameters));
	}

    @Override
    public boolean isVisible()
    {
        return project != null && LoginSession.get().isProjectEditAllowedByUser(project)
               || LoginSession.get().hasRole("ADMIN");
    }
}
