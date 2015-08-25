package ru.ruranobe.engine.wiki.parser;

import ru.ruranobe.misc.RuranobeUtils;

import java.util.*;

/**
 * .substring(int beginIndex, int endIndex) complexity in Java 7 (or higher) is O(n)
 * .substring(int beginIndex, int endIndex) complexity in pre-Java 7 or is O(1)
 */
public class WikiParser
{

    public String parseWikiText(List<String> imageUrls, boolean appendExtraImagesAtTheEnd)
    {
        fillWikiTags();
        Iterator<String> imageUrlsIterator = imageUrls.iterator();
        connectWikiTags(imageUrlsIterator);

        StringBuilder additionalTags = new StringBuilder("line-no=\"");
        additionalTags.append(Integer.toString(paragraphOrderNumber));
        additionalTags.append("\" ");
        if (textId != null)
        {
            additionalTags.append("text-id=\"").append(textId.toString()).append("\" ");
        }
        if (chapterId != null)
        {
            additionalTags.append("chapter-id=\"").append(chapterId.toString()).append("\" ");
        }
        StringBuilder htmlText = new StringBuilder(String.format("<p id=\"%s\" %s>",
                RuranobeUtils.paragraphIdOf(chapterId, textId, paragraphOrderNumber),
                additionalTags.toString()));

        paragraphOrderNumber++;

        parseWikiTextToHtmlText(0, wikiText.length(), htmlText, imageUrlsIterator, appendExtraImagesAtTheEnd);
        htmlText.append("</p>");
        return htmlText.toString();
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
        WikiTagType tagType;
        for (int i = 0; i < wikiText.length(); )
        {
            int curI = i;
            if (wikiText.codePointAt(i) == '{')
            {
                tagType = WikiTagType.resolve("{{Подзаголовок|");
                if (curI != (i = addWikiTagIfMetToMap(wikiText, i, tagType)))
                {
                    continue;
                }

                tagType = WikiTagType.resolve("{{Иллюстрация}}");
                if (curI != (i = addWikiTagIfMetToMap(wikiText, i, tagType)))
                {
                    continue;
                }

                tagType = WikiTagType.resolve("{{ref|");
                if (curI != (i = addWikiTagIfMetToMap(wikiText, i, tagType)))
                {
                    continue;
                }
            }
            if (wikiText.codePointAt(i) == '=')
            {
                tagType = WikiTagType.resolve("====");
                if (curI != (i = addWikiTagIfMetToMap(wikiText, i, tagType)))
                {
                    continue;
                }
                tagType = WikiTagType.resolve("===");
                if (curI != (i = addWikiTagIfMetToMap(wikiText, i, tagType)))
                {
                    continue;
                }
                tagType = WikiTagType.resolve("==");
                if (curI != (i = addWikiTagIfMetToMap(wikiText, i, tagType)))
                {
                    continue;
                }
            }
            if (wikiText.codePointAt(i) == '\'')
            {
                tagType = WikiTagType.resolve("\'\'\'");
                if (curI != (i = addWikiTagIfMetToMap(wikiText, i, tagType)))
                {
                    continue;
                }

                tagType = WikiTagType.resolve("\'\'");
                if (curI != (i = addWikiTagIfMetToMap(wikiText, i, tagType)))
                {
                    continue;
                }
            }
            if (wikiText.codePointAt(i) == '}')
            {
                tagType = WikiTagType.resolve("}}");
                if (curI != (i = addWikiTagIfMetToMap(wikiText, i, tagType)))
                {
                    continue;
                }
            }
            tagType = WikiTagType.resolve("\n");
            if (curI != (i = addWikiTagIfMetToMap(wikiText, i, tagType)))
            {
                continue;
            }
            tagType = WikiTagType.resolve("]");
            if (curI != (i = addWikiTagIfMetToMap(wikiText, i, tagType)))
            {
                continue;
            }
            tagType = WikiTagType.resolve("[http");
            if (curI != (i = addWikiTagIfMetToMap(wikiText, i, tagType)))
            {
                continue;
            }
            i++;
        }
    }

    // Connect related special tags together. Determine tag's end position in text.
    private void connectWikiTags(Iterator<String> imageUrlsIterator)
    {
        List<HashMap.SimpleEntry<Integer, Integer>> preParsingBoundaries = new ArrayList<HashMap.SimpleEntry<Integer, Integer>>();

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
                    } else if (footnote != null && footnote.getStartPosition() == startPosition)
                    {
                        footnote.setListOrderNumber(k);
                        AbstractMap.SimpleEntry<Integer, Integer> entry = new AbstractMap.SimpleEntry<Integer, Integer>
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
                if (imageUrlsIterator.hasNext())
                {
                    image.setImageUrl(imageUrlsIterator.next());
                } else
                {
                    image.setImageUrl("unknown source");
                }
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
        connectIdenticalTags(wikiTagTypeToWikiTags.get(WikiTagType.THREE_QUOTES));
        connectIdenticalTags(wikiTagTypeToWikiTags.get(WikiTagType.THREE_EQUAL));
        connectIdenticalTags(wikiTagTypeToWikiTags.get(WikiTagType.TWO_EQUAL));
        connectIdenticalTags(wikiTagTypeToWikiTags.get(WikiTagType.TWO_QUOTES));

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
            parseWikiTextToHtmlText(entry.getKey(), entry.getValue(), footnote, (new ArrayList<String>()).iterator(), false);
            this.footnotes.add(new FootnoteItem(footnoteParsingBoundariesToFootnoteId.get(entry), footnote.toString()));

            Replacement footnoteReplacement = footnoteParsingBoundariesToFootnoteReplacement.get(entry);
            String replacementText = String.format(footnoteReplacement.getReplacementText(), footnote.toString());
            footnoteReplacement.setReplacementText(replacementText);
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
                } else if (tags.get(i).getWikiTagType() == WikiTagType.THREE_EQUAL)
                {
                    contents.add(new ContentItem("h3", tag1.getUniqueId(),
                            wikiText.substring(tag1.getStartPosition() + tag1.getWikiTagLength(), tag2.getStartPosition())));
                } else if (tags.get(i).getWikiTagType() == WikiTagType.FOUR_EQUAL)
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

