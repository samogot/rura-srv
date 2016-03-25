package ru.ruranobe.wicket.components.sidebar;

import org.apache.wicket.AttributeModifier;

/**
 * Created by Samogot on 04.05.2015.
 */
public class MiniSearchSidebarModule extends SidebarModuleBase
{
    public MiniSearchSidebarModule()
    {
        super("sidebarModule", "mini-search");
        moduleBody.add(AttributeModifier.replace("class", "miniSearch"));
    }
}
