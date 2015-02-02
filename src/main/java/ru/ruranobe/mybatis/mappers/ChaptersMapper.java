package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.tables.Chapter;

public interface ChaptersMapper 
{
    public Chapter getChapterByUrl(String url);
}
