package ru.ruranobe.wicket.components.sidebar;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Created by Samogot on 04.05.2015.
 */
public class SidebarModuleBase extends Panel
{
    protected WebMarkupContainer module, moduleHeading, moduleHeadingChevron, moduleBody;
    protected Label moduleHeadingName;

    public SidebarModuleBase(String id, String markupId, String moduleName)
    {
        super(id);
        add(module = new WebMarkupContainer("module"));
        if (markupId != null) module.setMarkupId(markupId + "-module");
        module.add(moduleHeading = new WebMarkupContainer("moduleHeading"));
        moduleHeading.add(moduleHeadingName = new Label("moduleHeadingName", moduleName));
        moduleHeading.add(moduleHeadingChevron = new WebMarkupContainer("moduleHeadingChevron"));
        module.add(moduleBody = new WebMarkupContainer("moduleBody"));
    }
}
