package ru.ruranobe.mybatis.entities.tables;

import java.io.Serializable;
import java.util.Date;

public class Bookmark implements Serializable
{

    public Bookmark()
    {
    }

    public Integer getBookmarkId()
    {
        return bookmarkId;
    }

    public void setBookmarkId(Integer bookmarkId)
    {
        this.bookmarkId = bookmarkId;
    }

    public Integer getChapterId()
    {
        return chapterId;
    }

    public void setChapterId(Integer chapterId)
    {
        this.chapterId = chapterId;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public String getParagraphId()
    {
        return paragraphId;
    }

    public void setParagraphId(String paragraphId)
    {
        this.paragraphId = paragraphId;
    }

    public Date getCreatedWhen()
    {
        return createdWhen;
    }

    public void setCreatedWhen(Date createdWhen)
    {
        this.createdWhen = createdWhen;
    }

    public Paragraph getParagraph() {
        return paragraph;
    }

    public void setParagraph(Paragraph paragraph) {
        this.paragraph = paragraph;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public Integer getTextId() {
        return textId;
    }

    public void setTextId(Integer textId) {
        this.textId = textId;
    }

    private Integer bookmarkId;
    private Integer chapterId;
    private Integer userId;
    private String paragraphId;
    private Date createdWhen;
    /* Optional */
    private Paragraph paragraph;
    private String fullText;
    private Integer textId;
    private static final long serialVersionUID = 1L;
}