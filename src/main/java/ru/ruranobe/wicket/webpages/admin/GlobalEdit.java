package ru.ruranobe.wicket.webpages.admin;

import com.google.common.collect.Lists;
import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.AbstractRepeater;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.mybatis.entities.tables.Team;
import ru.ruranobe.mybatis.entities.tables.TeamMember;
import ru.ruranobe.mybatis.entities.tables.VolumeActivity;
import ru.ruranobe.mybatis.mappers.*;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.components.admin.AdminAffixedListPanel;
import ru.ruranobe.wicket.components.admin.BannerUploadComponent;
import ru.ruranobe.wicket.webpages.base.AdminLayoutPage;

import java.util.*;

@AuthorizeInstantiation(Roles.ADMIN)
public class GlobalEdit extends AdminLayoutPage
{

    private List<String> allRoles;
    private List<Team> teams;
    private AdminAffixedListPanel<Team> teamsAdminAffixedListPanel;

    public GlobalEdit()
    {
        List<Project> projects;
        List<VolumeActivity> activities;
        List<TeamMember> teamMembers;
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            ProjectsMapper projectsMapperCacheable = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
            Collection<Project> projectsCollection = projectsMapperCacheable.getRootProjects();
            projects = Lists.newArrayList(projectsCollection);

            VolumeActivitiesMapper activitiesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumeActivitiesMapper.class);
            activities = activitiesMapperCacheable.getAllVolumeActivities();

            TeamsMapper teamsMapperCacheable = CachingFacade.getCacheableMapper(session, TeamsMapper.class);
            teams = teamsMapperCacheable.getAllTeams();

            TeamMembersMapper teamMembersMapperCacheable = CachingFacade.getCacheableMapper(session, TeamMembersMapper.class);
            teamMembers = teamMembersMapperCacheable.getAllTeamMembersWithUserName();

            RolesMapper rolesMapperCacheable = CachingFacade.getCacheableMapper(session, RolesMapper.class);
            allRoles = rolesMapperCacheable.getAllUserGroups();
            for (TeamMember member : teamMembers)
            {
                if (member.getUserId() != null)
                {
                    member.setUserRoles(rolesMapperCacheable.getUserGroupsByUser(member.getUserId()));
                }
            }

