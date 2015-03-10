package ru.ruranobe.mybatis.mappers;

import java.util.List;
import ru.ruranobe.mybatis.tables.Chapter;

public interface ChaptersMapper 
{
    public Chapter getChapterByUrl(String url);
    public List<Chapter> getChaptersByVolumeId(Integer volumeId);
}
