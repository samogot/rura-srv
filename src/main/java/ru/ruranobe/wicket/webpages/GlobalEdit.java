package ru.ruranobe.wicket.webpages;

import com.google.common.collect.Lists;
import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.AbstractRepeater;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.TeamMembersMapper;
import ru.ruranobe.mybatis.mappers.TeamsMapper;
import ru.ruranobe.mybatis.mappers.VolumeActivitiesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.tables.Project;
import ru.ruranobe.mybatis.tables.Team;
import ru.ruranobe.mybatis.tables.TeamMember;
import ru.ruranobe.mybatis.tables.VolumeActivity;
import ru.ruranobe.wicket.components.admin.AdminAffixedListPanel;
import ru.ruranobe.wicket.components.admin.formitems.ProjectFormItemPanel;
import ru.ruranobe.wicket.components.admin.formitems.TeamFormItemPanel;
import ru.ruranobe.wicket.components.admin.formitems.TeamMemberFormItemPanel;
import ru.ruranobe.wicket.components.admin.formitems.VolumeActivityFormItemPanel;
import ru.ruranobe.wicket.webpages.base.AdminLayoutPage;

import java.util.*;

public class GlobalEdit extends AdminLayoutPage
{
/*    private static class TeamMembersForm extends Form
    {
        private String validateData()
        {
            String exceptionText = null;
            for (TeamMember teamMember : teamMembers)
            {
                if (teamMember.getTeamId() == null)
                {
                    exceptionText = "Для одного из членов команд не указана команда";
                }
                else if (Strings.isEmpty(teamMember.getNikname()))
                {
                    exceptionText = "Для одного из членов команд не указан никнейм";
                }
            }
            return exceptionText;
        }

        public TeamMembersForm(List<TeamMember> teamMembersVar, List<Team> teams)
        {
            super("teamMembersForm");

            teamMembers = teamMembersVar;

            for (Team team : teams)
            {
                teamNameToTeamId.put(team.getTeamName(), team.getTeamId());
                teamIdToTeamName.put(team.getTeamId(), team.getTeamName());
            }

            AjaxButton addTeamMember = new AjaxButton("addTeamMember", this)
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    TeamMember teamMember = new TeamMember();
                    teamMember.setNikname("<Пусто>");
                    teamMember.setActive(true);
                    teamMembers.add(0, teamMember);
                    target.add(form);
                    target.appendJavaScript("$('.list-group').listgroup();");
                }
            };
            this.add(addTeamMember);

            final ListView<TeamMember> teamMemberNameRepeater = new ListView<TeamMember>("teamMemberNameRepeater", teamMembers)
            {
                @Override
                protected void populateItem(ListItem<TeamMember> item)
                {
                    int orderNumber = item.getIndex();
                    String generatedTeamMemberId = "teammembers" + Integer.toString(orderNumber);
                    WebMarkupContainer visibilityController = new WebMarkupContainer("teamMemberVisibilityController");
                    AttributeAppender addHref = new AttributeAppender("href", "#" + generatedTeamMemberId);
                    visibilityController.add(addHref);
                    AttributeAppender addAriaControls = new AttributeAppender("aria-controls", generatedTeamMemberId);
                    visibilityController.add(addAriaControls);
                    TeamMember teamMember = item.getModelObject();
                    String name = teamMember.getNikname();
                    Label teamMemberName = new Label("teamMemberName", name);
                    teamMemberName.setOutputMarkupId(true);
                    teamMemberName.setMarkupId(generatedTeamMemberId + "_name");
                    visibilityController.add(teamMemberName);
                    item.add(visibilityController);
                }
            };
            this.add(teamMemberNameRepeater);

            final ListView<TeamMember> teamMemberRepeater = new ListView<TeamMember>("teamMemberRepeater", teamMembers)
            {
                @Override
                protected void populateItem(ListItem<TeamMember> item)
                {
                    int orderNumber = item.getIndex();
                    String generatedTeamMemberId = "teammembers" + Integer.toString(orderNumber);
                    WebMarkupContainer teamMemberDiv = new WebMarkupContainer("teamMemberDiv");
                    teamMemberDiv.setOutputMarkupId(true);
                    teamMemberDiv.setMarkupId(generatedTeamMemberId);
                    final TeamMember teamMember = item.getModelObject();

                    HiddenField<Integer> teamMemberId = new HiddenField<Integer>("teamMemberId", new Model<Integer>()
                    {
                        @Override
                        public Integer getObject()
                        {
                            return teamMember.getMemberId();
                        }
                    });
                    teamMemberId.setOutputMarkupId(true);
                    teamMemberId.setMarkupId(generatedTeamMemberId + "_id");
                    teamMemberDiv.add(teamMemberId);

                    HiddenField<String> teamMemberDeleted = new HiddenField<String>("teamMemberDeleted", new Model<String>()
                    {

                        @Override
                        public void setObject(String object)
                        {
                            Boolean value = Boolean.valueOf(object);
                            if (value)
                            {
                                deletedTeamMembers.add(teamMember.getMemberId());
                            }
                        }
                    });
                    teamMemberDeleted.setOutputMarkupId(true);
                    teamMemberDeleted.setMarkupId(generatedTeamMemberId + "_delete");
                    teamMemberDiv.add(teamMemberDeleted);

                    WebMarkupContainer teamMemberNicknameDiv = new WebMarkupContainer("teamMemberNicknameDiv");
                    teamMemberNicknameDiv.setOutputMarkupId(true);
                    teamMemberNicknameDiv.setMarkupId(generatedTeamMemberId + "_names_selector");
                    teamMemberDiv.add(teamMemberNicknameDiv);

                    TextField<String> teamMemberNickname = new TextField<String>("teamMemberNickname", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return teamMember.getNikname();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            teamMember.setNikname(value);
                        }
                    });
                    teamMemberNickname.setOutputMarkupId(true);
                    teamMemberNickname.setMarkupId(generatedTeamMemberId + "_name_input");
                    teamMemberNicknameDiv.add(teamMemberNickname);

                    DropDownChoice<String> teamMemberTeam = new DropDownChoice<String>("teamMemberTeam", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return teamIdToTeamName.get(teamMember.getTeamId());
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            teamMember.setMemberId(teamNameToTeamId.get(value));
                        }

                    }, Lists.newArrayList(teamNameToTeamId.keySet()));
                    teamMemberTeam.setOutputMarkupId(true);
                    teamMemberTeam.setMarkupId(generatedTeamMemberId + "_team_input");
                    teamMemberDiv.add(teamMemberTeam);

                    CheckBox teamMemberActive = new CheckBox("teamMemberActive", new Model<Boolean>()
                    {
                        @Override
                        public Boolean getObject()
                        {
                            return teamMember.isActive();
                        }

                        @Override
                        public void setObject(final Boolean value)
                        {
                            teamMember.setActive(value);
                        }
                    });
                    teamMemberActive.setOutputMarkupId(true);
                    teamMemberActive.setMarkupId(generatedTeamMemberId + "_checkbox_input");
                    teamMemberDiv.add(teamMemberActive);

                    item.add(teamMemberDiv);
                }
            };
            this.add(teamMemberRepeater);

            AjaxButton updateTeamMembersAjax = new AjaxButton("updateTeamMembersAjax", this)
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    SqlSession session = MybatisUtil.getSessionFactory().openSession();
                    try
                    {
                        TeamMembersMapper teamMembersMapperCacheable = CachingFacade.getCacheableMapper(session, TeamMembersMapper.class);
                        String exceptionText = validateData();
                        if (!Strings.isEmpty(exceptionText))
                        {
                            target.appendJavaScript("alert('" + exceptionText + "')");
                        }
                        else
                        {
                            for (TeamMember teamMember : teamMembers)
                            {
                                if (teamMember.getMemberId() != null)
                                {
                                    // update
                                    teamMembersMapperCacheable.updateTeamMember(teamMember);
                                }
                                else
                                {
                                    // insert
                                    teamMembersMapperCacheable.insertTeamMember(teamMember);
                                }
                            }

                            for (Integer teamMemberId : deletedTeamMembers)
                            {
                                // delete
                                teamMembersMapperCacheable.deleteTeamMember(teamMemberId);
                            }

                            session.commit();
                        }
                    }
                    finally
                    {
                        session.close();
                    }

                    deletedTeamMembers.clear();
                }
            };

            this.add(updateTeamMembersAjax);
        }

        private final List<TeamMember> teamMembers;
        private Map<String, Integer> teamNameToTeamId = new HashMap<String, Integer>();
        private Map<Integer, String> teamIdToTeamName = new HashMap<Integer, String>();
        private Set<Integer> deletedTeamMembers = new HashSet<Integer>();
    }

    private static class TeamsForm extends Form
    {
        private String validateData()
        {
            String exceptionText = null;
            for (Team team : teams)
            {
                if (Strings.isEmpty(team.getTeamName()))
                {
                    exceptionText = "Для одной из команд не указано имя";
                }
            }
            return exceptionText;
        }

        public TeamsForm(List<Team> teamsVar)
        {
            super("teamsForm");

            teams = teamsVar;

            AjaxButton addTeam = new AjaxButton("addTeam", this)
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    Team team = new Team();
                    team.setTeamName("<Пусто>");
                    team.setTeamWebsiteLink("<Пусто>");
                    teams.add(0, team);
                    target.add(form);
                    target.appendJavaScript("$('.list-group').listgroup();");
                }
            };
            this.add(addTeam);

            final ListView<Team> teamNameRepeater = new ListView<Team>("teamNameRepeater", teams)
            {
                @Override
                protected void populateItem(ListItem<Team> item)
                {
                    int orderNumber = item.getIndex();
                    String generatedTeamId = "teams" + Integer.toString(orderNumber);
                    WebMarkupContainer visibilityController = new WebMarkupContainer("teamVisibilityController");
                    AttributeAppender addHref = new AttributeAppender("href", "#" + generatedTeamId);
                    visibilityController.add(addHref);
                    AttributeAppender addAriaControls = new AttributeAppender("aria-controls", generatedTeamId);
                    visibilityController.add(addAriaControls);
                    Team team = item.getModelObject();
                    String name = team.getTeamName();
                    Label teamName = new Label("teamName", name);
                    teamName.setOutputMarkupId(true);
                    teamName.setMarkupId(generatedTeamId + "_name");
                    visibilityController.add(teamName);
                    item.add(visibilityController);
                }
            };
            this.add(teamNameRepeater);

            final ListView<Team> teamRepeater = new ListView<Team>("teamRepeater", teams)
            {
                @Override
                protected void populateItem(ListItem<Team> item)
                {
                    int orderNumber = item.getIndex();
                    String generatedTeamId = "teams" + Integer.toString(orderNumber);
                    WebMarkupContainer teamDiv = new WebMarkupContainer("teamDiv");
                    teamDiv.setOutputMarkupId(true);
                    teamDiv.setMarkupId(generatedTeamId);
                    final Team team = item.getModelObject();

                    HiddenField<Integer> teamId = new HiddenField<Integer>("teamId", new Model<Integer>()
                    {
                        @Override
                        public Integer getObject()
                        {
                            return team.getTeamId();
                        }
                    });
                    teamId.setOutputMarkupId(true);
                    teamId.setMarkupId(generatedTeamId + "_id");
                    teamDiv.add(teamId);

                    HiddenField<String> teamDeleted = new HiddenField<String>("teamDeleted", new Model<String>()
                    {

                        @Override
                        public void setObject(String object)
                        {
                            Boolean value = Boolean.valueOf(object);
                            if (value)
                            {
                                deletedTeams.add(team.getTeamId());
                            }
                        }
                    });
                    teamDeleted.setOutputMarkupId(true);
                    teamDeleted.setMarkupId(generatedTeamId + "_delete");
                    teamDiv.add(teamDeleted);

                    TextField<String> teamName = new TextField<String>("teamName", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return team.getTeamName();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            team.setTeamName(value);
                        }
                    });
                    teamName.setOutputMarkupId(true);
                    teamName.setMarkupId(generatedTeamId + "_name_input");
                    teamDiv.add(teamName);

                    TextField<String> teamUrl = new TextField<String>("teamUrl", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return team.getTeamWebsiteLink();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            team.setTeamName(value);
                        }
                    });
                    teamUrl.setOutputMarkupId(true);
                    teamUrl.setMarkupId(generatedTeamId + "_url_input");
                    teamDiv.add(teamUrl);

                    item.add(teamDiv);
                }
            };
            this.add(teamRepeater);

            AjaxButton updateTeamsAjax = new AjaxButton("updateTeamsAjax", this)
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    SqlSession session = MybatisUtil.getSessionFactory().openSession();
                    try
                    {
                        TeamsMapper teamMapperCacheable = CachingFacade.getCacheableMapper(session, TeamsMapper.class);
                        String exceptionText = validateData();
                        if (!Strings.isEmpty(exceptionText))
                        {
                            target.appendJavaScript("alert('" + exceptionText + "')");
                        }
                        else
                        {
                            for (Team team : teams)
                            {
                                if (team.getTeamId() != null)
                                {
                                    // update
                                    teamMapperCacheable.updateTeam(team);
                                }
                                else
                                {
                                    // insert
                                    teamMapperCacheable.insertTeam(team);
                                }
                            }

                            for (Integer teamId : deletedTeams)
                            {
                                // delete
                                teamMapperCacheable.deleteTeam(teamId);
                            }

                            session.commit();
                        }
                    }
                    finally
                    {
                        session.close();
                    }

                    deletedTeams.clear();
                }
            };

            this.add(updateTeamsAjax);
        }

        private final List<Team> teams;
        private Set<Integer> deletedTeams = new HashSet<Integer>();
    }

    private static class ActivitiesForm extends Form
    {
        private String validateData()
        {
            String exceptionText = null;
            for (VolumeActivity activity : activities)
            {
                if (Strings.isEmpty(activity.getActivityName()))
                {
                    exceptionText = "Для одного из видов работ не указано имя";
                }
                else if (Strings.isEmpty(activity.getActivityType()))
                {
                    exceptionText = "Для одного из видов работ не указан тип";
                }
            }
            return exceptionText;
        }

        public ActivitiesForm(List<VolumeActivity> activitiesVar)
        {
            super("activitiesForm");

            activities = activitiesVar;

            AjaxButton addActivity = new AjaxButton("addActivity", this)
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    VolumeActivity activity = new VolumeActivity();
                    activity.setActivityName("<Пусто>");
                    activity.setActivityType("text");
                    activities.add(0, activity);
                    target.add(form);
                    target.appendJavaScript("$('.list-group').listgroup();");
                }
            };
            this.add(addActivity);

            final ListView<VolumeActivity> activityNameRepeater = new ListView<VolumeActivity>("activityNameRepeater", activities)
            {
                @Override
                protected void populateItem(ListItem<VolumeActivity> item)
                {
                    int orderNumber = item.getIndex();
                    String generatedActivityId = "activities" + Integer.toString(orderNumber);
                    WebMarkupContainer visibilityController = new WebMarkupContainer("activityVisibilityController");
                    AttributeAppender addHref = new AttributeAppender("href", "#" + generatedActivityId);
                    visibilityController.add(addHref);
                    AttributeAppender addAriaControls = new AttributeAppender("aria-controls", generatedActivityId);
                    visibilityController.add(addAriaControls);
                    VolumeActivity activity = item.getModelObject();
                    String name = activity.getActivityName();
                    Label activityName = new Label("activityName", name);
                    activityName.setOutputMarkupId(true);
                    activityName.setMarkupId(generatedActivityId + "_name");
                    visibilityController.add(activityName);
                    item.add(visibilityController);
                }
            };
            this.add(activityNameRepeater);

            final ListView<VolumeActivity> activityRepeater = new ListView<VolumeActivity>("activityRepeater", activities)
            {
                @Override
                protected void populateItem(ListItem<VolumeActivity> item)
                {
                    int orderNumber = item.getIndex();
                    String generatedActivityId = "activities" + Integer.toString(orderNumber);
                    WebMarkupContainer activityDiv = new WebMarkupContainer("activityDiv");
                    activityDiv.setOutputMarkupId(true);
                    activityDiv.setMarkupId(generatedActivityId);
                    final VolumeActivity activity = item.getModelObject();

                    HiddenField<Integer> activityId = new HiddenField<Integer>("activityId", new Model<Integer>()
                    {
                        @Override
                        public Integer getObject()
                        {
                            return activity.getActivityId();
                        }
                    });
                    activityId.setOutputMarkupId(true);
                    activityId.setMarkupId(generatedActivityId + "_id");
                    activityDiv.add(activityId);

                    HiddenField<String> activityDeleted = new HiddenField<String>("activityDeleted", new Model<String>()
                    {

                        @Override
                        public void setObject(String object)
                        {
                            Boolean value = Boolean.valueOf(object);
                            if (value)
                            {
                                deletedActivities.add(activity.getActivityId());
                            }
                        }
                    });
                    activityDeleted.setOutputMarkupId(true);
                    activityDeleted.setMarkupId(generatedActivityId + "_delete");
                    activityDiv.add(activityDeleted);

                    TextField<String> activityTitle = new TextField<String>("activityTitle", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return activity.getActivityName();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            activity.setActivityName(value);
                        }
                    });
                    activityTitle.setOutputMarkupId(true);
                    activityTitle.setMarkupId(generatedActivityId + "_name_input");
                    activityDiv.add(activityTitle);

                    DropDownChoice<String> activityType = new DropDownChoice<String>("activityType", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return ACTIVITY_DB_TYPE_TO_TYPE.get(activity.getActivityType());
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            activity.setActivityType(ACTIVITY_TYPE_TO_DB_TYPE.get(value));
                        }

                    }, Arrays.asList("текст", "изображение"));
                    activityType.setOutputMarkupId(true);
                    activityType.setMarkupId(generatedActivityId + "_type_input");
                    activityDiv.add(activityType);

                    item.add(activityDiv);
                }
            };
            this.add(activityRepeater);

            AjaxButton updateActivitiesAjax = new AjaxButton("updateActivitiesAjax", this)
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    SqlSession session = MybatisUtil.getSessionFactory().openSession();
                    try
                    {
                        VolumeActivitiesMapper activitiesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumeActivitiesMapper.class);
                        String exceptionText = validateData();
                        if (!Strings.isEmpty(exceptionText))
                        {
                            target.appendJavaScript("alert('" + exceptionText + "')");
                        }
                        else
                        {
                            for (VolumeActivity activity : activities)
                            {
                                if (activity.getActivityId() != null)
                                {
                                    // update
                                    activitiesMapperCacheable.updateVolumeActivity(activity);
                                }
                                else
                                {
                                    // insert
                                    activitiesMapperCacheable.insertVolumeActivity(activity);
                                }
                            }

                            for (Integer activityId : deletedActivities)
                            {
                                // delete
                                activitiesMapperCacheable.deleteVolumeActivity(activityId);
                            }

                            session.commit();
                        }
                    }
                    finally
                    {
                        session.close();
                    }

                    deletedActivities.clear();
                }
            };

            this.add(updateActivitiesAjax);
        }

        private static final Map<String, String> ACTIVITY_DB_TYPE_TO_TYPE =
                new ImmutableMap.Builder<String, String>()
                        .put("text", "текст")
                        .put("image", "изображение")
                        .build();
        private static final Map<String, String> ACTIVITY_TYPE_TO_DB_TYPE =
                new ImmutableMap.Builder<String, String>()
                        .put("текст", "text")
                        .put("изображение", "image")
                        .build();
        private final List<VolumeActivity> activities;
        private Set<Integer> deletedActivities = new HashSet<Integer>();
    }

    private static class ProjectsForm extends Form
    {
        private String validateData()
        {
            String exceptionText = null;
            for (Project project : projects)
            {
                if (Strings.isEmpty(project.getUrl()))
                {
                    exceptionText = "Для одного из проектов не указан url";
                }
                else if (Strings.isEmpty(project.getTitle()))
                {
                    exceptionText = "Для одного из проектов не указан title";
                }
            }
            return exceptionText;
        }

        public ProjectsForm(List<Project> projectsVar)
        {
            super("projectsForm");

            projects = projectsVar;

            AjaxButton addProject = new AjaxButton("addProject", this)
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    Project project = new Project();
                    project.setTitle("<Пусто>");
                    projects.add(0, project);
                    target.add(form);
                    target.appendJavaScript("$('.list-group').listgroup();");
                }
            };

            this.add(addProject);

            final ListView<Project> projectNameRepeater = new ListView<Project>("projectNameRepeater", projects)
            {
                @Override
                protected void populateItem(ListItem<Project> item)
                {
                    int orderNumber = item.getIndex();
                    String generatedSeriesId = "series" + Integer.toString(orderNumber);
                    WebMarkupContainer visibilityController = new WebMarkupContainer("projectVisibilityController");
                    AttributeAppender addHref = new AttributeAppender("href", "#" + generatedSeriesId);
                    visibilityController.add(addHref);
                    AttributeAppender addAriaControls = new AttributeAppender("aria-controls", generatedSeriesId);
                    visibilityController.add(addAriaControls);
                    Project project = item.getModelObject();
                    String name = project.getNameRu();
                    name = Strings.isEmpty(name) ? project.getTitle() : name;
                    Label projectName = new Label("projectName", name);
                    projectName.setOutputMarkupId(true);
                    projectName.setMarkupId(generatedSeriesId + "_name");
                    visibilityController.add(projectName);
                    item.add(visibilityController);
                }
            };
            this.add(projectNameRepeater);

            final ListView<Project> projectRepeater = new ListView<Project>("projectRepeater", projects)
            {
                @Override
                protected void populateItem(ListItem<Project> item)
                {
                    int orderNumber = item.getIndex();
                    String generatedSeriesId = "series" + Integer.toString(orderNumber);
                    WebMarkupContainer projectDiv = new WebMarkupContainer("projectDiv");
                    projectDiv.setOutputMarkupId(true);
                    projectDiv.setMarkupId(generatedSeriesId);
                    final Project project = item.getModelObject();

                    HiddenField<Integer> projectId = new HiddenField<Integer>("projectId", new Model<Integer>()
                    {
                        @Override
                        public Integer getObject()
                        {
                            return project.getProjectId();
                        }
                    });
                    projectId.setOutputMarkupId(true);
                    projectId.setMarkupId(generatedSeriesId + "_id");
                    projectDiv.add(projectId);

                    HiddenField<String> projectOrder = new HiddenField<String>("projectOrder", new Model<String>()
                    {

                        @Override
                        public void setObject(String orderNumber)
                        {
                            project.setOrderNumber(Integer.parseInt(orderNumber));
                        }
                    });
                    projectOrder.setOutputMarkupId(true);
                    projectOrder.setMarkupId(generatedSeriesId + "_order");
                    projectDiv.add(projectOrder);

                    HiddenField<String> projectDeleted = new HiddenField<String>("projectDeleted", new Model<String>()
                    {

                        @Override
                        public void setObject(String object)
                        {
                            Boolean value = Boolean.valueOf(object);
                            if (value)
                            {
                                deletedProjects.add(project.getProjectId());
                            }
                        }
                    });
                    projectDeleted.setOutputMarkupId(true);
                    projectDeleted.setMarkupId(generatedSeriesId + "_delete");
                    projectDiv.add(projectDeleted);

                    TextField<String> projectUrl = new TextField<String>("projectUrl", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return project.getUrl();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            project.setUrl(value);
                        }
                    });
                    projectUrl.setOutputMarkupId(true);
                    projectUrl.setMarkupId(generatedSeriesId + "_url_input");
                    projectDiv.add(projectUrl);

                    TextField<String> projectTitle = new TextField<String>("projectTitle", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return project.getTitle();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            project.setTitle(value);
                        }
                    });
                    projectTitle.setOutputMarkupId(true);
                    projectTitle.setMarkupId(generatedSeriesId + "_name_input");
                    projectDiv.add(projectTitle);

                    item.add(projectDiv);
                }
            };

            this.add(projectRepeater);

            AjaxButton updateProjectsAjax = new AjaxButton("updateProjectsAjax", this)
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    SqlSession session = MybatisUtil.getSessionFactory().openSession();
                    try
                    {
                        ProjectsMapper projectsMapperCacheable = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
                        String exceptionText = validateData();
                        if (!Strings.isEmpty(exceptionText))
                        {
                            target.appendJavaScript("alert('" + exceptionText + "')");
                        }
                        else
                        {
                            for (Project project : projects)
                            {
                                if (project.getProjectId() != null)
                                {
                                    // update
                                    projectsMapperCacheable.updateProject(project);
                                }
                                else
                                {
                                    // insert
                                    projectsMapperCacheable.insertProject(project);
                                }
                            }

                            for (Integer projectId : deletedProjects)
                            {
                                // delete
                                projectsMapperCacheable.deleteProject(projectId);
                            }

                            session.commit();
                        }
                    }
                    finally
                    {
                        session.close();
                    }

                    deletedProjects.clear();
                }
            };

            this.add(updateProjectsAjax);
        }

        private final List<Project> projects;
        private Set<Integer> deletedProjects = new HashSet<Integer>();
    }*/

