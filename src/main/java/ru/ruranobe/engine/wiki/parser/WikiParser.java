package ru.ruranobe.engine.wiki.parser;

import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.entities.tables.ExternalResource;
import ru.ruranobe.wicket.RuraConstants;

import java.util.*;

/**
 * .substring(int beginIndex, int endIndex) complexity in Java 7 (or higher) is O(n)
 * .substring(int beginIndex, int endIndex) complexity in pre-Java 7 or is O(1)
 */
public class WikiParser
{

    public String parseWikiText(List<ExternalResource> images, boolean appendExtraImagesAtTheEnd)
    {
        fillWikiTags();
        Iterator<ExternalResource> imagesIterator = images.iterator();
        connectWikiTags(imagesIterator);

        StringBuilder additionalTags = new StringBuilder("data-line-no=\"").append(Integer.toString(paragraphOrderNumber)).append("\" ");
        if (textId != null)
        {
            additionalTags.append("data-text-id=\"").append(textId.toString()).append("\" ");
        }
        if (chapterId != null)
        {
            additionalTags.append("data-chapter-id=\"").append(chapterId.toString()).append("\" ");
        }
        StringBuilder htmlText = new StringBuilder(String.format("<p id=\"%s\" %s>",
                RuranobeUtils.paragraphIdOf(chapterId, textId, paragraphOrderNumber),
                additionalTags.toString()));

        paragraphOrderNumber++;

        parseWikiTextToHtmlText(0, wikiText.length(), htmlText, imagesIterator, appendExtraImagesAtTheEnd);
        htmlText.append("</p>");

        // parse quotes inside <p> tags
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < htmlText.length(); )
        {
            if (htmlText.codePointAt(i) == '<'
                && htmlText.codePointAt(i+1) == 'p'
                && htmlText.codePointAt(i+2) == ' ')
            {
                StringBuilder startTag = new StringBuilder();
                while (htmlText.codePointAt(i) != '>')
                {
                    startTag.appendCodePoint(htmlText.codePointAt(i));
                    i++;
                }
                startTag.append('>');
                i++;
                StringBuilder paragraph = new StringBuilder();
                while (htmlText.codePointAt(i) != '<'
                       || htmlText.codePointAt(i+1) != '/'
                       || htmlText.codePointAt(i+2) != 'p'
                       || htmlText.codePointAt(i+3) != '>')
                {
                    paragraph.appendCodePoint(htmlText.codePointAt(i));
                    i++;
                }

                result.append(startTag).append(new QuoteParser().applyTo(paragraph.toString())).append("</p>");
            }
            i++;
        }
        return result.toString();
    }

    public List<FootnoteItem> getFootnotes()
    {
        return footnotes;
    }

    public List<ContentItem> getContents()
    {
        return contents;
    }

    // Detect special tags in text and remember them
    // The order is important
    private void fillWikiTags()
    {
        for (int i = 0; i < wikiText.length(); )
        {
            int curI = i;
            if (wikiText.codePointAt(i) == '{')
            {
                if (curI != (i = addWikiTagIfMetToMap(wikiText, i, WikiTagType.resolve("{{Подзаголовок|"))))
                {
                    continue;
                }

                if (curI != (i = addWikiTagIfMetToMap(wikiText, i, WikiTagType.resolve("{{Иллюстрация}}"))))
                {
                    continue;
                }

                if (curI != (i = addWikiTagIfMetToMap(wikiText, i, WikiTagType.resolve("{{ref|"))))
                {
                    continue;
                }
            }
            if (wikiText.codePointAt(i) == '=')
            {
                if (curI != (i = addWikiTagIfMetToMap(wikiText, i, WikiTagType.resolve("===="))))
                {
                    continue;
                }

                if (curI != (i = addWikiTagIfMetToMap(wikiText, i, WikiTagType.resolve("==="))))
                {
                    continue;
                }

                if (curI != (i = addWikiTagIfMetToMap(wikiText, i, WikiTagType.resolve("=="))))
                {
                    continue;
                }
            }
            if (wikiText.codePointAt(i) == '}')
            {
                if (curI != (i = addWikiTagIfMetToMap(wikiText, i, WikiTagType.resolve("}}"))))
                {
                    continue;
                }
            }
            if (curI != (i = addWikiTagIfMetToMap(wikiText, i, WikiTagType.resolve("\n"))))
            {
                continue;
            }
            if (curI != (i = addWikiTagIfMetToMap(wikiText, i, WikiTagType.resolve("]"))))
            {
                continue;
            }
            if (curI != (i = addWikiTagIfMetToMap(wikiText, i, WikiTagType.resolve("[http"))))
            {
                continue;
            }
            i++;
        }
    }

    // Connect related special tags together. Determine tag's end position in text.
    private void connectWikiTags(Iterator<ExternalResource> imagesIterator)
    {
        List<HashMap.SimpleEntry<Integer, Integer>> preParsingBoundaries = new ArrayList<>();

        int j = 0;
        int k = 0;
        List<WikiTag> doubleEndBrackets = wikiTagTypeToWikiTags.get(WikiTagType.DOUBLE_END_BRACKET);
        List<WikiTag> subtitles = wikiTagTypeToWikiTags.get(WikiTagType.SUBTITLE);
        List<WikiTag> footnotes = wikiTagTypeToWikiTags.get(WikiTagType.FOOTNOTE);
        if (doubleEndBrackets != null)
        {
            for (WikiTag doubleEndBracket : doubleEndBrackets)
            {
                WikiTag subtitle = null;
                WikiTag footnote = null;

                int startPosition = Integer.MAX_VALUE;
                if (subtitles != null && j < subtitles.size())
                {
                    subtitle = subtitles.get(j);
                    startPosition = subtitle.getStartPosition();
                }
                if (footnotes != null && k < footnotes.size())
                {
                    footnote = footnotes.get(k);
                    startPosition = Math.min(startPosition, footnote.getStartPosition());
                }

                if (doubleEndBracket.getStartPosition() > startPosition)
                {
                    if (subtitle != null && subtitle.getStartPosition() == startPosition)
                    {
                        List<Replacement> replacements = Replacement.getReplacementsForPair(subtitle, doubleEndBracket);
                        for (Replacement replacement : replacements)
                        {
                            startPositionToReplacement.put(replacement.getStartPosition(), replacement);
                        }
                        j++;
                    }
                    else if (footnote != null && footnote.getStartPosition() == startPosition)
                    {
                        footnote.setListOrderNumber(k);
                        AbstractMap.SimpleEntry<Integer, Integer> entry = new AbstractMap.SimpleEntry<>
                                (footnote.getStartPosition() + footnote.getWikiTagLength(), doubleEndBracket.getStartPosition());
                        preParsingBoundaries.add(entry);

                        List<Replacement> replacements = Replacement.getReplacementsForPair(footnote, doubleEndBracket);
                        for (Replacement replacement : replacements)
                        {
                            footnoteParsingBoundariesToFootnoteReplacement.put(entry, replacement);
                            footnoteParsingBoundariesToFootnoteId.put(entry, footnote.getUniqueId());
                            startPositionToReplacement.put(replacement.getStartPosition(), replacement);
                        }
                        k++;
                    }
                }
            }
        }

        List<WikiTag> images = wikiTagTypeToWikiTags.get(WikiTagType.IMAGE);
        if (images != null)
        {
            for (WikiTag image : images)
            {
                ExternalResource imageEntry = imagesIterator.hasNext() ? imagesIterator.next() : RuraConstants.UNKNOWN_IMAGE;
                image.setExternalResourceId(imageEntry.getResourceId());
                image.setImageUrl(imageEntry.getUrl());
                image.setImageThumbnail(imageEntry.getThumbnail(900));
                startPositionToReplacement.put(image.getStartPosition(),
                        new Replacement(image));
            }
        }

        j = 0;
        List<WikiTag> links = wikiTagTypeToWikiTags.get(WikiTagType.LINK);
        List<WikiTag> endBrackets = wikiTagTypeToWikiTags.get(WikiTagType.END_BRACKET);
        if (endBrackets != null)
        {
            for (WikiTag endBracket : endBrackets)
            {
                int startPosition = Integer.MAX_VALUE;
                WikiTag link = null;

                if (links != null && j < links.size())
                {
                    link = links.get(j);
                    startPosition = Math.min(startPosition, link.getStartPosition());
                }

                if (endBracket.getStartPosition() > startPosition)
                {
                    List<Replacement> replacements = Replacement.getReplacementsForPair(link, endBracket);
                    for (Replacement replacement : replacements)
                    {
                        startPositionToReplacement.put(replacement.getStartPosition(), replacement);
                    }
                    j++;
                }
            }
        }

        // The order is important
        connectIdenticalTags(wikiTagTypeToWikiTags.get(WikiTagType.FOUR_EQUAL));
        connectIdenticalTags(wikiTagTypeToWikiTags.get(WikiTagType.THREE_EQUAL));
        connectIdenticalTags(wikiTagTypeToWikiTags.get(WikiTagType.TWO_EQUAL));

        List<WikiTag> newLines = wikiTagTypeToWikiTags.get(WikiTagType.NEW_LINE);
        if (newLines != null)
        {
            for (WikiTag newLine : newLines)
            {
                startPositionToReplacement.put(newLine.getStartPosition(),
                        new Replacement(newLine));
            }
        }

        for (HashMap.SimpleEntry<Integer, Integer> entry : preParsingBoundaries)
        {
            StringBuilder footnote = new StringBuilder();
            parseWikiTextToHtmlText(entry.getKey(), entry.getValue(), footnote, Collections.<ExternalResource>emptyListIterator(), false);

            String quotedFootnoteText = new QuoteParser().applyTo(footnote.toString());
            this.footnotes.add(new FootnoteItem(footnoteParsingBoundariesToFootnoteId.get(entry), quotedFootnoteText));

            Replacement footnoteReplacement = footnoteParsingBoundariesToFootnoteReplacement.get(entry);
            //String replacementText = String.format(footnoteReplacement.getReplacementText(), footnote.toString());

            // add to data-content content without any tags
            String dataContent = "<p>" + quotedFootnoteText.replaceAll("\"", "&quot;") + "</p >";
            footnoteReplacement.setReplacementText(String.format(footnoteReplacement.getReplacementText(), dataContent));
        }
    }

    private void connectIdenticalTags(List<WikiTag> tags)
    {
        if (tags != null)
        {
            for (int i = 0; i < tags.size(); )
            {
                if (i + 1 >= tags.size())
                {
                    break;
                }
                WikiTag tag1 = tags.get(i);
                WikiTag tag2 = tags.get(i + 1);
                List<Replacement> replacements = Replacement.getReplacementsForPair(tag1, tag2);
                for (Replacement replacement : replacements)
                {
                    startPositionToReplacement.put(replacement.getStartPosition(), replacement);
                }

                /**
                 * If we need to actually parse the content of the headers see preParsingBoundaries example
                 * Don't forget, that we intentionally execute
                 *
                 * for (HashMap.SimpleEntry<Integer, Integer> entry : preParsingBoundaries)
                 * {
                 *     parseWikiTextToHtmlText(entry.getKey(), entry.getValue(), this.footnotes, (new ArrayList<String>()).iterator(), false);
                 * }
                 *
                 * AFTER we connect all wiki tags, so this information might be used during the parsing.
                 */
                if (tag1.getWikiTagType() == WikiTagType.TWO_EQUAL)
                {
                    contents.add(new ContentItem("h2", tag1.getUniqueId(),
                            wikiText.substring(tag1.getStartPosition() + tag1.getWikiTagLength(), tag2.getStartPosition())));
                }
                else if (tags.get(i).getWikiTagType() == WikiTagType.THREE_EQUAL)
                {
                    contents.add(new ContentItem("h3", tag1.getUniqueId(),
                            wikiText.substring(tag1.getStartPosition() + tag1.getWikiTagLength(), tag2.getStartPosition())));
                }
                else if (tags.get(i).getWikiTagType() == WikiTagType.FOUR_EQUAL)
                {
                    contents.add(new ContentItem("h4", tag1.getUniqueId(),
                            wikiText.substring(tag1.getStartPosition() + tag1.getWikiTagLength(), tag2.getStartPosition())));
                }
                i += 2;
            }
        }
    }

    private int addWikiTagIfMetToMap(String s, int i, WikiTagType tagType)
    {
        int symbolsTillEnd = s.length() - i;
        int tagLength = tagType.getWikiTagSize();

        if (symbolsTillEnd > tagLength - 1 &&
            s.substring(i, i + tagLength).equals(tagType.getWikiTagCharSequence()))
        {
            WikiTag tag = null;
            Map<String, String> attributeNameToValue = null;

            String uniqueId = (textId == null ? "" : Integer.toString(textId))
                    + (chapterId == null ? "" : Integer.toString(chapterId))
                    + Integer.toString(orderNumber);

            if (tagType == WikiTagType.FOOTNOTE)
            {
                attributeNameToValue = new HashMap<>();
                attributeNameToValue.put("href", "#cite_note-" + uniqueId);
            }
            else if (tagType == WikiTagType.NEW_LINE)
            {
                uniqueId = RuranobeUtils.paragraphIdOf(chapterId, textId, paragraphOrderNumber);

                attributeNameToValue = new HashMap<>();
                if (textId != null)
                {
                    attributeNameToValue.put("data-text-id", textId.toString());
                }
                if (chapterId != null)
                {
                    attributeNameToValue.put("data-chapter-id", chapterId.toString());
                }
                attributeNameToValue.put("data-line-no", Integer.toString(paragraphOrderNumber));
                paragraphOrderNumber++;
            }
            else if (tagType == WikiTagType.LINK)
            {
                int j = i + tagLength;
                //noinspection StatementWithEmptyBody
                for (; j < s.length() && j < i + tagLength + 300 && s.charAt(j) != ' '; ++j) ;
                String href = ((s.charAt(j) == ' ') ? ("http" + s.substring(i + tagLength, j)) : null);
                if (href != null)
                {
                    attributeNameToValue = new HashMap<>();
                    attributeNameToValue.put("href", href);
                    tag = new WikiTag(tagType, i, uniqueId, attributeNameToValue);
                    tag.setWikiTagLength(j - i);
                    i = j - tagLength;
                }
            }

            if (tagType != WikiTagType.LINK)
            {
                tag = new WikiTag(tagType, i, uniqueId, attributeNameToValue);
            }

            if (wikiTagTypeToWikiTags.get(tagType) == null)
            {
                ArrayList<WikiTag> tags = new ArrayList<>();
                tags.add(tag);
                wikiTagTypeToWikiTags.put(tagType, tags);
            }
            else
            {
                wikiTagTypeToWikiTags.get(tagType).add(tag);
            }
            i += tagLength;
            orderNumber++;
        }
        return i;
    }

    // Form output html text
    private void parseWikiTextToHtmlText(int start, int end, StringBuilder htmlText, Iterator<ExternalResource> imagesIterator, boolean appendExtraImagesAtTheEnd)
    {
        for (int i = start; i < end; )
        {
            Replacement replacement = startPositionToReplacement.get(i);
            if (replacement != null)
            {
                htmlText.append(replacement.getReplacementText());
                i += replacement.getEndPosition() - replacement.getStartPosition();
            }
            else
            {
                htmlText.appendCodePoint(wikiText.codePointAt(i));
                i++;
            }
        }

        if (appendExtraImagesAtTheEnd)
        {
            while (imagesIterator.hasNext())
            {
                ExternalResource imageEntry = imagesIterator.next();
                htmlText.append(String.format("<div class=\"center illustration\"><a class=\"fancybox\" rel=\"group\" href=\"%s\">" +
                                              "<img src=\"%s\" data-resource-id=\"%d\" alt=\"\" class=\"img-responsive img-thumbnail\"/>" +
                                              "</a></div>", imageEntry.getUrl(), imageEntry.getThumbnail(900), imageEntry.getResourceId()));
            }
        }
    }

    // textId and chapterId can be nullable
    public WikiParser(Integer textId, Integer chapterId, String wikiText, boolean sanitize)
    {
        this.chapterId = chapterId;
        this.textId = textId;
        if (sanitize)
        {
            this.wikiText = SimpleHtmlSanitizer.apply(wikiText);
        }
        else
        {
            this.wikiText = wikiText;
        }
    }

    //private StringBuilder footnotes = new StringBuilder();
    private List<FootnoteItem> footnotes = new ArrayList<>();
    private List<ContentItem> contents = new ArrayList<>();

    private final Map<WikiTagType, ArrayList<WikiTag>> wikiTagTypeToWikiTags = new
            EnumMap<>(WikiTagType.class);
    private final Map<Integer, Replacement> startPositionToReplacement = new
            HashMap<>();
    private final Map<HashMap.SimpleEntry<Integer, Integer>, Replacement> footnoteParsingBoundariesToFootnoteReplacement = new
            HashMap<>();
    private final Map<HashMap.SimpleEntry<Integer, Integer>, String> footnoteParsingBoundariesToFootnoteId = new
            HashMap<>();
    private final Integer textId;
    private final Integer chapterId;
    private final String wikiText;
    private int orderNumber = 0;
    int paragraphOrderNumber = 0;
}