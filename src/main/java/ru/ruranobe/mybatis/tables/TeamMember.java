package ru.ruranobe.mybatis.tables;

import java.io.Serializable;

/**
 * Created by samogot on 08.05.15.
 */
public class TeamMember implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Integer memberId;
    private Integer userId;
    private Integer teamId;
    private String nikname;
    private boolean active;

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

    public String getNikname()
    {
        return nikname;
    }

    public void setNikname(String nikname)
    {
        this.nikname = nikname;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        TeamMember that = (TeamMember) o;

        return memberId.equals(that.memberId);

    }

    @Override
    public int hashCode()
    {
        return memberId.hashCode();
    }
}
