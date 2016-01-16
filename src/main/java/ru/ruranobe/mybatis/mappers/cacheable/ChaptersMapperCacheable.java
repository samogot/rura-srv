package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.entities.tables.Chapter;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;

import java.util.List;

public class ChaptersMapperCacheable implements ChaptersMapper
{

    private ChaptersMapper mapper;

    public ChaptersMapperCacheable(ChaptersMapper mapper)
    {
        this.mapper = mapper;
    }

    @Override
    public Chapter getChapterById(int chapterId)
    {
        return mapper.getChapterById(chapterId);
    }

    @Override
    public Chapter getChapterByUrl(String url)
    {
        return mapper.getChapterByUrl(url);
    }

    @Override
    public Chapter getChapterNextPrevByUrl(String url)
    {
        return mapper.getChapterNextPrevByUrl(url);
    }

    @Override
    public List<Chapter> getChaptersByVolumeId(Integer volumeId)
    {
        return mapper.getChaptersByVolumeId(volumeId);
    }

    @Override
    public void insertChapter(Chapter chapter)
    {
        mapper.insertChapter(chapter);
    }

    @Override
    public void deleteChapter(int activityId)
    {
        mapper.deleteChapter(activityId);
    }

    @Override
    public void updateChapter(Chapter chapter)
    {
        mapper.updateChapter(chapter);
    }
}
