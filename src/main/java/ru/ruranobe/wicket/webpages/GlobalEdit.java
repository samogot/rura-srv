package ru.ruranobe.wicket.webpages;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
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
import ru.ruranobe.wicket.webpages.base.AdminLayoutPage;

import java.util.*;

public class GlobalEdit extends AdminLayoutPage
{
    public GlobalEdit()
    {
        SqlSession session = MybatisUtil.getSessionFactory().openSession();
        List<Project> projects = null;
        List<VolumeActivity> activities = null;
        List<Team> teams = null;
        List<TeamMember> teamMembers = null;
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

        add(new ProjectsForm(projects));
        add(new ActivitiesForm(activities));
        add(new TeamsForm(teams));
        add(new TeamMembersForm(teamMembers, teams));
    }

    private static class TeamMembersForm extends Form
    {
        public TeamMembersForm(List <TeamMember> teamMembersVar, List <Team> teams)
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
                    AttributeAppender addHref = new AttributeAppender("href", "#"+generatedTeamMemberId);
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

        private Map <String, Integer> teamNameToTeamId = new HashMap<String, Integer>();
        private Map <Integer, String> teamIdToTeamName = new HashMap<Integer, String>();
        private Set <Integer> deletedTeamMembers = new HashSet<Integer>();
        private final List<TeamMember> teamMembers;
    }

    private static class TeamsForm extends Form
    {
        public TeamsForm(List <Team> teamsVar)
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
                    AttributeAppender addHref = new AttributeAppender("href", "#"+generatedTeamId);
                    visibilityController.add(addHref);
                    AttributeAppender addAriaControls = new AttributeAppender("aria-controls", generatedTeamId);
                    visibilityController.add(addAriaControls);
                    Team team = item.getModelObject();
                    String name = team.getTeamName();
                    Label teamName = new Label("teamName", name);
                    teamName.setOutputMarkupId(true);
                    teamName.setMarkupId(generatedTeamId+"_name");
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

        private Set <Integer> deletedTeams = new HashSet<Integer>();
        private final List<Team> teams;
    }

    private static class ActivitiesForm extends Form
    {
        public ActivitiesForm(List <VolumeActivity> activitiesVar)
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
                    AttributeAppender addHref = new AttributeAppender("href", "#"+generatedActivityId);
                    visibilityController.add(addHref);
                    AttributeAppender addAriaControls = new AttributeAppender("aria-controls", generatedActivityId);
                    visibilityController.add(addAriaControls);
                    VolumeActivity activity = item.getModelObject();
                    String name = activity.getActivityName();
                    Label activityName = new Label("activityName", name);
                    activityName.setOutputMarkupId(true);
                    activityName.setMarkupId(generatedActivityId+"_name");
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

                    }, Arrays.asList("текст","изображение"));
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

        private Set <Integer> deletedActivities = new HashSet<Integer>();
        private final List<VolumeActivity> activities;

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
    }

    private static class ProjectsForm extends Form
    {
        public ProjectsForm(List <Project> projectsVar)
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
                    AttributeAppender addHref = new AttributeAppender("href", "#"+generatedSeriesId);
                    visibilityController.add(addHref);
                    AttributeAppender addAriaControls = new AttributeAppender("aria-controls", generatedSeriesId);
                    visibilityController.add(addAriaControls);
                    Project project = item.getModelObject();
                    String name = project.getNameRu();
                    name = Strings.isEmpty(name) ? project.getTitle() : name;
                    Label projectName = new Label("projectName", name);
                    projectName.setOutputMarkupId(true);
                    projectName.setMarkupId(generatedSeriesId+"_name");
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

        private Set <Integer> deletedProjects = new HashSet<Integer>();
        private final List<Project> projects;
    }
}