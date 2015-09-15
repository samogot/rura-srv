package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.additional.ChapterUrlDetails;

public interface ChapterUrlDetailsMapper
{
    public ChapterUrlDetails getChapterUrlDetailsByChapter(Integer chapterId);
}
