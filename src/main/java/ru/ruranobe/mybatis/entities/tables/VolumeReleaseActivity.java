package ru.ruranobe.mybatis.entities.tables;

import org.apache.commons.lang3.StringEscapeUtils;
import ru.ruranobe.engine.wiki.parser.Replacement;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class VolumeReleaseActivity implements Serializable
{

    public Integer getOrderNumber()
    {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber)
    {
        this.orderNumber = orderNumber;
    }

    public VolumeActivity getActivity()
    {
        return activity;
    }

    public void setActivity(VolumeActivity activity)
    {
        this.activity = activity;
        this.activityId = activity == null ? null : activity.getActivityId();
    }

    public Integer getActivityId()
    {
        return activityId;
    }

    public void setActivityId(Integer activityId)
    {
        this.activityId = activityId;
    }

    public Integer getVolumeId()
    {
        return volumeId;
    }

    public void setVolumeId(Integer volumeId)
    {
        this.volumeId = volumeId;
    }

    public Integer getReleaseActivityId()
    {
        return releaseActivityId;
    }

    public void setReleaseActivityId(Integer releaseActivityId)
    {
        this.releaseActivityId = releaseActivityId;
    }

    public Integer getMemberId()
    {
        return memberId;
    }

    public void setMemberId(Integer memberId)
    {
        this.memberId = memberId;
    }

    public boolean isTeamShowLabel()
    {
        return teamShowLabel;
    }

    public void setTeamShowLabel(boolean teamShowLabel)
    {
        this.teamShowLabel = teamShowLabel;
    }

    public String getActivityName()
    {
        return activityName;
    }

    public void setActivityName(String activityName)
    {
        this.activityName = activityName;
    }

    public String getMemberName()
    {
        return memberName;
    }

    public void setMemberName(String memberName)
    {
        this.memberName = memberName;
    }

    public String getTitle()
    {
        return memberName + " - " + activityName;
    }

    public String getTeamShowStatus()
    {
        return teamShowStatus;
    }

    public void setTeamShowStatus(String teamShowStatus)
    {
        this.teamShowStatus = teamShowStatus;
    }

    public String getTeamName()
    {
        return teamName;
    }

    public void setTeamName(String teamName)
    {
        this.teamName = teamName;
    }

    public List<String> getTeamShowStatuses()
    {
        return teamName == null ? SINGLE_SHOW_STATUSES : TEAM_SHOW_STATUSES;
    }

    public String getTeamLink()
    {
        return teamLink;
    }

    public String getTeamLinkTag()
    {
        return String.format("<a href=\"%s\">%s</a>",
                Replacement.escapeURLIllegalCharacters(teamLink),
                StringEscapeUtils.unescapeHtml4(teamName));
    }

    public void setTeamLink(String teamLink)
    {
        this.teamLink = teamLink;
    }

    public VolumeReleaseActivity()
    {
    }

    public VolumeReleaseActivity(Integer volumeId, Integer activityId)
    {
        this.volumeId = volumeId;
        this.activityId = activityId;
    }

    private static final long serialVersionUID = 2L;
    private Integer releaseActivityId;
    private Integer volumeId;
    private Integer activityId;
    private Integer memberId;
    private Integer orderNumber;
    private boolean teamShowLabel;
    private String teamShowStatus = TEAM_SHOW_TEAM;
    /* Optional. Doesn't exist in table, used only in mybatis selects and corresponding code. */
    private String activityName;
    private String memberName;
    private String teamName;
    private String teamLink;
    private VolumeActivity activity;
    public static final String TEAM_SHOW_NONE = "show_none";
    public static final String TEAM_SHOW_NICK = "show_nick";
    public static final String TEAM_SHOW_TEAM = "show_team";
    public static final List<String> TEAM_SHOW_STATUSES = Arrays.asList(TEAM_SHOW_NONE, TEAM_SHOW_NICK, TEAM_SHOW_TEAM);
    public static final List<String> SINGLE_SHOW_STATUSES = Arrays.asList(TEAM_SHOW_NONE, TEAM_SHOW_NICK);
}
