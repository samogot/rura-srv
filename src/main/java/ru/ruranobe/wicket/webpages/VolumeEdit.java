package ru.ruranobe.wicket.webpages;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.wicket.components.sidebar.FriendsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ProjectsSidebarModule;
import ru.ruranobe.wicket.webpages.base.SidebarLayoutPage;

public class VolumeEdit extends SidebarLayoutPage
{

    public VolumeEdit(final PageParameters parameters)
    {
        /*if (parameters.getNamedKeys().size() != 1)
        {
            throw RuranobeUtils.REDIRECT_TO_404;
        }
        
        String projectUrl = parameters.getNamedKeys().iterator().next();
        String volumeShortUrl = parameters.get(projectUrl).toString();
        String volumeUrl = projectUrl + "/" + volumeShortUrl;
        
        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        
        VolumesMapper volumesMapper = session.getMapper(VolumesMapper.class);
        Volume volume = volumesMapper.getVolumeByUrl(volumeUrl);
        
        if (volume == null)
        {
            throw RuranobeUtils.REDIRECT_TO_404;
        }
        
        ChaptersMapper chaptersMapper = session.getMapper(ChaptersMapper.class);
        List<Chapter> chapters = chaptersMapper.getChaptersByVolumeId(volume.getVolumeId());
        
        ChapterImagesMapper chapterImagesMapper = session.getMapper(ChapterImagesMapper.class);
        List<ChapterImage> chapterImages = chapterImagesMapper.getChapterImagesByVolumeId(volume.getVolumeId());
        
        session.close();*/
        sidebarModules.add(new ProjectsSidebarModule("sidebarModule"));
        sidebarModules.add(new FriendsSidebarModule("sidebarModule"));
    }

}
