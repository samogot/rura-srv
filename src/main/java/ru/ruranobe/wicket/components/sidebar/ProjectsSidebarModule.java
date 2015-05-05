package ru.ruranobe.wicket.components.sidebar;

import org.apache.wicket.behavior.AttributeAppender;
import ru.ruranobe.wicket.components.ProjectBannersList;

/**
 * Created by Samogot on 03.05.2015.
 */
public class ProjectsSidebarModule extends SidebarModuleBase
{

    public ProjectsSidebarModule(String id)
    {
        super(id, "all-projects", "Проекты");
        moduleBody.add(new ProjectBannersList("bannersList", null));
        moduleBody.add(new AttributeAppender("class", " banners"));
    }

}
