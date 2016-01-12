package ru.ruranobe.wicket.webpages.common;

import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.UpdatesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.RuraConstants;
import ru.ruranobe.wicket.components.UpdatesWideList;
import ru.ruranobe.wicket.components.sidebar.ActionsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.FriendsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ProjectsSidebarModule;
import ru.ruranobe.wicket.webpages.admin.GlobalEdit;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

import java.util.ArrayList;
import java.util.List;

public class Updates extends SidebarLayoutPage
{
    private static final int UPDATES_COUNT_ON_PAGE = 50;
    private static final int NUMBER_OF_PAGES_ON_UPDATE_LIST = 5;

    @SuppressWarnings("unused")
    public Updates()
    {
        initComponents(null, 1, null, null);
    }

    @SuppressWarnings("unused")
    public Updates(final PageParameters parameters)
    {
        String projectString = parameters.get("project").toOptionalString();
        String volumeString = parameters.get("volume").toOptionalString();
        String searchType = parameters.get("type").toOptionalString();
        String pageString = parameters.get("page").toOptionalString();

        int page;
        Integer projectId = null;
        Integer volumeId = null;
        searchType = RuraConstants.UPDATE_TYPE_LIST.contains(searchType) ? searchType : null;
        try
        {
            page = (pageString == null) ? 1 : Integer.parseInt(pageString);
            projectId = projectString == null ? null : Integer.parseInt(projectString);
            volumeId = volumeString == null ? null : Integer.parseInt(volumeString);
        }
        catch (Exception ex)
        {
            page = 1;
        }
        initComponents(searchType, page, volumeId, projectId);
    }

    private void initComponents(final String searchType, final int page, final Integer volumeId, final Integer projectId)
    {
        add(
                LinkToUpdatesPage.searchType("first", projectId, volumeId, page, null)
                        .add(new AttributeModifier("class", ((searchType == null) ? "first active" : "first")))
        );

        add(
                LinkToUpdatesPage.searchType("third", projectId, volumeId, page, RuraConstants.UPDATE_TYPE_PROOFREAD)
                        .add(new AttributeModifier("class", ((RuraConstants.UPDATE_TYPE_PROOFREAD.equals(searchType)) ? "third active" : "third")))
        );

        add(
                LinkToUpdatesPage.searchType("fifth", projectId, volumeId, page, RuraConstants.UPDATE_TYPE_IMAGES)
                        .add(new AttributeModifier("class", ((RuraConstants.UPDATE_TYPE_IMAGES.equals(searchType)) ? "fifth active" : "fifth")))
        );

        add(
                LinkToUpdatesPage.searchType("sixth", projectId, volumeId, page, RuraConstants.UPDATE_TYPE_PUBLISH)
                        .add(new AttributeModifier("class", ((RuraConstants.UPDATE_TYPE_PUBLISH.equals(searchType)) ? "sixth active" : "sixth")))
        );

        int numberOfPages;
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            int updatesCount = CachingFacade.getCacheableMapper(session, UpdatesMapper.class)
                    .getUpdatesCountBy(projectId, volumeId, searchType);
            numberOfPages = (updatesCount / UPDATES_COUNT_ON_PAGE) + (((updatesCount % UPDATES_COUNT_ON_PAGE) == 0) ? 0 : 1);
        }

        addPaginator("paginator1", searchType, page, volumeId, projectId, numberOfPages);
        add(new UpdatesWideList("updatesList", projectId, volumeId, searchType, (page - 1) * UPDATES_COUNT_ON_PAGE, UPDATES_COUNT_ON_PAGE));
        addPaginator("paginator2", searchType, page, volumeId, projectId, numberOfPages);

