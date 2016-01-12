package ru.ruranobe.mybatis.entities.tables;

import java.io.Serializable;
import java.util.List;

/**
 * Created by samogot on 08.05.15.
 */
public class TeamMember implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Integer memberId;
    private Integer userId;
    private Integer teamId;
    private String nickname;
    private boolean active;
    //Optional
    private Team team;
    private List<String> userRoles;
    private String userName;

    public Team getTeam()
    {
        return team;
    }

    public void setTeam(Team team)
    {
        this.team = team;
        this.teamId = team == null ? null : team.getTeamId();
    }

    public Integer getMemberId()
    {
        return memberId;
    }

    public void setMemberId(Integer memberId)
    {
        this.memberId = memberId;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public Integer getTeamId()
    {
        return teamId;
    }

    public void setTeamId(Integer teamId)
    {
        this.teamId = teamId;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public List<String> getUserRoles()
    {
        return userRoles;
    }

    public void setUserRoles(List<String> roles)
    {
        this.userRoles = roles;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }
}
