package ru.ruranobe.wicket.components.sidebar;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class DownloadsSidebarModule extends SidebarModuleBase
{
    private static final int UPDATES_BY_PROJECT_ON_PAGE = 5;

    public DownloadsSidebarModule(PageParameters pageParameters)
    {
        super("sidebarModule", "downloads", "Скачать том");
        String url = String.format("%s/%s", pageParameters.get("project").toString(), pageParameters.get("volume").toString());
        add(new ExternalLink("fb2pic", "/d/fb2/" + url));
        add(new ExternalLink("fb2nopic", "/d/fb2/" + url + "?pic=0"));
        add(new ExternalLink("docx", "/d/docx/" + url));
        add(new ExternalLink("epub", "/d/epub/" + url));
        moduleBody.add(AttributeModifier.replace("class", "actions"));
    }
}