            String textIdStr = textId == null ? "" : Integer.toString(textId);
            String chapterIdStr = chapterId == null ? "" : Integer.toString(chapterId);
            String orderNumberStr = Integer.toString(orderNumber);

            String uniqueId = textIdStr + chapterIdStr + orderNumberStr;

            if (tagType == WikiTagType.FOOTNOTE)
            {
                attributeNameToValue = new HashMap<String, String>();
                attributeNameToValue.put("href", "#cite_note-" + uniqueId);
            }
            else if (tagType == WikiTagType.NEW_LINE)
            {
                uniqueId = RuranobeUtils.paragraphIdOf(chapterId, textId, paragraphOrderNumber);

                attributeNameToValue = new HashMap<String, String>();
                if (textId != null)
                {
                    attributeNameToValue.put("text-id", textId.toString());
                }
                if (chapterId != null)
                {
                    attributeNameToValue.put("chapter-id", chapterId.toString());
                }
                attributeNameToValue.put("line-no", Integer.toString(paragraphOrderNumber));
                paragraphOrderNumber++;
            }
            else if (tagType == WikiTagType.LINK)
            {
                int j = i+tagLength;
                for (; j < s.length() && j < i+tagLength+300 && s.charAt(j) != ' '; ++j);
                String href = ((s.charAt(j) == ' ') ? ("http" + s.substring(i+tagLength, j)) : null);
                if (href != null)
                {
                    attributeNameToValue = new HashMap<String, String>();
                    attributeNameToValue.put("href", href);
                    tag = new WikiTag(tagType, i, uniqueId, attributeNameToValue);
                    tag.setWikiTagLength(j-i);
                    i = j-tagLength;
                }
            }

            if (tagType != WikiTagType.LINK)
            {
                tag = new WikiTag(tagType, i, uniqueId, attributeNameToValue);
            }

            if (wikiTagTypeToWikiTags.get(tagType) == null)
            {
                ArrayList<WikiTag> tags = new ArrayList<WikiTag>();
                tags.add(tag);
                wikiTagTypeToWikiTags.put(tagType, tags);
            } else
            {
                wikiTagTypeToWikiTags.get(tagType).add(tag);
            }
            i += tagLength;
            orderNumber++;
        }
        return i;
    }

    // Form output html text
    private void parseWikiTextToHtmlText(int start, int end, StringBuilder htmlText, Iterator<String> imageUrlsIterator, boolean appendExtraImagesAtTheEnd)
    {
        for (int i = start; i < end; )
        {
            Replacement replacement = startPositionToReplacement.get(i);
            if (replacement != null)
            {
                htmlText.append(replacement.getReplacementText());
                i += replacement.getEndPosition() - replacement.getStartPosition();
            } else
            {
                htmlText.appendCodePoint(wikiText.codePointAt(i));
                i++;
            }
        }

        if (appendExtraImagesAtTheEnd)
        {
            while (imageUrlsIterator.hasNext())
            {
                String url = imageUrlsIterator.next();
                htmlText.append(String.format("<div class=\"center illustration\"><a class=\"fancybox\" rel=\"group\" href=\"%s\">" +
                                              "<img src=\"%s\" alt=\"\" class=\"img-responsive center-block img-thumbnail\"/>" +
                                              "</a></div>", url, url));
            }
        }
    }

    // textId and chapterId can be nullable
    public WikiParser(Integer textId, Integer chapterId, String wikiText)
    {
        this.chapterId = chapterId;
        this.textId = textId;
        this.wikiText = wikiText;
    }

    //private StringBuilder footnotes = new StringBuilder();
    private List<FootnoteItem> footnotes = new ArrayList<FootnoteItem>();
    private List<ContentItem> contents = new ArrayList<ContentItem>();

    private final Map<WikiTagType, ArrayList<WikiTag>> wikiTagTypeToWikiTags = new
            EnumMap<WikiTagType, ArrayList<WikiTag>>(WikiTagType.class);
    private final Map<Integer, Replacement> startPositionToReplacement = new
            HashMap<Integer, Replacement>();
    private final Map<HashMap.SimpleEntry<Integer, Integer>, Replacement> footnoteParsingBoundariesToFootnoteReplacement = new
            HashMap<AbstractMap.SimpleEntry<Integer, Integer>, Replacement>();
    private final Map<HashMap.SimpleEntry<Integer, Integer>, String> footnoteParsingBoundariesToFootnoteId = new
            HashMap<AbstractMap.SimpleEntry<Integer, Integer>, String>();
    private final Integer textId;
    private final Integer chapterId;
    private final String wikiText;
    private int orderNumber = 0;
    int paragraphOrderNumber = 0;
}