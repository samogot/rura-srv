package ru.ruranobe.mybatis.entities.tables;

import java.io.Serializable;

public class Paragraph implements Serializable
{
    public Paragraph()
    {

    }

    public String getParagraphId() {
        return paragraphId;
    }

    public void setParagraphId(String paragraphId) {
        this.paragraphId = paragraphId;
    }

    public String getParagraphText() {
        return paragraphText;
    }

    public void setParagraphText(String paragraphText) {
        this.paragraphText = paragraphText;
    }

    public Integer getTextId() {
        return textId;
    }

    public void setTextId(Integer textId) {
        this.textId = textId;
    }

    String paragraphId;
    String paragraphText;
    Integer textId;
    private static final long serialVersionUID = 1L;
}
