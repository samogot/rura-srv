package ru.ruranobe.mybatis.entities.tables;

import java.io.Serializable;
import java.util.Date;

public class OrphusComment implements Serializable
{

    private static final long serialVersionUID = 2L;
    private Integer chapterId;
    private String paragraph;
    private Integer startOffset;
    private String originalText;
    private String replacementText;
    private String optionalComment;
    private String userIP;
    private Integer userId;
    private Date createdWhen;
    private String fullText;
    private Integer textId;
    /* Optional */
    private Paragraph paragrap;
    private String username;
    private String chapterUrl;
    private String chapterName;

    public OrphusComment()
    {
    }

    public OrphusComment(Integer chapterId, String parapraph, Integer startOffset,
                         String originalText, String replacementText, String optionalComment, Date createdWhen)
    {
        this.chapterId = chapterId;
        this.paragraph = parapraph;
        this.startOffset = startOffset;
        this.originalText = originalText;
        this.replacementText = replacementText;
        this.optionalComment = optionalComment;
        this.createdWhen = createdWhen;
    }

    public Integer getChapterId()
    {
        return chapterId;
    }

    public void setChapterId(Integer chapterId)
    {
        this.chapterId = chapterId;
    }

    public String getOriginalText()
    {
        return originalText;
    }

    public void setOriginalText(String originalText)
    {
        this.originalText = originalText;
    }

    public String getParagraph()
    {
        return paragraph;
    }

    public void setParagraph(String paragraph)
    {
        this.paragraph = paragraph;
    }

    public String getReplacementText()
    {
        return replacementText;
    }

    public void setReplacementText(String replacementText)
    {
        this.replacementText = replacementText;
    }

    public String getOptionalComment()
    {
        return optionalComment;
    }

    public void setOptionalComment(String optionalComment)
    {
        this.optionalComment = optionalComment;
    }

    public Integer getStartOffset()
    {
        return startOffset;
    }

    public void setStartOffset(Integer startOffset)
    {
        this.startOffset = startOffset;
    }

    public Date getCreatedWhen()
    {
        return createdWhen;
    }

    public void setCreatedWhen(Date createdWhen)
    {
        this.createdWhen = createdWhen;
    }

    public String getUserIP()
    {
        return userIP;
    }

    public void setUserIP(String userIP)
    {
        this.userIP = userIP;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
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

    public Paragraph getParagrap() {
        return paragrap;
    }

    public void setParagrap(Paragraph paragrap) {
        this.paragrap = paragrap;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChapterUrl() {
        return chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }
}
