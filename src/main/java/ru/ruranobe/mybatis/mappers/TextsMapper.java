package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.tables.Text;

public interface TextsMapper
{
    public Text getTextById(Integer textId);

    public Text getHtmlInfoById(Integer textId);

    public void updateText(Text text);

    public int insertText(Text text);

    public void deleteText(Integer textId);
}