        sidebarModules.add(new ActionsSidebarModule(GlobalEdit.class, null));
        sidebarModules.add(new ProjectsSidebarModule());
        sidebarModules.add(new FriendsSidebarModule());
    }

    private void addPaginator(final String paginatorComponentId, final String searchType, final int page, final Integer volumeId, final Integer projectId, final int numberOfPages)
    {
        WebMarkupContainer parent = new WebMarkupContainer(paginatorComponentId);

        StatelessLink firstPageLink = LinkToUpdatesPage.page("firstPageLink", projectId, volumeId, 1, searchType);
        WebMarkupContainer firstPage = new WebMarkupContainer("firstPage");
        if (page == 1)
        {
            firstPage.add(new AttributeAppender("class", "disabled"));
            firstPageLink.setVisible(false);
        }

        StatelessLink lastPageLink = LinkToUpdatesPage.page("lastPageLink", projectId, volumeId, numberOfPages, searchType);
        WebMarkupContainer lastPage = new WebMarkupContainer("lastPage");
        if (page == numberOfPages || numberOfPages == 0)
        {
            lastPage.add(new AttributeAppender("class", "disabled"));
            lastPageLink.setVisible(false);
        }

        parent.add(firstPage.add(firstPageLink));
        parent.add(lastPage.add(lastPageLink));

        List<StatelessLink> references = new ArrayList<>();
        AttributeAppender activeAppender = new AttributeAppender("class", "active");
        if (page <= NUMBER_OF_PAGES_ON_UPDATE_LIST)
        {
            int endLoop = Math.min(NUMBER_OF_PAGES_ON_UPDATE_LIST, numberOfPages);
            for (int i = 1; i <= endLoop; ++i)
            {
                StatelessLink link = LinkToUpdatesPage.page("updatesPageLink", projectId, volumeId, i, searchType);
                link.add(new Label("updatesPageText", Integer.toString(i)));
                if (i == page)
                {
                    link.add(activeAppender);
                }
                references.add(link);
            }
            if (numberOfPages > NUMBER_OF_PAGES_ON_UPDATE_LIST)
            {
                StatelessLink linkToNextPage = LinkToUpdatesPage.page("updatesPageLink", projectId, volumeId, NUMBER_OF_PAGES_ON_UPDATE_LIST + 1, searchType);
                linkToNextPage.add(new Label("updatesPageText", "..."));
                references.add(linkToNextPage);

                StatelessLink linkToLastPage = LinkToUpdatesPage.page("updatesPageLink", projectId, volumeId, numberOfPages, searchType);
                linkToLastPage.add(new Label("updatesPageText", Integer.toString(numberOfPages)));
                references.add(linkToLastPage);
            }
        }
        else if (page < numberOfPages - NUMBER_OF_PAGES_ON_UPDATE_LIST)
        {
            StatelessLink linkToFirstPage = LinkToUpdatesPage.page("updatesPageLink", projectId, volumeId, 1, searchType);
            linkToFirstPage.add(new Label( "updatesPageText", "1"));
            references.add(linkToFirstPage);

            StatelessLink linkToPrevPage = LinkToUpdatesPage.page("updatesPageLink", projectId, volumeId, page - NUMBER_OF_PAGES_ON_UPDATE_LIST, searchType);
            linkToPrevPage.add(new Label("updatesPageText", "..."));
            references.add(linkToPrevPage);

            for (int i = page; i <= page + NUMBER_OF_PAGES_ON_UPDATE_LIST - 1; ++i)
            {
                StatelessLink link = LinkToUpdatesPage.page("updatesPageLink", projectId, volumeId, i, searchType);
                link.add(new Label("updatesPageText", Integer.toString(i)));
                if (i == page)
                {
                    link.add(activeAppender);
                }
                references.add(link);
            }

            StatelessLink linkToNextPage = LinkToUpdatesPage.page("updatesPageLink", projectId, volumeId, page + NUMBER_OF_PAGES_ON_UPDATE_LIST, searchType);
            linkToNextPage.add(new Label("updatesPageText", "..."));
            references.add(linkToNextPage);

            StatelessLink linkToLastPage = LinkToUpdatesPage.page("updatesPageLink", projectId, volumeId, numberOfPages, searchType);
            linkToLastPage.add(new Label("updatesPageText", Integer.toString(numberOfPages)));
            references.add(linkToLastPage);
        }
        else
        {
            StatelessLink linkToFirstPage = LinkToUpdatesPage.page("updatesPageLink", projectId, volumeId, 1, searchType);
            linkToFirstPage.add(new Label( "updatesPageText", "1"));
            references.add(linkToFirstPage);

            StatelessLink linkToPrevPage = LinkToUpdatesPage.page("updatesPageLink", projectId, volumeId, page - NUMBER_OF_PAGES_ON_UPDATE_LIST, searchType);
            linkToPrevPage.add(new Label("updatesPageText", "..."));
            references.add(linkToPrevPage);

            for (int i = page; i <= numberOfPages; ++i)
            {
                StatelessLink link = LinkToUpdatesPage.page("updatesPageLink", projectId, volumeId, i, searchType);
                link.add(new Label("updatesPageText", Integer.toString(i)));
                if (i == page)
                {
                    link.add(activeAppender);
                }
                references.add(link);
            }
        }

        parent.add(new ListView<StatelessLink>("updatesPaginator", references)
        {
            @Override
            protected void populateItem(final ListItem<StatelessLink> listItem)
            {
                listItem.add(listItem.getModelObject());
            }

            @Override
            public boolean isVisible()
            {
                return super.isVisible() && getList().size() != 1;
            }
        });

        add(parent);
    }

    @Override
    protected String getPageTitle()
    {
        return "Обновления - РуРанобэ";
    }

    private static class LinkToUpdatesPage extends StatelessLink
    {
        private Integer projectId;
        private Integer volumeId;
        private int page;
        private String updateType;

        private LinkToUpdatesPage(String id)
        {
            super(id);
        }

        // link to concrete page of Updates
        static LinkToUpdatesPage page(String componentId, Integer projectId, Integer volumeId, int page, String updateType)
        {
            LinkToUpdatesPage link = new LinkToUpdatesPage(componentId);
            link.page = page;
            link.updateType = updateType;
            link.projectId = projectId;
            link.volumeId = volumeId;
            return link;
        }

        // creates search type link. By search type link i mean the link with small icon image and text such as "Опубликовано", "Иллюстрации".
        static LinkToUpdatesPage searchType(String componentId, Integer projectId, Integer volumeId, int page, String updateType)
        {
            LinkToUpdatesPage link = new LinkToUpdatesPage(componentId);
            link.projectId = projectId;
            link.volumeId = volumeId;
            link.page = page;
            link.updateType = updateType;
            return link;
        }

        @Override
        public void onClick()
        {
            PageParameters p = new PageParameters();
            p.add("page", page);
            if (updateType != null)
            {
                p.add("type", updateType);
            }
            if (projectId != null)
            {
                p.add("project", projectId);
            }
            if (volumeId != null)
            {
                p.add("volume", volumeId);
            }
            setResponsePage(Updates.class, p);
        }
    }
}
