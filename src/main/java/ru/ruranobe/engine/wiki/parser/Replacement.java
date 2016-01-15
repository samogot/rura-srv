package ru.ruranobe.engine.wiki.parser;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static ru.ruranobe.engine.wiki.parser.WikiTagType.*;

public class Replacement
{

    public static List<Replacement> getReplacementsForPair(WikiTag startTag, WikiTag endTag)
    {
        List<Replacement> replacements = new ArrayList<>();
        if (startTag.getWikiTagType() == FOOTNOTE)
        {
            String replacementText = TAG_TO_REPLACEMENT_TEXT.get(FOOTNOTE);
            Map<String, String> attributeNameToValue = startTag.getAttributeNameToValue();
            String hrefTag = attributeNameToValue.get("href");
            replacementText = String.format(replacementText, startTag.getUniqueId(), "%s", hrefTag);
            replacements.add(new Replacement(startTag.getStartPosition(), endTag.getStartPosition() + endTag.getWikiTagLength(), replacementText, startTag));
        }
        else
        {
            WikiTagPair tagPair = new WikiTagPair(startTag.getWikiTagType(), endTag.getWikiTagType());
            String replacementText = PAIR_TO_START_REPLACEMENT_TEXT.get(tagPair);
            if (startTag.getWikiTagType() == TWO_EQUAL ||
                startTag.getWikiTagType() == THREE_EQUAL ||
                startTag.getWikiTagType() == FOUR_EQUAL)
            {
                replacementText = String.format(replacementText, startTag.getUniqueId());
            }
            else if (startTag.getWikiTagType() == LINK)
            {
                replacementText = String.format(replacementText, startTag.getAttributeNameToValue().get("href"));
            }
            replacements.add(new Replacement(startTag.getStartPosition(), startTag.getStartPosition() + startTag.getWikiTagLength(), replacementText, startTag));
            replacements.add(new Replacement(endTag.getStartPosition(), endTag.getStartPosition() + endTag.getWikiTagLength(), PAIR_TO_END_REPLACEMENT_TEXT.get(tagPair), startTag));
        }
        return replacements;
    }

    public int getStartPosition()
    {
        return startPosition;
    }

    public int getEndPosition()
    {
        return endPosition;
    }

    public String getReplacementText()
    {
        return replacementText;
    }

    public void setReplacementText(String replacementText)
    {
        this.replacementText = replacementText;
    }

    public WikiTag getMainTag()
    {
        return mainTag;
    }

    private static class WikiTagPair implements Map.Entry<WikiTagType, WikiTagType>
    {
        @Override
        public WikiTagType getKey()
        {
            return key;
        }

        @Override
        public WikiTagType getValue()
        {
            return value;
        }

        @Override
        public WikiTagType setValue(WikiTagType value)
        {
            this.value = value;
            return this.value;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }

            WikiTagPair that = (WikiTagPair) o;

            return key == that.key && value == that.value;

        }

        @Override
        public int hashCode()
        {
            int result = key.getWikiTagCharSequence().hashCode();
            result = 31 * result + value.getWikiTagCharSequence().hashCode();
            return result;
        }

        public WikiTagPair(WikiTagType key, WikiTagType value)
        {
            this.key = key;
            this.value = value;
        }

