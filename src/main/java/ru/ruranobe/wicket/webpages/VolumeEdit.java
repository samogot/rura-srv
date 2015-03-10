package ru.ruranobe.wicket.webpages;

import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ChapterImagesMapper;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.tables.Chapter;
import ru.ruranobe.mybatis.tables.ChapterImage;
import ru.ruranobe.mybatis.tables.Volume;

public class VolumeEdit extends WebPage
{
    
    public VolumeEdit(final PageParameters parameters)
    {
        if (parameters.getNamedKeys().size() != 1)
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
        
        session.close();
    }
    
}
