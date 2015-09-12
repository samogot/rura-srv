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
import org.apache.wicket.model.Model;
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
import ru.ruranobe.wicket.components.sidebar.ContentsModule;
import ru.ruranobe.wicket.webpages.base.AdminLayoutPage;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProjectEdit_ extends AdminLayoutPage
{

    public ProjectEdit_(final PageParameters parameters)
    {
        project = getProject(parameters);

        if (project == null)
        {
            throw RuranobeUtils.REDIRECT_TO_404;
        }

        final Form<Project> projectInfoForm = new Form<Project>("projectInfoForm", new CompoundPropertyModel<Project>(project));
        projectInfoForm.setOutputMarkupId(true);
        final TextField<String> url = new TextField<String>("url");
        final TextField<String> title = new TextField<String>("title");
        final TextField<String> nameJp = new TextField<String>("nameJp");
        final TextField<String> nameEn = new TextField<String>("nameEn");
        final TextField<String> nameRu = new TextField<String>("nameRu");
        final TextField<String> nameRomaji = new TextField<String>("nameRomaji");
        final TextField<String> author = new TextField<String>("author");
        final TextField<String> illustrator = new TextField<String>("illustrator");
        final CheckBox onevolume = new CheckBox("onevolume");
        final CheckBox bannerHidden = new CheckBox("bannerHidden");
        final CheckBox projectHidden = new CheckBox("projectHidden");
        final TextArea<String> franchise = new TextArea<String>("franchise");
        final TextArea<String> annotation = new TextArea<String>("annotation");
        final FeedbackPanel updateProjectAjaxFeedback = new FeedbackPanel("updateProjectAjaxFeedback");
        updateProjectAjaxFeedback.setOutputMarkupId(true);
        updateProjectAjaxFeedback.setFilter(new ContainerFeedbackMessageFilter(projectInfoForm));
        final AjaxButton updateProjectAjax = new AjaxButton("updateProjectAjax", projectInfoForm)
        {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                info("Данные были успешно обновлены.");
                SqlSession session = MybatisUtil.getSessionFactory().openSession();
                try
                {
                    CachingFacade.getCacheableMapper(session, ProjectsMapper.class).updateProject(project);
                    session.commit();
                    target.add(updateProjectAjaxFeedback);
                }
                finally
                {
                    session.close();
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form)
            {
                error("Не удалось обновить данные.");
                target.add(updateProjectAjaxFeedback);
            }
        };

        updateProjectAjax.setOutputMarkupId(true);

        projectInfoForm.add(url);
        projectInfoForm.add(title);
        projectInfoForm.add(nameJp);
        projectInfoForm.add(nameEn);
        projectInfoForm.add(nameRu);
        projectInfoForm.add(nameRomaji);
        projectInfoForm.add(author);
        projectInfoForm.add(illustrator);
        projectInfoForm.add(onevolume);
        projectInfoForm.add(bannerHidden);
        projectInfoForm.add(projectHidden);
        projectInfoForm.add(franchise);
        projectInfoForm.add(annotation);
        projectInfoForm.add(updateProjectAjaxFeedback);
        projectInfoForm.add(updateProjectAjax);
        add(projectInfoForm);

        SqlSession session = MybatisUtil.getSessionFactory().openSession();
        try
        {
            volumes = CachingFacade.getCacheableMapper(session, VolumesMapper.class).getVolumesByProjectId(project.getProjectId());
            VolumesForm volumesForm = new VolumesForm(volumes);

            final ListView<Volume> volumeRepeater = new ListView<Volume>("volumeRepeater", volumes)
            {

                @Override
                protected void populateItem(final ListItem<Volume> listItem)
                {
                    final Volume volume = listItem.getModelObject();
                    volumeTableOrderNumberToVolume.put(listItem.getIndex(), volume);

                    Label volId = new Label("volOrderNumber", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return Integer.toString(listItem.getIndex());
                        }
                    });
                    TextField<String> volUrl = new TextField<String>("volUrl", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return volume.getUrl();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            volume.setUrl(value);
                        }
                    });
                    TextField<String> volNameFile = new TextField<String>("volNameFile", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return volume.getNameFile();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            volume.setNameFile(value);
                        }
                    });
                    TextField<String> volTitle = new TextField<String>("volTitle", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return volume.getNameTitle();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            volume.setNameTitle(value);
                        }
                    });
                    TextField<String> volNameJp = new TextField<String>("volNameJp", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return volume.getNameJp();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            volume.setNameJp(value);
                        }
                    });
                    TextField<String> volNameEn = new TextField<String>("volNameEn", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return volume.getNameEn();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            volume.setNameEn(value);
                        }
                    });
                    TextField<String> volNameRu = new TextField<String>("volNameRu", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return volume.getNameRu();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            volume.setNameRu(value);
                        }
                    });
                    TextField<String> volNameRomaji = new TextField<String>("volNameRomaji", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return volume.getNameRomaji();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            volume.setNameRomaji(value);
                        }
                    });
                    TextField<String> volNameShort = new TextField<String>("volNameShort", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return volume.getNameShort();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            volume.setNameShort(value);
                        }
                    });
                    TextField<String> volSequenceNumber = new TextField<String>("volSequenceNumber", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return volume.getSequenceNumber() == null ? null : Integer.toString(volume.getSequenceNumber());
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            volume.setSequenceNumber(Integer.parseInt(value));
                        }
                    });
                    TextField<String> volAuthor = new TextField<String>("volAuthor", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return volume.getAuthor();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            volume.setAuthor(value);
                        }
                    });
                    TextField<String> volIllustrator = new TextField<String>("volIllustrator", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return volume.getIllustrator();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            volume.setIllustrator(value);
                        }
                    });
                    TextField<String> volReleaseDate = new TextField<String>("volReleaseDate", new Model<String>()
                    {
                        private final SimpleDateFormat sdf = new SimpleDateFormat("YYYY:MM:DD");

                        @Override
                        public String getObject()
                        {
                            return volume.getReleaseDate() == null ? null : sdf.format(volume.getReleaseDate());
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            try
                            {
                                volume.setReleaseDate(sdf.parse(value));
                            }
                            catch (ParseException ex)
                            {
                                throw new RuntimeException(ex);
                            }
                        }
                    });
                    TextField<String> volIsbn = new TextField<String>("volIsbn", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return volume.getIsbn();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            volume.setIsbn(value);
                        }
                    });
                    DropDownChoice<String> volumeType = new DropDownChoice<String>("volumeType", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return volume.getVolumeType();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            volume.setVolumeType(value);
                        }
                    }, Arrays.asList("Ранобэ", "Побочные истории", "Авторские додзинси", "Другое"));
                    DropDownChoice<String> volumeStatus = new DropDownChoice<String>("volumeStatus", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return RuraConstants.VOLUME_STATUS_TO_FULL_TEXT.get(volume.getVolumeStatus());
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            volume.setVolumeStatus(RuraConstants.VOLUME_STATUS_FULL_TEXT_TO_STATUS.get(value));
                        }
                    }, Arrays.asList("Заброшенный сторонний перевод", "Активный сторонний перевод", "Завершенный сторонний перевод", "Отсутствует анлейт", "Заморожен", "Приостановлен", "Очередь перевода", "Перевод в онгоинге", "Перевод", "Редактура", "Не оформлен", "Завершен"));
                    TextArea<String> volAnnotation = new TextArea<String>("volAnnotation", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return volume.getAnnotation();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            volume.setAnnotation(value);
                        }
                    });
                    CheckBox volAdult = new CheckBox("volAdult", new Model<Boolean>()
                    {
                        @Override
                        public Boolean getObject()
                        {
                            return volume.isAdult();
                        }

                        @Override
                        public void setObject(final Boolean value)
                        {
                            volume.setAdult(value);
                        }
                    });

                    listItem.add(volId);
                    listItem.add(volUrl);
                    listItem.add(volNameFile);
                    listItem.add(volTitle);
                    listItem.add(volNameJp);
                    listItem.add(volNameEn);
                    listItem.add(volNameRu);
                    listItem.add(volNameShort);
                    listItem.add(volNameRomaji);
                    listItem.add(volSequenceNumber);
                    listItem.add(volAuthor);
                    listItem.add(volIllustrator);
                    listItem.add(volReleaseDate);
                    listItem.add(volIsbn);
                    listItem.add(volumeType);
                    listItem.add(volumeStatus);
                    listItem.add(volAnnotation);
                    listItem.add(volAdult);
                }
            };
            volumeRepeater.setOutputMarkupId(true);

            AjaxButton addVolume = new AjaxButton("addVolume", volumesForm)
            {

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    VolumesForm volumeForm = (VolumesForm) form;
                    volumeForm.addVolume(project.getProjectId());
                    target.add(volumeForm);
                }
            };

            AjaxButton cloneVolume = new AjaxButton("cloneVolume", volumesForm)
            {

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    if (selectedVolume == null)
                    {
                        target.appendJavaScript("alert('Сначала выделите том!');");
                    }
                    else
                    {
                        VolumesForm volumeForm = (VolumesForm) form;
                        volumeForm.cloneVolume();
                        target.add(volumeForm);
                    }
                }
            };

            AjaxButton deleteVolume = new AjaxButton("deleteVolume", volumesForm)
            {

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    if (selectedVolume == null)
                    {
                        target.appendJavaScript("alert('Сначала выделите том!');");
                    }
                    else
                    {
                        VolumesForm volumeForm = (VolumesForm) form;
                        volumeForm.deleteVolume();
                        target.add(volumeForm);
                    }
                }
            };

            final FeedbackPanel updateVolumesAjaxFeedback = new FeedbackPanel("updateVolumesAjaxFeedback");
            updateVolumesAjaxFeedback.setOutputMarkupId(true);
            updateVolumesAjaxFeedback.setFilter(new ContainerFeedbackMessageFilter(volumesForm));

            AjaxButton updateVolumesAjax = new AjaxButton("updateVolumesAjax", volumesForm)
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    info("Данные были успешно обновлены.");
                    SqlSession session = MybatisUtil.getSessionFactory().openSession();
                    try
                    {
                        VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);

                        for (Volume volume : volumes)
                        {
                            if (volume.getVolumeId() != null)
                            {
                                // update
                                volumesMapperCacheable.updateVolume(volume);
                            }
                            else
                            {
                                // insert
                                volumesMapperCacheable.insertVolume(volume);
                            }
                        }

                        for (Integer volumeId : deletedVolumeIds)
                        {
                            // delete
                            volumesMapperCacheable.deleteVolume(volumeId);
                        }

                        session.commit();

                        deletedVolumeIds.clear();

                        target.add(updateVolumesAjaxFeedback);
                    }
                    finally
                    {
                        session.close();
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form)
                {
                    error("Не удалось обновить данные.");
                    target.add(updateVolumesAjaxFeedback);
                }

            };

            volumesForm.add(addVolume);
            volumesForm.add(deleteVolume);
            volumesForm.add(cloneVolume);
            volumesForm.add(volumeRepeater);
            volumesForm.add(updateVolumesAjax);
            volumesForm.add(updateVolumesAjaxFeedback);

            add(volumesForm);

            subProjects = CachingFacade.getCacheableMapper(session, ProjectsMapper.class).getSubProjectsByParentProjectId(project.getProjectId());
            for (Project subProject : subProjects)
            {
                projectIdToProject.put(subProject.getProjectId(), subProject);
            }

            subProjectForm = new Form("subProjectForm");
            ListView<Project> subProjectRepeater = new ListView<Project>("subProjectRepeater", subProjects)
            {
                @Override
                protected void populateItem(ListItem<Project> listItem)
                {
                    final Project subProject = listItem.getModelObject();
                    TextField<String> subProjectName = new TextField<String>("subProjectName", new Model<String>()
                    {

                        @Override
                        public String getObject()
                        {
                            return subProject.getTitle();
                        }

                    });
                    HiddenField<String> subProjectId = new HiddenField<String>("subProjectId", new Model<String>()
                    {

                        @Override
                        public String getObject()
                        {
                            return subProject.getProjectId().toString();
                        }

                    });
                    listItem.add(subProjectId);
                    listItem.add(subProjectName);
                }
            };
            subProjectForm.add(subProjectRepeater);
            subProjectRepeater.setOutputMarkupId(true);
            add(subProjectForm);
        }
        finally
        {
            session.close();
        }

        add(new SubVolumesEditAjaxBehavior());
        add(new SubProjectsEditAjaxBehavior());
        add(new ImageUploadAjaxBehaviour());


        List<ContentsHolder> contentsHolders = new ArrayList<ContentsHolder>();
        contentsHolders.add(new ContentsHolder("#seriesHead", "Серии"));
        contentsHolders.add(new ContentsHolder("#typesHead", "Виды"));
        contentsHolders.add(new ContentsHolder("#teamsHead", "Команды"));
        contentsHolders.add(new ContentsHolder("#membersHead", "Члены команды"));
        sidebarModules.add(new ContentsModule("sidebarModule", contentsHolders));
    }

    private class VolumesForm extends Form<List<Volume>>
    {
        private int maxSequenceNumber = 1;
        private List<Volume> volumes;

        public VolumesForm(List<Volume> volumes)
        {
            super("volumesForm");
            this.volumes = volumes;
            for (Volume volume : volumes)
            {
                if (volume.getSequenceNumber() != null
                    && volume.getSequenceNumber() > maxSequenceNumber)
                {
                    maxSequenceNumber = volume.getSequenceNumber();
                }
            }
        }

        public void addVolume(int projectId)
        {
            Volume volume = new Volume();
            volume.setProjectId(projectId);
            maxSequenceNumber++;
            volume.setSequenceNumber(maxSequenceNumber);
            volumes.add(volume);
            selectedVolume = null;
        }

        public void deleteVolume()
        {
            if (selectedVolume != null)
            {
                deletedVolumeIds.add(selectedVolume.getVolumeId());
                volumes.remove(selectedVolume);
            }
            selectedVolume = null;
        }

        public void cloneVolume()
        {
            if (selectedVolume != null)
            {
                maxSequenceNumber++;
                Volume cloneVolume = new Volume(selectedVolume, maxSequenceNumber);
                volumes.add(cloneVolume);
            }
            selectedVolume = null;
        }
    }

    private class SubVolumesEditAjaxBehavior extends AbstractDefaultAjaxBehavior
    {

        @Override
        protected void respond(final AjaxRequestTarget target)
        {
            Integer selectedVolumeId = getRequest().getRequestParameters().getParameterValue("tableOrderNumber").toOptionalInteger();
            selectedVolume = volumeTableOrderNumberToVolume.get(selectedVolumeId);
        }

        @Override
        public void renderHead(Component component, IHeaderResponse response)
        {
            super.renderHead(component, response);
            String componentMarkupId = component.getMarkupId();
            String callbackUrl = getCallbackUrl().toString();

            response.render(JavaScriptHeaderItem.forScript("var componentMarkupId1='" + componentMarkupId + "'; var callbackUrl1='" + callbackUrl + "';", "volumes"));
        }
    }

    private class SubProjectsEditAjaxBehavior extends AbstractDefaultAjaxBehavior
    {

        @Override
        protected void respond(final AjaxRequestTarget target)
        {
            JSONArray jsonSubProjects = new JSONArray(getRequest().getRequestParameters().getParameterValue("subProjects").toOptionalString());
            SqlSession session = MybatisUtil.getSessionFactory().openSession();
            try
            {
                ProjectsMapper projectsMapperCacheable = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
                Set<Integer> subProjectIds = new HashSet<Integer>();
                for (int i = 0; i < jsonSubProjects.length(); ++i)
                {
                    JSONObject jsonSubProject = jsonSubProjects.getJSONObject(i);
                    String projectTitle = jsonSubProject.getString("projectTitle");

                    if (StringUtils.isEmptyOrWhitespaceOnly(projectTitle))
                    {
                        throw new RuntimeException("Неправильно задано название подпроекта - оно пустое или состоит только из пробелов.");
                    }

                    Integer orderNumber = jsonSubProject.getInt("orderNumber");
                    Integer projectId = null;
                    try
                    {
                        projectId = jsonSubProject.getInt("projectId");
                    }
                    catch (JSONException ex)
                    {
                        // projectId not found...
                    }

                    if (projectId != null)
                    {
                        subProjectIds.add(projectId);
                    }

                    Project subProject = projectIdToProject.get(projectId);
                    if (subProject != null)
                    {
                        // update
                        subProject.setOrderNumber(orderNumber);
                        subProject.setTitle(projectTitle);
                        projectsMapperCacheable.updateProject(subProject);
                    }
                    else
                    {
                        // insert
                        subProject = Project.subProjectOf(project, orderNumber, projectTitle);
                        projectsMapperCacheable.insertProject(subProject);
                        subProjects.add(subProject);
                    }
                }

                // delete
                Set<Integer> deletedSubProjectIds = new HashSet<Integer>(projectIdToProject.keySet());
                deletedSubProjectIds.removeAll(subProjectIds);
                for (Integer deletedSubProjectId : deletedSubProjectIds)
                {
                    projectsMapperCacheable.deleteProject(deletedSubProjectId);
                    subProjects.remove(projectIdToProject.get(deletedSubProjectId));
                    projectIdToProject.remove(deletedSubProjectId);
                }

                projectIdToProject.clear();
                for (Project subProject : subProjects)
                {
                    projectIdToProject.put(subProject.getProjectId(), subProject);
                }

                session.commit();
            }
            finally
            {
                session.close();
            }

            Collections.sort(subProjects, new Comparator<Project>()
            {
                @Override
                public int compare(Project o1, Project o2)
                {
                    return o1.getOrderNumber().compareTo(o2.getOrderNumber());
                }
            });

            target.add(subProjectForm);
            target.appendJavaScript("$('.list-group').listgroup();");
        }


        @Override
        public void renderHead(Component component, IHeaderResponse response)
        {
            super.renderHead(component, response);
            String componentMarkupId = component.getMarkupId();
            String callbackUrl = getCallbackUrl().toString();

            response.render(JavaScriptHeaderItem.forScript("var componentMarkupId2='" + componentMarkupId + "'; var callbackUrl2='" + callbackUrl + "';", "subProjects"));
        }
    }

    private class ImageUploadAjaxBehaviour extends AbstractDefaultAjaxBehavior
    {
        @Override
        protected void respond(final AjaxRequestTarget target)
        {
            HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
            if (ServletFileUpload.isMultipartContent(request))
            {
                File imageTempFile;
                try
                {
                    imageTempFile = File.createTempFile("ruranobe-image_temp", ".tmp");
                }
                catch (IOException ex)
                {
                    throw new RuntimeException("Unable to create temp file during image upload", ex);
                }

                FileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                String uploadingFileExtension = null;
                String filename = null;
                try
                {
                    List<FileItem> items = upload.parseRequest(request);
                    for (FileItem item : items)
                    {
                        filename = filename == null ? item.getName() : null;
                        uploadingFileExtension = FilenameUtils.getExtension(filename);
                        item.write(imageTempFile);
                    }
                }
                catch (Exception ex)
                {
                    throw new RuntimeException("Unable to write uploading image to temp file", ex);
                }

                ApplicationContext context = RuranobeUtils.getApplicationContext();
                Webpage webpage = context.getWebpageByPageClass(this.getClass().getName());
                if (webpage == null)
                {
                    webpage = context.getWebpageByPageClass(ProjectEdit.class.getName());
                }
                Map<String, String> pageContextVariables = new ImmutableMap.Builder<String, String>()
                        .put("project", project.getTitle())
                        .build();
                RuraImage image = new RuraImage(imageTempFile, uploadingFileExtension, filename);
                List<ExternalResource> externalResources = ImageServices.uploadImage(image, webpage.getImageStorages(), pageContextVariables);
                ExternalResource externalResource = externalResources.iterator().next();
                project.setImageId(externalResource.getResourceId());
                imageTempFile.delete();

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss");
                JSONObject file = new JSONObject();
                file.put("url", externalResource.getUrl());
                file.put("name", filename);
                file.put("ts", sdf.format(externalResource.getUploadedWhen()));
                file.put("id", externalResource.getResourceId());

                JSONArray files = new JSONArray();
                files.put(file);

                JSONObject responseString = new JSONObject();
                responseString.put("files", files);

                IResource jsonResource = new ByteArrayResource("text/plain", responseString.toString().getBytes());
                IRequestHandler requestHandler = new ResourceRequestHandler(jsonResource, null);
                requestHandler.respond(getRequestCycle());
            }
        }

        @Override
        public void renderHead(Component component, IHeaderResponse response)
        {
            super.renderHead(component, response);
            String componentMarkupId = component.getMarkupId();
            String callbackUrl = getCallbackUrl().toString();

            response.render(JavaScriptHeaderItem.forScript("var componentMarkupId3='" + componentMarkupId + "'; var callbackUrl3='" + callbackUrl + "';", "imageUpload"));
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
    private Volume selectedVolume;
    private final Project project;
    private final Form subProjectForm;
    private final Set<Integer> deletedVolumeIds = new HashSet<Integer>();
    private static final long serialVersionUID = 1L;
}
