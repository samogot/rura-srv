package ru.ruranobe.misc;

import ru.ruranobe.mybatis.entities.tables.Paragraph;
import ru.ruranobe.mybatis.mappers.ParagraphsMapper;

/**
 * If we have simultaneous insert or update of a paragraph it can cause some problems.
 *
 * Synchronized protects from it, but such problems should occur on an exceptionally rare occasion
 * so we can also delete synchronized in order to improve performance.
 */
public class ParagraphService
{
    public synchronized static void databaseHandleParagraph(ParagraphsMapper paragraphsMapper, Paragraph paragraph, String paragraphId)
    {
        Paragraph oldParagraph = paragraphsMapper.getParagraph(paragraphId);

        if (oldParagraph == null)
        {
            paragraphsMapper.insertParagraph(paragraph);
        }
        else if (paragraph.getTextId() >= oldParagraph.getTextId())
        {
            paragraphsMapper.updateParagraph(paragraph);
        }
    }
}