    @Override
    public boolean isVersioned()
    {
        return false;
    }

    public GlobalEdit()
    {
        SqlSession session = MybatisUtil.getSessionFactory().openSession();
        try
        {
            ProjectsMapper projectsMapperCacheable = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
            Collection<Project> projectsCollection = projectsMapperCacheable.getAllProjects();
            projects = Lists.newArrayList(projectsCollection);

            VolumeActivitiesMapper activitiesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumeActivitiesMapper.class);
            activities = activitiesMapperCacheable.getAllVolumeActivities();

            TeamsMapper teamsMapperCacheable = CachingFacade.getCacheableMapper(session, TeamsMapper.class);
            teams = teamsMapperCacheable.getAllTeams();

            TeamMembersMapper teamMembersMapperCacheable = CachingFacade.getCacheableMapper(session, TeamMembersMapper.class);
            teamMembers = teamMembersMapperCacheable.getAllTeamMembers();
        }
        finally
        {
            session.close();
        }

        Collections.sort(projects, new Comparator<Project>()
        {
            @Override
            public int compare(Project o1, Project o2)
            {
                return o1.getOrderNumber() - o2.getOrderNumber();
            }
        });

        HashMap<Integer, Team> teamIdToTeamMap = new HashMap<Integer, Team>();
        for (Team team : teams)
        {
            teamIdToTeamMap.put(team.getTeamId(), team);
        }
        for (TeamMember member : teamMembers)
        {
            member.setTeam(teamIdToTeamMap.get(member.getTeamId()));
        }

/*        add(new ProjectsForm(projects));
        add(new ActivitiesForm(activities));
        add(new TeamsForm(teams));
        add(new TeamMembersForm(teamMembers, teams));*/

