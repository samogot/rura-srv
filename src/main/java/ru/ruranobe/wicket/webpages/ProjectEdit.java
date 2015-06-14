package ru.ruranobe.wicket.webpages;

import com.google.common.collect.ImmutableMap;
import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ProjectsMapper;
import ru.ruranobe.mybatis.mappers.VolumesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.tables.Project;
import ru.ruranobe.mybatis.tables.Volume;
import ru.ruranobe.wicket.RuraConstants;
import ru.ruranobe.wicket.webpages.base.AdminLayoutPage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProjectEdit extends AdminLayoutPage
{

    public ProjectEdit(final PageParameters parameters)
    {
        final Project project = getProject(parameters);

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
                            return volume.getReleaseDate()==null ? null : sdf.format(volume.getReleaseDate());
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
                    }, Arrays.asList("Ранобэ","Побочные истории","Авторские додзинси","Другое"));
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
                    }, Arrays.asList("Заброшенный сторонний перевод","Активный сторонний перевод","Завершенный сторонний перевод","Отсутствует анлейт","Заморожен","Приостановлен","Очередь перевода","Перевод в онгоинге","Перевод","Редактура","Не оформлен","Завершен"));
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

            volumesForm.add(addVolume);
            volumesForm.add(deleteVolume);
            volumesForm.add(cloneVolume);
            volumesForm.add(volumeRepeater);

            add(volumesForm);

            //List<Project> subProjects = CachingFacade.getCacheableMapper(session, ProjectsMapper.class).getSubProjectsByParentProjectId(project.getProjectId());
        }
        finally
        {
            session.close();
        }

        add(new SubVolumesEditAjaxBehavior());
        add(new SubProjectsEditAjaxBehavior());
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

    private class VolumesForm extends Form<List<Volume>>
    {
        private int maxSequenceNumber = 1;
        private List<Volume> volumes;

        public VolumesForm(List<Volume> volumes)
        {
            super("volumesForm");
            this.volumes = volumes;
            for(Volume volume : volumes)
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
            String componentMarkupId = component.getMarkupId();
            String callbackUrl = getCallbackUrl().toString();

            response.render(JavaScriptHeaderItem.forScript("var componentMarkupId1='" + componentMarkupId + "'; var callbackUrl1='" + callbackUrl + "';", "values"));
        }
    }

    private class SubProjectsEditAjaxBehavior extends AbstractDefaultAjaxBehavior
    {

        @Override
        protected void respond(final AjaxRequestTarget target)
        {

        }

        @Override
        public void renderHead(Component component, IHeaderResponse response)
        {
            String componentMarkupId = component.getMarkupId();
            String callbackUrl = getCallbackUrl().toString();

            response.render(JavaScriptHeaderItem.forScript("var componentMarkupId2='" + componentMarkupId + "'; var callbackUrl2='" + callbackUrl + "';", "values"));
        }
    }

    private final List<Volume> volumes;
    private final Map<Integer, Volume> volumeTableOrderNumberToVolume = new HashMap<Integer, Volume>();
    private Volume selectedVolume;
    private static final long serialVersionUID = 1L;
}
