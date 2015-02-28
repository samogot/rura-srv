package ru.ruranobe.mybatis.tables;

import java.io.Serializable;
import java.util.Date;

public class OrphusComment implements Serializable
{

    public OrphusComment()
    {
    }

    public OrphusComment(int chapterId, int parapraph, int startOffset, 
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
    
    public int getChapterId()
    {
        return chapterId;
    }

    public void setChapterId(int chapterId)
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

    public int getParagraph()
    {
        return paragraph;
    }

    public void setParagraph(int paragraph)
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

    public int getStartOffset()
    {
        return startOffset;
    }

    public void setStartOffset(int startOffset)
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
    
    private int chapterId;
    private int paragraph;
    private int startOffset;
    private String originalText;
    private String replacementText;
    private String optionalComment;
    private Date createdWhen;
    
    private static final long serialVersionUID = 1L;
}
