package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.mappers.TeamsMapper;
import ru.ruranobe.mybatis.mappers.TextsMapper;
import ru.ruranobe.mybatis.tables.Text;

public class TextsMapperCacheable implements TextsMapper
{

    private TextsMapper mapper;

    public TextsMapperCacheable(TextsMapper mapper)
    {
        this.mapper = mapper;
    }

    @Override
    public Text getTextById(Integer textId)
    {
       return mapper.getTextById(textId);
    }

    @Override
    public Text getHtmlInfoById(Integer textId)
    {
        return mapper.getHtmlInfoById(textId);
    }

    @Override
    public void updateText(Text text)
    {
        mapper.updateText(text);
    }

    @Override
    public int insertText(Text text)
    {
        return mapper.insertText(text);
    }

    @Override
    public void deleteText(Integer textId)
    {
        mapper.deleteText(textId);
    }
}
