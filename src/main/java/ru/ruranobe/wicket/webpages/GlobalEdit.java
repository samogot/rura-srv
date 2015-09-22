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
import ru.ruranobe.mybatis.mappers.*;
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
            Collection<Project> projectsCollection = projectsMapperCacheable.getRootProjects();
            projects = Lists.newArrayList(projectsCollection);

            VolumeActivitiesMapper activitiesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumeActivitiesMapper.class);
            activities = activitiesMapperCacheable.getAllVolumeActivities();

            TeamsMapper teamsMapperCacheable = CachingFacade.getCacheableMapper(session, TeamsMapper.class);
            teams = teamsMapperCacheable.getAllTeams();

            TeamMembersMapper teamMembersMapperCacheable = CachingFacade.getCacheableMapper(session, TeamMembersMapper.class);
            teamMembers = teamMembersMapperCacheable.getAllTeamMembers();

            ExternalResourcesMapper externalResourcesMapperCacheable = CachingFacade.getCacheableMapper(session, ExternalResourcesMapper.class);
            for (Project project : projects)
            {
                if (project.getImageId() != null)
                {
                    project.setImage(externalResourcesMapperCacheable.getExternalResourceById(project.getImageId()));
                }
            }
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