        add(new AdminAffixedListPanel<Project>("projects", "Серии", new ListModel<Project>(projects))
        {
            @Override
            public void onSubmit()
            {
                SqlSession session = MybatisUtil.getSessionFactory().openSession();
                try
                {
                    ProjectsMapper mapper = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
                    for (Project item : model.getObject())
                    {
                        if (!removed.contains(item))
                        {
                            if (item.getProjectId() != null)
                            {
                                mapper.updateProject(item);
                            }
                            else
                            {
                                mapper.insertProject(item);
                            }
                        }
                    }
                    for (Project removedItem : removed)
                    {
                        if (removedItem.getProjectId() != null)
                        {
                            mapper.deleteProject(removedItem.getProjectId());
                        }
                    }
                    session.commit();
                }
                finally
                {
                    session.close();
                }
            }

            @Override
            protected Project makeItem()
            {
                return new Project();
            }

            @Override
            protected Component getSelectorItemLabelComponent(String id, IModel<Project> model)
            {
                return new Label(id, new PropertyModel<Project>(model, "title"));
            }

            @Override
            protected Component getFormItemLabelComponent(String id, IModel<Project> model)
            {
                return new ProjectFormItemPanel(id, model);
            }
        }.setSortable(true));

        add(new AdminAffixedListPanel<VolumeActivity>("activities", "Виды работ", new ListModel<VolumeActivity>(activities))
        {

            @Override
            public void onSubmit()
            {
                SqlSession session = MybatisUtil.getSessionFactory().openSession();
                try
                {
                    VolumeActivitiesMapper mapper = CachingFacade.getCacheableMapper(session, VolumeActivitiesMapper.class);
                    for (VolumeActivity item : model.getObject())
                    {
                        if (!removed.contains(item))
                        {
                            if (item.getActivityId() != null)
                            {
                                mapper.updateVolumeActivity(item);
                            }
                            else
                            {
                                mapper.insertVolumeActivity(item);
                            }
                        }
                    }
                    for (VolumeActivity removedItem : removed)
                    {
                        if (removedItem.getActivityId() != null)
                        {
                            mapper.deleteVolumeActivity(removedItem.getActivityId());
                        }
                    }
                    session.commit();
                }
                finally
                {
                    session.close();
                }
            }

            @Override
            protected VolumeActivity makeItem()
            {
                return new VolumeActivity();
            }

            @Override
            protected Component getSelectorItemLabelComponent(String id, IModel<VolumeActivity> model)
            {
                return new Label(id, new PropertyModel<VolumeActivity>(model, "activityName"));
            }

            @Override
            protected Component getFormItemLabelComponent(String id, IModel<VolumeActivity> model)
            {
                return new VolumeActivityFormItemPanel(id, model);
            }

        });

