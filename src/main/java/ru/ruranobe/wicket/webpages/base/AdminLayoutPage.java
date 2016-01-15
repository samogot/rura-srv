package ru.ruranobe.wicket.webpages.base;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import ru.ruranobe.wicket.components.ContentsHolder;
import ru.ruranobe.wicket.components.sidebar.ContentsModule;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Adds new js/css files needed for admin pages
 */
public class AdminLayoutPage extends SidebarLayoutPage
{
    @Override
    public void renderHead(IHeaderResponse response)
    {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forScript(
                String.format("window.serverTimeZone = '%s';", ZoneId.systemDefault()), "timezone"));
    }

    public void addContentsItem(String href, String title)
    {
        contentsHolders.add(new ContentsHolder(href, title));
    }

    @Override
    protected void onInitialize()
    {
        super.onInitialize();
        sidebarModules.add(new ContentsModule(contentsHolders));
    }

    private List<ContentsHolder> contentsHolders = new ArrayList<>();
}
