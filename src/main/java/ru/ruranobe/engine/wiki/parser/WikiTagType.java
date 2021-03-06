package ru.ruranobe.engine.wiki.parser;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public enum WikiTagType
{
    SUBTITLE("{{Подзаголовок|"),
    TWO_EQUAL("=="),
    THREE_EQUAL("==="),
    FOUR_EQUAL("===="),
    IMAGE("{{Иллюстрация}}"),
    DOUBLE_END_BRACKET("}}"),
    NEW_LINE("\n"),
    FOOTNOTE("{{ref|"),
    LINK("[http"),
    END_BRACKET("]");

    WikiTagType(String wikiTagCharSequence)
    {
        this.wikiTagCharSequence = wikiTagCharSequence;
    }

    public String getWikiTagCharSequence()
    {
        return wikiTagCharSequence;
    }

    public int getWikiTagSize()
    {
        return wikiTagCharSequence.length();
    }

    public static WikiTagType resolve(String wikiTagCharSequence)
    {
        return TAG_CHAR_SEQUENCE_TO_TAG_TYPE.get(wikiTagCharSequence);
    }

    private static final Map<String, WikiTagType> TAG_CHAR_SEQUENCE_TO_TAG_TYPE =
            new ImmutableMap.Builder<String, WikiTagType>()
                    .put("{{Подзаголовок|", SUBTITLE)
                    .put("==", TWO_EQUAL)
                    .put("===", THREE_EQUAL)
                    .put("====", FOUR_EQUAL)
                    .put("{{Иллюстрация}}", IMAGE)
                    .put("}}", DOUBLE_END_BRACKET)
                    .put("\n", NEW_LINE)
                    .put("{{ref|", FOOTNOTE)
                    .put("[http", LINK)
                    .put("]", END_BRACKET)
                    .build();
    private final String wikiTagCharSequence;
}
