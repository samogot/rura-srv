package ru.ruranobe.mybatis.mappers;

import java.util.List;
import ru.ruranobe.mybatis.tables.ChapterImage;

public interface ChapterImagesMapper 
{
    public List<ChapterImage> getChapterImagesByVolumeId(Integer volumeId);
}
