package ru.ruranobe.mybatis.tables;

import java.io.Serializable;

public class Text implements Serializable
{

    private static final long serialVersionUID = 1L;
    private Integer textId;
    private String textWiki;
    private String textHtml;
    private String contents;
    private String footnotes;

    public Text()
    {
    }

    public Text(String textWiki, String textHtml)
    {
        this.textWiki = textWiki;
        this.textHtml = textHtml;
    }

    public String getTextHtml()
    {
        return textHtml;
    }

    public void setTextHtml(String textHtml)
    {
        this.textHtml = textHtml;
    }

    public Integer getTextId()
    {
        return textId;
    }

    public void setTextId(Integer textId)
    {
        this.textId = textId;
    }

    public String getTextWiki()
    {
        return textWiki;
    }

    public void setTextWiki(String textWiki)
    {
        this.textWiki = textWiki;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getFootnotes() {
        return footnotes;
    }

    public void setFootnotes(String footnotes) {
        this.footnotes = footnotes;
    }
}
