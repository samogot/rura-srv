package ru.ruranobe.wicket.webpages;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.*;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.tables.*;
import ru.ruranobe.wicket.RuraConstants;
import ru.ruranobe.wicket.components.sidebar.FriendsSidebarModule;
import ru.ruranobe.wicket.components.sidebar.ProjectsSidebarModule;
import ru.ruranobe.wicket.webpages.base.AdminLayoutPage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class VolumeEdit extends AdminLayoutPage
{

    public VolumeEdit(final PageParameters parameters)
    {
        String projectName = parameters.get("project").toOptionalString();
        String volumeName = parameters.get("volume").toOptionalString();
        if (Strings.isEmpty(projectName) || Strings.isEmpty(volumeName))
        {
            throw RuranobeUtils.REDIRECT_TO_404;
        }

        String volumeUrl = projectName + "/" + volumeName;
        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        Volume volume = null;
        List<Project> projects = null;
        try
        {
            VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
            volume = volumesMapperCacheable.getVolumeByUrl(volumeUrl);

            if (volume == null)
            {
                throw RuranobeUtils.REDIRECT_TO_404;
            }

            ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            List<Chapter> chapters = chaptersMapperCacheable.getChaptersByVolumeId(volume.getVolumeId());

            ChapterImagesMapper chapterImagesMapperCacheable = CachingFacade.getCacheableMapper(session, ChapterImagesMapper.class);
            List<ChapterImage> chapterImages = chapterImagesMapperCacheable.getChapterImagesByVolumeId(volume.getVolumeId());

            ProjectsMapper projectsMapperCacheable = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
            projects = Lists.newArrayList(projectsMapperCacheable.getAllProjects());
        }
        finally
        {
            session.close();
        }

        add(new VolumeForm(volume, projects));
        /*sidebarModules.add(new ProjectsSidebarModule("sidebarModule"));
        sidebarModules.add(new FriendsSidebarModule("sidebarModule"));*/
    }

    private static class VolumeForm extends Form<Volume>
    {
        public VolumeForm(final Volume volumeVar, final List<Project> projects)
        {
            super("volumeForm", new CompoundPropertyModel(volumeVar));

            volume = volumeVar;

            for (Project project : projects)
            {
                projectNameToProjectId.put(project.getTitle(), project.getProjectId());
            }
            projectIdToProjectName = projectNameToProjectId.inverse();

            TextField<String> url = new TextField<String>("url");
            this.add(url);
            TextField<String> nameFile = new TextField<String>("nameFile");
            this.add(nameFile);
            TextField<String> nameTitle = new TextField<String>("nameTitle");
            this.add(nameTitle);
            TextField<String> nameJp = new TextField<String>("nameJp");
            this.add(nameJp);
            TextField<String> nameEn = new TextField<String>("nameEn");
            this.add(nameEn);
            TextField<String> nameRu = new TextField<String>("nameRu");
            this.add(nameRu);
            TextField<String> nameRomaji = new TextField<String>("nameRomaji");
            this.add(nameRomaji);
            TextField<String> nameShort = new TextField<String>("nameShort");
            this.add(nameShort);
            DropDownChoice<String> projectId = new DropDownChoice<String>("projectId", new Model<String>()
            {
                @Override
                public String getObject()
                {
                    return projectIdToProjectName.get(volume.getProjectId());
                }

                @Override
                public void setObject(final String value)
                {
                    volume.setProjectId(projectNameToProjectId.get(value));
                }

            }, Lists.newArrayList(projectNameToProjectId.keySet()));
            this.add(projectId);
            TextField<String> sequenceNumber = new TextField<String>("sequenceNumber", new Model<String>()
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
            this.add(sequenceNumber);
            TextField<String> author = new TextField<String>("author");
            this.add(author);
            TextField<String> illustrator = new TextField<String>("illustrator");
            this.add(illustrator);
            TextField<String> releaseDate = new TextField<String>("releaseDate", new Model<String>()
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
            this.add(releaseDate);
            TextField<String> isbn = new TextField<String>("isbn");
            this.add(isbn);
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
            this.add(volumeType);
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
                    volume.setVolumeType(RuraConstants.VOLUME_STATUS_FULL_TEXT_TO_STATUS.get(value));
                }

            }, Lists.newArrayList(RuraConstants.VOLUME_STATUS_FULL_TEXT_TO_STATUS.keySet()));
            this.add(volumeStatus);
            TextField<String> externalUrl = new TextField<String>("externalUrl");
            this.add(externalUrl);
            TextArea<String> annotation = new TextArea<String>("annotation");
            this.add(annotation);
            CheckBox adult = new CheckBox("adult");
            this.add(adult);

            AjaxButton updateVolumeAjax = new AjaxButton("updateVolumeAjax", this)
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    SqlSession session = MybatisUtil.getSessionFactory().openSession();
                    try
                    {
                        VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
                        volumesMapperCacheable.updateVolume(volume);
                        session.commit();
                    }
                    finally
                    {
                        session.close();
                    }
                }
            };
            this.add(updateVolumeAjax);
        }


        private final BiMap<String, Integer> projectNameToProjectId = HashBiMap.create();
        private final Map<Integer, String> projectIdToProjectName;
        private final Volume volume;
    }

}
