package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.Text;

public interface TextsMapper
{
    Text getTextById(Integer textId);

    Text getHtmlInfoById(Integer textId);

    void updateText(Text text);

    int insertText(Text text);

    void deleteText(Integer textId);
}