            ExternalResourcesMapper externalResourcesMapperCacheable = CachingFacade.getCacheableMapper(session, ExternalResourcesMapper.class);
            for (Project project : projects)
            {
                if (project.getImageId() != null)
                {
                    project.setImage(externalResourcesMapperCacheable.getExternalResourceById(project.getImageId()));
                }
            }
        }

        Collections.sort(projects, new Comparator<Project>()
        {
            @Override
            public int compare(Project o1, Project o2)
            {
                return o1.getOrderNumber() - o2.getOrderNumber();
            }
        });

        HashMap<Integer, Team> teamIdToTeamMap = new HashMap<>();
        for (Team team : teams)
        {
            teamIdToTeamMap.put(team.getTeamId(), team);
        }
        for (TeamMember member : teamMembers)
        {
            member.setTeam(teamIdToTeamMap.get(member.getTeamId()));
        }

        add(new AdminAffixedListPanel<Project>("projects", "Серии", new ListModel<>(projects))
        {
            @Override
            public void onSubmit()
            {
                try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
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
            }

            @Override
            protected Project makeItem()
            {
                Project project = new Project();
                project.setBannerHidden(true);
                project.setProjectHidden(true);
                project.setOnevolume(false);
                project.setUrl("");
                return project;
            }

            @Override
            protected Component getSelectorItemLabelComponent(String id, IModel<Project> model)
            {
                return new Label(id, new PropertyModel<Project>(model, "title"));
            }

            @Override
            protected Component getFormItemLabelComponent(String id, final IModel<Project> model)
            {
                return new Fragment(id, "projectsFormItemFragment", GlobalEdit.this, model)
                {
                    @Override
                    protected void onInitialize()
                    {
                        super.onInitialize();
                        add(new TextField<String>("url").setRequired(true).setLabel(Model.of("Ссылка")));
                        add(new TextField<String>("title").setRequired(true).setLabel(Model.of("Заголовок")));
                        add(new CheckBox("projectHidden"));
                        add(new CheckBox("bannerHidden"));
                        add(new BannerUploadComponent("image").setProject(model.getObject()));
                        add(new BookmarkablePageLink("link", ProjectEdit.class, model.getObject().getUrlParameters()));
                    }
                };
            }
        }.setSortable(true));

        add(new AdminAffixedListPanel<VolumeActivity>("activities", "Виды работ", new ListModel<>(activities))
        {

            @Override
            public void onSubmit()
            {
                try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
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
                return new Fragment(id, "activitiesFormItemFragment", GlobalEdit.this, model)
                {
                    @Override
                    protected void onInitialize()
                    {
                        super.onInitialize();
                        add(new TextField<String>("activityName").setRequired(true).setLabel(Model.of("Название")));
                        add(new DropDownChoice<String>("activityType", Arrays.asList("text", "image"))
                        {
                            @Override
                            protected boolean localizeDisplayValues()
                            {
                                return true;
                            }
                        }.setRequired(true));
                    }
                };
            }

        });

        add(teamsAdminAffixedListPanel = new AdminAffixedListPanel<Team>("teams", "Команды", new ListModel<>(teams))
        {
            @Override
            public void onSubmit()
            {
                try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
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
                return new Fragment(id, "teamsFormItemFragment", GlobalEdit.this, model)
                {
                    @Override
                    protected void onInitialize()
                    {
                        super.onInitialize();
                        add(new TextField<String>("teamName").setRequired(true).setLabel(Model.of("Название")));
                        add(new TextField<String>("teamWebsiteLink"));
                    }
                };
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

        add(new AdminAffixedListPanel<TeamMember>("teamMembers", "Члены команд", new ListModel<>(teamMembers))
        {
            @Override
            public void onSubmit()
            {
                teamsAdminAffixedListPanel.onSubmit();
                try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
                {
                    TeamMembersMapper mapper = CachingFacade.getCacheableMapper(session, TeamMembersMapper.class);
                    RolesMapper rolesMapper = CachingFacade.getCacheableMapper(session, RolesMapper.class);
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
                            if (item.getUserId() != null)
                            {
                                rolesMapper.deleteUserGroupsByUserId(item.getUserId());
                                if (!item.getUserRoles().isEmpty())
                                {
                                    rolesMapper.setUserGroupsByUserId(item.getUserId(), item.getUserRoles());
                                }
                            }
                        }
                    }
                    for (TeamMember removedItem : removed)
                    {
                        if (removedItem.getMemberId() != null)
                        {
                            mapper.deleteTeamMember(removedItem.getMemberId());
                        }
                        if (removedItem.getUserId() != null)
                        {
                            rolesMapper.deleteUserGroupsByUserId(removedItem.getUserId());
                        }
                    }
                    session.commit();
                }
            }

            @Override
            protected TeamMember makeItem()
            {
                TeamMember teamMember = new TeamMember();
                teamMember.setTeam(teams.get(0));
                return teamMember;
            }

            @Override
            protected Component getSelectorItemLabelComponent(String id, IModel<TeamMember> model)
            {
                return new Label(id, new PropertyModel<TeamMember>(model, "nickname"));
            }

            @Override
            protected Component getFormItemLabelComponent(String id, IModel<TeamMember> model)
            {
                return new Fragment(id, "teamMembersFormItemFragment", GlobalEdit.this, model)
                {
                    @Override
                    protected void onInitialize()
                    {
                        super.onInitialize();
                        add(new TextField<String>("nickname").setRequired(true).setLabel(Model.of("Никнейм")));
                        add(new DropDownChoice<>("team", teams).setNullValid(true)
                                                               .setChoiceRenderer(new ChoiceRenderer<Team>("teamName", "teamId"))
                                                               .setOutputMarkupId(true));
                        add(new TextField<String>("userName"));
                        add(new HiddenField<Integer>("userId"));
                        add(new ListMultipleChoice<String>("userRoles", allRoles)
                        {
                            @Override
                            protected void onComponentTag(ComponentTag tag)
                            {
                                super.onComponentTag(tag);
                                if (getDefaultModelObject() == null)
                                {
                                    tag.getAttributes().put("disabled", "disabled");
                                }
                            }
                        });
                    }
                };
            }
        });

    }

    @Override
    public boolean isVersioned()
    {
        return false;
    }
}