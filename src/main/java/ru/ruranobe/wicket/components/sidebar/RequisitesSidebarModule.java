package ru.ruranobe.wicket.components.sidebar;

import org.apache.wicket.AttributeModifier;

public class RequisitesSidebarModule extends SidebarModuleBase
{
    public RequisitesSidebarModule()
    {
        super("sidebarModule", "requisites", "Реквизиты");
        moduleBody.add(AttributeModifier.replace("class", "actions"));
    }
}
