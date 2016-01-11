package ru.ruranobe.wicket.webpages.common;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import ru.ruranobe.wicket.components.ProjectBannersList;
import ru.ruranobe.wicket.components.UpdatesWideList;
import ru.ruranobe.wicket.webpages.base.BaseLayoutPage;

public class HomePage extends BaseLayoutPage
{
    private static final int COUNT_OF_PROJECTS_ON_PAGE = 12;
    private static final int COUNT_OF_UPDATES_ON_PAGE = 10;

    @Override
    protected void onInitialize()
    {
        setStatelessHint(true);
        super.onInitialize();
        add(new ProjectBannersList("bannersList", COUNT_OF_PROJECTS_ON_PAGE));
        add(new BookmarkablePageLink("allProjects", FullProjects.class));
        add(new UpdatesWideList("updatesList", null, null, null, 0, COUNT_OF_UPDATES_ON_PAGE));
        add(new BookmarkablePageLink("allUpdates", Updates.class));
    }
}
