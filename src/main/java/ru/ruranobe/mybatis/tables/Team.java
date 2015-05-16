package ru.ruranobe.mybatis.tables;

import java.io.Serializable;

public class Team implements Serializable
{

    private static final long serialVersionUID = 2L;
    private Integer teamId;
    private String teamName;
    private String teamWebsiteLink;

    public Team()
    {
    }

    public Team(String teamName, String teamWebsiteLink)
    {
        this.teamName = teamName;
        this.teamWebsiteLink = teamWebsiteLink;
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
}