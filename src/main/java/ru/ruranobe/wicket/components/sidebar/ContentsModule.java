package ru.ruranobe.wicket.components.sidebar;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;

/**
 * Created by samogot on 12.05.15.
 */
public class ContentsModule extends SidebarModuleBase
{

    public ContentsModule(String id)
    {
        super(id, "contents", "Содержание");
        module.add(new AttributeAppender("class", " scrollspy"));
        moduleWrapper.add(new AttributeModifier("data-spy", "affix"));
    }
}
