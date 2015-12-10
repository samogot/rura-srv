package ru.ruranobe.mybatis.mappers;

import ru.ruranobe.mybatis.entities.tables.Paragraph;

public interface ParagraphsMapper
{
    void insertParagraph(Paragraph paragraph);

    void updateParagraph(Paragraph paragraph);

    Paragraph getParagraph(String paragraphId);
}
