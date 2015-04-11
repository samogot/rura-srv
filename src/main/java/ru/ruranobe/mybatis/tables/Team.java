package ru.ruranobe.mybatis.tables;

import java.io.Serializable;

public class Team implements Serializable
{

    public Team()
    {
    }

    public Team(String teamName, String teamWebsiteLink, boolean teamHidden)
    {
        this.teamName = teamName;
        this.teamWebsiteLink = teamWebsiteLink;
        this.teamHidden = teamHidden;
    }
    
    public boolean isTeamHidden()
    {
        return teamHidden;
    }

    public void setTeamHidden(boolean teamHidden)
    {
        this.teamHidden = teamHidden;
    }

    public Integer getTeamId()
    {
        return teamId;
    }

    public void setTeamId(Integer teamId)
    {
        this.teamId = teamId;
    }

    public String getTeamName()
    {
        return teamName;
    }

    public void setTeamName(String teamName)
    {
        this.teamName = teamName;
    }

    public String getTeamWebsiteLink()
    {
        return teamWebsiteLink;
    }

    public void setTeamWebsiteLink(String teamWebsiteLink)
    {
        this.teamWebsiteLink = teamWebsiteLink;
    }
    
    private Integer teamId;
    private String teamName;
    private String teamWebsiteLink;
    private boolean teamHidden;
    private static final long serialVersionUID = 1L;
}
