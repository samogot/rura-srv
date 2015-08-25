package ru.ruranobe.engine.wiki.parser;

import java.util.*;

/**
 * .substring(int beginIndex, int endIndex) complexity in Java 7 (or higher) is O(n)
 * .substring(int beginIndex, int endIndex) complexity in pre-Java 7 or is O(1)
 */
public class WikiParser
{

    private final Map<WikiTagType, ArrayList<WikiTag>> wikiTagTypeToWikiTags = new
            EnumMap<WikiTagType, ArrayList<WikiTag>>(WikiTagType.class);
    private final Map<Integer, Replacement> startPositionToReplacement = new
            HashMap<Integer, Replacement>();
    private final int textId;
    private final String wikiText;
    //private StringBuilder footnotes = new StringBuilder();
    private List<String> footnotes = new ArrayList<String>();
    private List<ContentItem> contents = new ArrayList<ContentItem>();
    private int orderNumber = 0;

    public WikiParser(int textId, String wikiText)
    {
        this.textId = textId;
        this.wikiText = wikiText;
    }

    public String parseWikiText(List<String> imageUrls, boolean appendExtraImagesAtTheEnd)
    {
        fillWikiTags();
        Iterator<String> imageUrlsIterator = imageUrls.iterator();
        connectWikiTags(imageUrlsIterator);
        long uniqueId = Long.valueOf(Integer.toString(textId) + Integer.toString(orderNumber + 1));
        StringBuilder htmlText = new StringBuilder(String.format("<p id=\"p_id-%d\">", uniqueId));
        parseWikiTextToHtmlText(0, wikiText.length(), htmlText, imageUrlsIterator, appendExtraImagesAtTheEnd);
        htmlText.append("</p>");
        return htmlText.toString();
    }

    public List<String> getFootnotes()
    {
        return footnotes;
        /*String footnotesVal = footnotes.toString();
        if (Strings.isEmpty(footnotesVal))
        {
            return null;
        }
        return "<h2><span>Примечания</span></h2><ol class=\"references\">"+footnotesVal+"</ol>";*/
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

                        preParsingBoundaries.add(new AbstractMap.SimpleEntry<Integer, Integer>
                                (footnote.getStartPosition() + footnote.getWikiTagLength(), doubleEndBracket.getStartPosition()));

                        List<Replacement> replacements = Replacement.getReplacementsForPair(footnote, doubleEndBracket);
                        for (Replacement replacement : replacements)
                        {
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
            this.footnotes.add(footnote.toString());
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
            long uniqueId = Long.valueOf(Integer.toString(textId) + Integer.toString(orderNumber));
            WikiTag tag = new WikiTag(tagType, i, uniqueId);
            if (wikiTagTypeToWikiTags.get(tagType) == null)
            {
                ArrayList<WikiTag> tags = new ArrayList<WikiTag>();
                tags.add(tag);
                wikiTagTypeToWikiTags.put(tagType, tags);
            } else
            {
                wikiTagTypeToWikiTags.get(tagType).add(tag);
            }
            i += tagType.getWikiTagSize();
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
}