        add(teamsAdminAffixedListPanel = new AdminAffixedListPanel<Team>("teams", "Команды", new ListModel<Team>(teams))
        {
            @Override
            public void onSubmit()
            {
                SqlSession session = MybatisUtil.getSessionFactory().openSession();
                try
                {
                    TeamsMapper mapper = CachingFacade.getCacheableMapper(session, TeamsMapper.class);
                    for (Team item : model.getObject())
                    {
                        if (!removed.contains(item))
                        {
                            if (item.getTeamId() != null)
                            {
                                mapper.updateTeam(item);
                            }
                            else
                            {
                                mapper.insertTeam(item);
                            }
                        }
                    }
                    for (Team removedItem : removed)
                    {
                        if (removedItem.getTeamId() != null)
                        {
                            mapper.deleteTeam(removedItem.getTeamId());
                        }
                    }
                    session.commit();
                }
                finally
                {
                    session.close();
                }
            }

            @Override
            protected Team makeItem()
            {
                return new Team();
            }

            @Override
            protected Component getSelectorItemLabelComponent(String id, IModel<Team> model)
            {
                return new Label(id, new PropertyModel<Team>(model, "teamName"));
            }

            @Override
            protected Component getFormItemLabelComponent(String id, IModel<Team> model)
            {
                return new TeamFormItemPanel(id, model);
            }

            @Override
            protected void onRefresh(AjaxRequestTarget target, Form<?> form)
            {
                for (Component component : ((AbstractRepeater) GlobalEdit.this.get("teamMembers:form:formBlock:repeater")))
                {
                    target.add(component.get("item:label:team"));
                }
            }
        });


