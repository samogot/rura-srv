package ru.ruranobe.wicket.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by samogot on 12.05.15.
 */
public class ContentsHolder implements Serializable
{
    private String url;
    private String title;
    private List<ContentsHolder> children;

    public ContentsHolder(String url, String title)
    {
        this.url = url;
        this.title = title;
        this.children = new ArrayList<>();
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public List<ContentsHolder> getChildren()
    {
        return children;
    }

    public void setChildren(List<ContentsHolder> children)
    {
        this.children = children;
    }

    public void addChild(ContentsHolder child)
    {
        children.add(child);
    }
}
