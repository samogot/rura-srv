package ru.ruranobe.wicket.components.sidebar;

import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.additional.VolumeDownloadInfo;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;

public class DownloadsSidebarModule extends SidebarModuleBase
{

    public DownloadsSidebarModule(String id, PageParameters pageParameters)
    {
        super(id, "downloads-mobile");
        init(pageParameters);
    }

    public DownloadsSidebarModule(PageParameters pageParameters)
    {
        super("sidebarModule", "downloads", "Скачать том");
        init(pageParameters);
    }

    private void init(PageParameters pageParameters)
    {
        String url = String.format("%s/%s", pageParameters.get("project").toString(), pageParameters.get("volume").toString());
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            VolumesMapper volumesMapper = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
            VolumeDownloadInfo volumeDownloadInfo = volumesMapper.getVolumeDownloadInfoByUrl(url);
            setVisible(volumeDownloadInfo.isDownload());
            String titleSuffix = !volumeDownloadInfo.isImages() ? WITHOUT_IMAGES_TITLE : volumeDownloadInfo.isColors() ? WITH_COLORS_TITLE : WITH_IMAGES_TITLE;
            String text = !volumeDownloadInfo.isImages() ? WITHOUT_IMAGES_TEXT : volumeDownloadInfo.isColors() ? WITH_COLORS_TEXT : WITH_IMAGES_TEXT;
            add(new ExternalLink("fb2", "/d/fb2/" + url).add(AttributeModifier.replace("title", DL_FB2 + titleSuffix)));
            add(new ExternalLink("docx", "/d/docx/" + url).add(AttributeModifier.replace("title", DL_DOCX + titleSuffix)));
            add(new ExternalLink("epub", "/d/epub/" + url).add(AttributeModifier.replace("title", DL_EPUB + titleSuffix)));
            add(new Label("fb2-text", text));
            add(new Label("docx-text", text));
            add(new Label("epub-text", text));
            add(new CheckBox("images", Model.of(true)).setVisible(volumeDownloadInfo.isImages()));
            add(new CheckBox("bw", Model.of(false)).setVisible(volumeDownloadInfo.isColors()));
            moduleBody.add(AttributeModifier.replace("class", "actions"));
        }
    }

    private static final String DL_FB2 = "Скачать fb2";
    private static final String DL_DOCX = "Скачать docx";
    private static final String DL_EPUB = "Скачать epub";
    private static final String WITH_COLORS_TITLE = " c цветными иллюстрациями вместо черно-белых";
    private static final String WITH_IMAGES_TITLE = " c иллюстрациями";
    private static final String WITHOUT_IMAGES_TITLE = " без иллюстраций";
    private static final String WITH_COLORS_TEXT = "С цвет. иллюстрациями";
    private static final String WITH_IMAGES_TEXT = "С иллюстрациями";
    private static final String WITHOUT_IMAGES_TEXT = "Без иллюстраций";
}