        add(new AdminAffixedListPanel<TeamMember>("teamMembers", "Члены команд", new ListModel<TeamMember>(teamMembers))
        {
            @Override
            public void onSubmit()
            {
                teamsAdminAffixedListPanel.onSubmit();
                SqlSession session = MybatisUtil.getSessionFactory().openSession();
                try
                {
                    TeamMembersMapper mapper = CachingFacade.getCacheableMapper(session, TeamMembersMapper.class);
                    for (TeamMember item : model.getObject())
                    {
                        if (!removed.contains(item))
                        {
                            if (item.getMemberId() != null)
                            {
                                mapper.updateTeamMember(item);
                            }
                            else
                            {
                                mapper.insertTeamMember(item);
                            }
                        }
                    }
                    for (TeamMember removedItem : removed)
                    {
                        if (removedItem.getMemberId() != null)
                        {
                            mapper.deleteTeamMember(removedItem.getMemberId());
                        }
                    }
                    session.commit();
                }
                finally
                {
                    session.close();
                }
            }

            @Override
            protected TeamMember makeItem()
            {
                TeamMember teamMember = new TeamMember();
                teamMember.setTeam(teams.get(0));
                teamMember.setActive(true);
                return teamMember;
            }

            @Override
            protected Component getSelectorItemLabelComponent(String id, IModel<TeamMember> model)
            {
                return new Label(id, new PropertyModel<TeamMember>(model, "nikname"));
            }

            @Override
            protected Component getFormItemLabelComponent(String id, IModel<TeamMember> model)
            {
                return new TeamMemberFormItemPanel(id, model, teams);
            }
        });

    }

    private List<Project> projects;
    private List<VolumeActivity> activities;
    private List<Team> teams;
    private List<TeamMember> teamMembers;
    private AdminAffixedListPanel<Team> teamsAdminAffixedListPanel;
}