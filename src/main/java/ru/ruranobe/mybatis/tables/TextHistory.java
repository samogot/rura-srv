package ru.ruranobe.mybatis.tables;

import java.io.Serializable;
import java.util.Date;

public class TextHistory implements Serializable
{

    public TextHistory()
    {
    }

    public Integer getCurrentTextId() {
        return currentTextId;
    }

    public void setCurrentTextId(Integer currentTextId) {
        this.currentTextId = currentTextId;
    }

    public Integer getPreviousTextId() {
        return previousTextId;
    }

    public void setPreviousTextId(Integer previousTextId) {
        this.previousTextId = previousTextId;
    }

    public Date getInsertionTime() {
        return insertionTime;
    }

    public void setInsertionTime(Date insertionTime) {
        this.insertionTime = insertionTime;
    }

    private static final long serialVersionUID = 1L;
    private Integer currentTextId;
    private Integer previousTextId;
    private Date insertionTime;
}
