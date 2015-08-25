package ru.ruranobe.mybatis.tables;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Created by Samogot on 07.05.2015.
 */
public abstract class PageRepresentable
{
    public abstract Class getLinkClass();

    public abstract PageParameters getUrlParameters();

    public BookmarkablePageLink makeBookmarkablePageLink(String name)
    {
        return new BookmarkablePageLink(name, getLinkClass(), getUrlParameters());
    }

    public String getBookmarkablePageUrlString(Page relativeTo)
    {
        return relativeTo.urlFor(getLinkClass(), getUrlParameters()).toString();
    }
}
