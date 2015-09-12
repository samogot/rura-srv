package ru.ruranobe.wicket.webpages;

import com.google.common.collect.ImmutableMap;
import com.mysql.jdbc.StringUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.handler.resource.ResourceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ruranobe.config.ApplicationContext;
import ru.ruranobe.engine.Webpage;
import ru.ruranobe.engine.image.ImageServices;
import ru.ruranobe.engine.image.RuraImage;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.tables.ExternalResource;
import ru.ruranobe.mybatis.tables.Project;
import ru.ruranobe.mybatis.tables.Volume;
import ru.ruranobe.wicket.RuraConstants;
import ru.ruranobe.wicket.components.ContentsHolder;
import ru.ruranobe.wicket.components.admin.AdminInfoFormPanel;
import ru.ruranobe.wicket.components.sidebar.ContentsModule;
import ru.ruranobe.wicket.webpages.base.AdminLayoutPage;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProjectEdit extends AdminLayoutPage
{

    public ProjectEdit(final PageParameters parameters)
    {
        project = getProject(parameters);

        if (project == null)
        {
            throw RuranobeUtils.REDIRECT_TO_404;
        }
        add(new AdminInfoFormPanel<Project>("info","Информация", new CompoundPropertyModel<Project>(project))
        {
            @Override
            public void onSubmit()
            {

            }

            @Override
            protected Component getContentItemLabelComponent(String id, IModel<Project> model)
            {
                return new Label(id, PropertyModel.of(model,"title"));
            }
        });

        SqlSession session = MybatisUtil.getSessionFactory().openSession();
        try
        {
            volumes = CachingFacade.getCacheableMapper(session, VolumesMapper.class).getVolumesByProjectId(project.getProjectId());
            subProjects = CachingFacade.getCacheableMapper(session, ProjectsMapper.class).getSubProjectsByParentProjectId(project.getProjectId());
            for (Project subProject : subProjects)
            {
                projectIdToProject.put(subProject.getProjectId(), subProject);
            }
        }
        finally
        {
            session.close();
        }
    }


    private Project getProject(final PageParameters parameters)
    {
        String projectUrl = parameters.get("project").toOptionalString();
        SqlSession session = MybatisUtil.getSessionFactory().openSession();
        try
        {
            return CachingFacade.getCacheableMapper(session, ProjectsMapper.class).getProjectByUrl(projectUrl);
        }
        finally
        {
            session.close();
        }
    }

    private final List<Project> subProjects;
    private final List<Volume> volumes;
    private final Map<Integer, Volume> volumeTableOrderNumberToVolume = new HashMap<Integer, Volume>();
    private final Map<Integer, Project> projectIdToProject = new HashMap<Integer, Project>();
    private final Project project;
    private final Set<Integer> deletedVolumeIds = new HashSet<Integer>();
    private static final long serialVersionUID = 1L;
}
