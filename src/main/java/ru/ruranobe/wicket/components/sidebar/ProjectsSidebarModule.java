package ru.ruranobe.wicket.components.sidebar;

import org.apache.wicket.AttributeModifier;
import ru.ruranobe.wicket.components.ProjectBannersList;

/**
 * Created by Samogot on 03.05.2015.
 */
public class ProjectsSidebarModule extends SidebarModuleBase
{

    public ProjectsSidebarModule()
    {
        super("sidebarModule", "all-projects", "Проекты");
        add(new ProjectBannersList("bannersList", null));
        moduleBody.add(AttributeModifier.replace("class", "actions banners"));
    }

}
