package ru.ruranobe.wicket.components.sidebar;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import ru.ruranobe.wicket.components.ContentsHolder;

import java.util.List;

/**
 * Created by samogot on 12.05.15.
 */
public class ContentsModule extends SidebarModuleBase
{
//    @Override
//    public void renderHead(IHeaderResponse response)
//    {
//        response.render(CssHeaderItem.forReference(new PackageResourceReference(this.getClass(), "ContentsModule.css")));
//        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(this.getClass(), "../ReinitAffix.js")));
//        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(this.getClass(), "ContentsModule.js")));
//    }

    public ContentsModule(String id, List<ContentsHolder> contents)
    {
        super(id, "contents", "Содержание");
        module.add(new AttributeAppender("class", " scrollspy"));
        moduleWrapper.add(new AttributeModifier("data-spy", "affix"));
        moduleBody.add(new ListView<ContentsHolder>("h2Repeater", contents)
        {
            @Override
            protected void populateItem(ListItem<ContentsHolder> item)
            {
                ContentsHolder ch = item.getModelObject();
                item.add(new ExternalLink("h2Link", ch.getUrl(), ch.getTitle()));
                item.add(new ListView<ContentsHolder>("h3Repeater", ch.getChildren())
                {
                    @Override
                    protected void populateItem(ListItem<ContentsHolder> item)
                    {
                        ContentsHolder ch = item.getModelObject();
                        item.add(new ExternalLink("h3Link", ch.getUrl(), ch.getTitle()));
                        item.add(new ListView<ContentsHolder>("h4Repeater", ch.getChildren())
                        {
                            @Override
                            protected void populateItem(ListItem<ContentsHolder> item)
                            {
                                ContentsHolder ch = item.getModelObject();
                                item.add(new ExternalLink("h4Link", ch.getUrl(), ch.getTitle()));
                            }

                            @Override
                            public boolean isVisible()
                            {
                                return !getModelObject().isEmpty();
                            }
                        });
                    }

                    @Override
                    public boolean isVisible()
                    {
                        return !getModelObject().isEmpty();
                    }
                });
            }
        });
    }
}
