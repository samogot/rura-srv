package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.entities.additional.ChapterUrlDetails;
import ru.ruranobe.mybatis.mappers.ChapterUrlDetailsMapper;

public class ChapterUrlDetailsMapperCacheable implements ChapterUrlDetailsMapper
{
    private ChapterUrlDetailsMapper mapper;

    public ChapterUrlDetailsMapperCacheable(ChapterUrlDetailsMapper mapper)
    {
        this.mapper = mapper;
    }

    public ChapterUrlDetails getChapterUrlDetailsByChapter(Integer chapterId)
    {
        return mapper.getChapterUrlDetailsByChapter(chapterId);
    }
}
