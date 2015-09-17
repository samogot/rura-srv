package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.Paragraph;

public interface ParagraphsMapper
{
    public void insertParagraph(Paragraph paragraph);
    public void updateParagraph(Paragraph paragraph);
    public Paragraph getParagraph(String paragraphId);
}
