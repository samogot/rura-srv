package ru.ruranobe.wicket.webpages;

import com.google.common.collect.Lists;
import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.tables.Project;
import ru.ruranobe.wicket.webpages.base.AdminLayoutPage;

import java.util.*;

public class GlobalEdit extends AdminLayoutPage
{
    public GlobalEdit()
    {
        SqlSession session = MybatisUtil.getSessionFactory().openSession();
        try
        {
            ProjectsMapper projectsMapperCacheable = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
            Collection<Project> projectsCollection = projectsMapperCacheable.getAllProjects();
            projectsInfoHolder = new ProjectsInfoHolder(projectsCollection);
        }
        finally
        {
            session.close();
        }

        Form projectsForm = new Form("projectsForm");

        /*AjaxButton addProject = new AjaxButton("addProject", projectsForm)
        {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                Project project = new Project();
                project.setNameRu("Неопределенный");
                projectsInfoHolder.addProject(project);
                target.add(form);
            }
        };

        projectsForm.add(addProject);

        AjaxButton deleteProject = new AjaxButton("deleteProject", projectsForm)
        {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                boolean result = projectsInfoHolder.deleteProject();
                if (result)
                {
                    target.add(form);
                }
            }
        };

        projectsForm.add(deleteProject);*/

        ListView<Project> projectNameRepeater = new ListView<Project>("projectNameRepeater", projectsInfoHolder.getProjects())
        {
            @Override
            protected void populateItem(ListItem<Project> item)
            {
                Project project = item.getModelObject();
                String name = project.getNameRu();
                name = Strings.isEmpty(name) ? project.getTitle() : name;
                Label projectName = new Label("projectName", name);
                item.add(projectName);
            }
        };
        projectsForm.add(projectNameRepeater);

        ListView<Project> projectRepeater = new ListView<Project>("projectRepeater", projectsInfoHolder.getProjects())
        {
            @Override
            protected void populateItem(ListItem<Project> item)
            {
                final Project project = item.getModelObject();
                HiddenField<Integer> projectId = new HiddenField<Integer>("projectId", new Model<Integer>()
                {
                    @Override
                    public Integer getObject()
                    {
                        return project.getProjectId();
                    }
                });
                item.add(projectId);
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
                item.add(projectUrl);
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
                item.add(projectTitle);
            }
        };

        projectsForm.add(projectRepeater);

        /*AjaxButton updateProjectsAjax = new AjaxButton("updateProjectsAjax", projectsForm)
        {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                SqlSession session = MybatisUtil.getSessionFactory().openSession();
                try
                {
                    ProjectsMapper projectsMapperCacheable = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
                    String exceptionText = projectsInfoHolder.validateData();
                    if (!Strings.isEmpty(exceptionText))
                    {
                        target.appendJavaScript("alert('" + exceptionText + "')");
                    }
                    else
                    {
                        for (Project project : projectsInfoHolder.getProjects())
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

                        for (Integer projectId : projectsInfoHolder.getDeletedProjectIds())
                        {
                            // delete
                            projectsMapperCacheable.deleteProject(projectId);
                        }

                        projectsInfoHolder.doPostUpdateActions();
                        session.commit();
                    }
                }
                finally
                {
                    session.close();
                }
            }
        };

        projectsForm.add(updateProjectsAjax);*/

        add(projectsForm);

        add(new AbstractDefaultAjaxBehavior()
        {
            @Override
            protected void respond(AjaxRequestTarget target)
            {
                JSONArray jsonProjects = new JSONArray(getRequest().getRequestParameters().getParameterValue("projects").toOptionalString());
                for (int i = 0; i < jsonProjects.length(); ++i)
                {
                    JSONObject jsonSubProject = jsonProjects.getJSONObject(i);
                    String projectTitle = jsonSubProject.getString("projectTitle");
                }
            }

            public void renderHead(Component component, IHeaderResponse response)
            {
                super.renderHead(component, response);
                String callbackUrl = getCallbackUrl().toString();
                response.render(JavaScriptHeaderItem.forScript("var projectsCallbackUrl='" + callbackUrl + "';", "projects"));
            }
        });
    }

    private class ProjectsInfoHolder
    {
        public ProjectsInfoHolder(Collection<Project> projectsCollection)
        {
            projects = Lists.newArrayList(projectsCollection);
            for (Project project : projects)
            {
                sequenceNumber = Math.max(sequenceNumber, project.getOrderNumber());
            }
        }

        public void addProject(Project project)
        {
            sequenceNumber++;
            project.setOrderNumber(sequenceNumber);
            selectedProject = null;
        }

        public boolean deleteProject()
        {
            boolean result = false;
            if (selectedProject != null)
            {
                projects.remove(selectedProject);
                sequenceNumber--;
                deletedProjectIds.add(selectedProject.getProjectId());
                result = true;
            }
            selectedProject = null;
            return result;
        }

        public List<Project> getProjects()
        {
            return projects;
        }

        public Set<Integer> getDeletedProjectIds()
        {
            return deletedProjectIds;
        }

        public void doPostUpdateActions()
        {
            deletedProjectIds.clear();
            selectedProject = null;
        }

        public String validateData()
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

        private int sequenceNumber = 0;
        private Project selectedProject = null;
        private Set<Integer> deletedProjectIds = new HashSet<Integer>();
        private List<Project> projects = new ArrayList<Project>();
    }

    private final ProjectsInfoHolder projectsInfoHolder;
}