package ru.ruranobe.mybatis.mappers.cacheable;

import ru.ruranobe.mybatis.entities.tables.Paragraph;
import ru.ruranobe.mybatis.mappers.ParagraphsMapper;

public class ParagraphsMapperCacheable implements ParagraphsMapper
{
    private ParagraphsMapper mapper;

    public ParagraphsMapperCacheable(ParagraphsMapper mapper)
    {
        this.mapper = mapper;
    }

    @Override
    public void insertParagraph(Paragraph paragraph) {
        mapper.insertParagraph(paragraph);
    }

    @Override
    public void updateParagraph(Paragraph paragraph) {
        mapper.updateParagraph(paragraph);
    }

    @Override
    public Paragraph getParagraph(String paragraphId) {
        return mapper.getParagraph(paragraphId);
    }
}