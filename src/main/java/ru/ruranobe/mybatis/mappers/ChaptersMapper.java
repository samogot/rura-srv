package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.tables.Chapter;

import java.util.List;

public interface ChaptersMapper
{
    public Chapter getChapterByUrl(String url);

    public List<Chapter> getChaptersByVolumeId(Integer volumeId);
}
