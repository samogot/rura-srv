package ru.ruranobe.wicket.webpages.base;

import ru.ruranobe.wicket.components.ContentsHolder;
import ru.ruranobe.wicket.components.sidebar.ContentsModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds new js/css files needed for admin pages
 */
public class AdminLayoutPage extends SidebarLayoutPage
{
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