        private final WikiTagType key;
        private WikiTagType value;
    }

    public Replacement(WikiTag tag)
    {
        this.startPosition = tag.getStartPosition();
        this.endPosition = tag.getStartPosition() + tag.getWikiTagLength();
        this.mainTag = tag;
        String replacementText = TAG_TO_REPLACEMENT_TEXT.get(tag.getWikiTagType());
        if (tag.getWikiTagType() == FOOTNOTE)
        {
            Map<String, String> attributeNameToValue = tag.getAttributeNameToValue();
            String hrefTag = "href=\"" + attributeNameToValue.get("href") + "\"";
            this.replacementText = String.format(replacementText, tag.getUniqueId(), hrefTag);
        }
        else if (tag.getWikiTagType() == IMAGE)
        {
            this.replacementText = String.format(replacementText, tag.getImageUrl(), tag.getImageThumbnail(), tag.getExternalResourceId().toString());
        }
        else if (tag.getWikiTagType() == NEW_LINE)
        {
            String additionalTags = "";
            Map<String, String> attributeNameToValue = tag.getAttributeNameToValue();
            if (attributeNameToValue != null && !attributeNameToValue.isEmpty())
            {
                StringBuilder additionalTagsBuilder = new StringBuilder();
                for (Map.Entry<String, String> entry : attributeNameToValue.entrySet())
                {
                    additionalTagsBuilder.append(entry.getKey())
                                         .append("=\"")
                                         .append(entry.getValue())
                                         .append("\" ");
                    additionalTags = additionalTagsBuilder.toString();
                }
            }
            this.replacementText = String.format(replacementText, tag.getUniqueId(), additionalTags);
        }
        else
        {
            this.replacementText = replacementText;
        }
    }

    private Replacement(int startPosition, int endPosition, String replacementText, WikiTag mainTag)
    {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.replacementText = replacementText;
        this.mainTag = mainTag;
    }

    private static final Map<WikiTagType, String> TAG_TO_REPLACEMENT_TEXT =
            new EnumMap<WikiTagType, String>(WikiTagType.class)
            {
                {
                    put(NEW_LINE, "</p><p id=\"%s\" %s>");
                    put(FOOTNOTE, "<span id=\"cite_ref-%s\" data-content=\"%s\" class=\"reference\"><a href=\"%s\">*</a></span>");
                    put(IMAGE, "<div class=\"center illustration\"><a class=\"fancybox\" rel=\"group\" href=\"%s\">" +
                               "<img src=\"%s\" data-resource-id=\"%d\" alt=\"\" class=\"img-responsive img-thumbnail\"/>" +
                               "</a></div>");
                }
            };

    private static final Map<WikiTagPair, String> PAIR_TO_START_REPLACEMENT_TEXT = new ImmutableMap.Builder<WikiTagPair, String>()
            .put(new WikiTagPair(SUBTITLE, DOUBLE_END_BRACKET), "<div class=\"center subtitle\">")
            .put(new WikiTagPair(TWO_EQUAL, TWO_EQUAL), "<h2 id=\"h_id-%s\">")
            .put(new WikiTagPair(THREE_EQUAL, THREE_EQUAL), "<h3 id=\"h_id-%s\">")
            .put(new WikiTagPair(FOUR_EQUAL, FOUR_EQUAL), "<h4 id=\"h_id-%s\">")
            .put(new WikiTagPair(TWO_QUOTES, TWO_QUOTES), "<i>")
            .put(new WikiTagPair(THREE_QUOTES, THREE_QUOTES), "<b>")
            .put(new WikiTagPair(LINK, END_BRACKET), "<a href=\"%s\">")
            .build();

    private static final Map<WikiTagPair, String> PAIR_TO_END_REPLACEMENT_TEXT = new ImmutableMap.Builder<WikiTagPair, String>()
            .put(new WikiTagPair(SUBTITLE, DOUBLE_END_BRACKET), "</div>")
            .put(new WikiTagPair(TWO_EQUAL, TWO_EQUAL), "</h2>")
            .put(new WikiTagPair(THREE_EQUAL, THREE_EQUAL), "</h3>")
            .put(new WikiTagPair(FOUR_EQUAL, FOUR_EQUAL), "</h4>")
            .put(new WikiTagPair(TWO_QUOTES, TWO_QUOTES), "</i>")
            .put(new WikiTagPair(THREE_QUOTES, THREE_QUOTES), "</b>")
            .put(new WikiTagPair(LINK, END_BRACKET), "</a>")
            .build();

    private final int startPosition;
    private final int endPosition;
    private final WikiTag mainTag;
    private String replacementText;
}
