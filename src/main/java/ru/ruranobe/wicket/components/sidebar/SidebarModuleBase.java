package ru.ruranobe.wicket.components.sidebar;

import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Created by Samogot on 04.05.2015.
 */
public class SidebarModuleBase extends Panel
{
    protected WebMarkupContainer module, moduleWrapper, moduleHeading, moduleHeadingChevron, moduleBody;
    protected Label moduleHeadingName;

    public SidebarModuleBase(String id, String markupId)
    {
        this(id, markupId, null);
    }

    public SidebarModuleBase(String id, String markupId, String moduleName)
    {
        super(id);
        add(module = new TransparentWebMarkupContainer("module"));
        if (markupId != null)
        {
            module.setMarkupId(markupId + "-module");
        }
        module.add(moduleWrapper = new TransparentWebMarkupContainer("moduleWrapper"));
        moduleWrapper.add(moduleHeading = new WebMarkupContainer("moduleHeading"));
        moduleHeading.setVisible(moduleName != null);
        moduleHeading.add(moduleHeadingName = new Label("moduleHeadingName", moduleName));
        moduleHeading.add(moduleHeadingChevron = new WebMarkupContainer("moduleHeadingChevron"));
        moduleWrapper.add(moduleBody = new TransparentWebMarkupContainer("moduleBody"));
    }
}
