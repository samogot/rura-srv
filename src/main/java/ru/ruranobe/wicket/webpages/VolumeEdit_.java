package ru.ruranobe.wicket.webpages;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.handler.resource.ResourceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.util.string.Strings;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.*;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.tables.*;
import ru.ruranobe.wicket.RuraConstants;
import ru.ruranobe.wicket.webpages.base.AdminLayoutPage;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class VolumeEdit_ extends AdminLayoutPage
{

    public VolumeEdit_(final PageParameters parameters)
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
        List<VolumeReleaseActivity> volumeReleaseActivities = null;
        List<VolumeActivity> activities = null;
        List<TeamMember> teamMembers = null;
        List<Chapter> chapters = null;
        List<Update> updates = null;
        List<ChapterImage> volumeImages = null;
        try
        {
            VolumesMapper volumesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumesMapper.class);
            volume = volumesMapperCacheable.getVolumeByUrl(volumeUrl);

            if (volume == null)
            {
                throw RuranobeUtils.REDIRECT_TO_404;
            }

            VolumeReleaseActivitiesMapper volumeReleaseActivitiesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumeReleaseActivitiesMapper.class);
            volumeReleaseActivities = Lists.newArrayList(volumeReleaseActivitiesMapperCacheable.getVolumeReleaseActivitiesByVolumeId(volume.getVolumeId()));

            VolumeActivitiesMapper activitiesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumeActivitiesMapper.class);
            activities = activitiesMapperCacheable.getAllVolumeActivities();

            TeamMembersMapper teamMembersMapperCacheable = CachingFacade.getCacheableMapper(session, TeamMembersMapper.class);
            teamMembers = teamMembersMapperCacheable.getAllTeamMembers();

            ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
            chapters = chaptersMapperCacheable.getChaptersByVolumeId(volume.getVolumeId());

            UpdatesMapper updatesMapperCacheable = CachingFacade.getCacheableMapper(session, UpdatesMapper.class);
            updates = updatesMapperCacheable.getUpdatesByVolumeId(volume.getVolumeId());

            ProjectsMapper projectsMapperCacheable = CachingFacade.getCacheableMapper(session, ProjectsMapper.class);
            projects = Lists.newArrayList(projectsMapperCacheable.getAllProjects());

            ChapterImagesMapper chapterImagesMapperCacheable = CachingFacade.getCacheableMapper(session, ChapterImagesMapper.class);
            volumeImages = chapterImagesMapperCacheable.getChapterImagesByVolumeId(volume.getVolumeId());
        }
        finally
        {
            session.close();
        }

        add(new VolumeForm(volume, projects));
        add(new VolumeReleaseActivityForm(volumeReleaseActivities, volume, activities, teamMembers));
        add(new ChaptersForm(chapters, volume));
        add(new UpdatesForm(updates, chapters, volume));
        ImagesForm imagesForm = new ImagesForm(volumeImages, volume, chapters);
        imagesForm.setOutputMarkupId(true);
        add(imagesForm);
        add(new ImageUploadAjaxBehavior(imagesForm));
        /*sidebarModules.add(new ProjectsSidebarModule("sidebarModule"));
        sidebarModules.add(new FriendsSidebarModule("sidebarModule"));*/
    }

    private class ImageUploadAjaxBehavior extends AbstractDefaultAjaxBehavior
    {
        public ImageUploadAjaxBehavior(ImagesForm imagesForm)
        {
            super();
            this.imagesForm = imagesForm;
        }

        @Override
        protected void respond(final AjaxRequestTarget target)
        {
            HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
            if (ServletFileUpload.isMultipartContent(request))
            {
                /* Upload somewhere... */
                /*File beach = new File("");
                FileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                try
                {
                    String filename = null;
                    List<FileItem> items = upload.parseRequest(request);
                    for (FileItem item : items)
                    {
                        filename = filename == null ? item.getName() : null;
                        item.write(beach);
                    }
                }
                catch (Exception ex)
                {

                }*/
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss");

            JSONObject file = new JSONObject();
            file.put("url", "sdvdsfvds");
            file.put("name", "dhfndgfmhj");
            file.put("ts", sdf.format(new Date()));
            file.put("id", new Integer(123));

            JSONArray files = new JSONArray();
            files.put(file);

            JSONObject responseString = new JSONObject();
            responseString.put("files", files);

            IResource jsonResource = new ByteArrayResource("text/plain", responseString.toString().getBytes());
            IRequestHandler requestHandler = new ResourceRequestHandler(jsonResource, null);
            requestHandler.respond(getRequestCycle());

            //target.add(imagesForm);
            //imagesForm.addImage("", target);
        }

        @Override
        public void renderHead(Component component, IHeaderResponse response)
        {
            super.renderHead(component, response);
            String callbackUrl = getCallbackUrl().toString();
            response.render(JavaScriptHeaderItem.forScript("var addImageCallbackUrl='" + callbackUrl + "';", "imageUpload"));
        }

        public final ImagesForm imagesForm;
    }


    private static class ImagesForm extends Form
    {
        public ImagesForm(List<ChapterImage> volumeImages, Volume volumeVar, final List<Chapter> chaptersVar)
        {
            super("imagesForm");

            images = volumeImages;
            volume = volumeVar;
            chapters = chaptersVar;

            /*for (Chapter chapter: chapters)
            {
                chapterNameToId.put(chapter.getTitle(), chapter.getChapterId());
            }
            chapterIdToName = chapterNameToId.inverse();*/

            /*AjaxButton addImage = new AjaxButton("addImage", this)
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    ChapterImage image = new ChapterImage();
                    image.setVolumeId(volume.getVolumeId());
                    image.setAdult(false);
                    if (chapters.size() > 0)
                    {
                        image.setChapterId(chapters.get(0).getChapterId());
                    }
                    images.add(0, image);
                    target.add(form);
                    target.appendJavaScript("$('.list-group').listgroup();");
                }
            };
            this.add(addImage);*/

            final ListView<Chapter> imageChapterRepeater = new ListView<Chapter>("imageChapterRepeater", chapters)
            {
                @Override
                protected void populateItem(ListItem<Chapter> item)
                {
                    int orderNumber = item.getIndex();
                    Chapter chapter = item.getModelObject();

                    WebMarkupContainer imageDataChapterId = new WebMarkupContainer("imageDataChapterId");
                    AttributeAppender dataChapterId = new AttributeAppender("data-chapter-id", chapter.getChapterId());
                    imageDataChapterId.add(dataChapterId);
                    AttributeAppender value = new AttributeAppender("value", orderNumber);
                    imageDataChapterId.add(value);
                    item.add(imageDataChapterId);

                    Label imageChapterName = new Label("imageChapterName", chapter.getTitle());
                    imageDataChapterId.add(imageChapterName);

                    List<ChapterImage> chapterImages = new ArrayList<ChapterImage>();
                    for (ChapterImage image : images)
                    {
                        if (image.getChapterId().equals(chapter.getChapterId()))
                        {
                            chapterImages.add(image);
                        }
                    }

                    final ListView<ChapterImage> imageInfoRepeater = new ListView<ChapterImage>("imageInfoRepeater", chapterImages)
                    {
                        @Override
                        protected void populateItem(ListItem<ChapterImage> item)
                        {
                            int orderNumber = item.getIndex();
                            String generatedImageId = "image" + Integer.toString(orderNumber);

                            WebMarkupContainer visibilityController = new WebMarkupContainer("imageVisibilityController");
                            AttributeAppender addHref = new AttributeAppender("href", "#" + generatedImageId);
                            visibilityController.add(addHref);
                            AttributeAppender addAriaControls = new AttributeAppender("aria-controls", generatedImageId);
                            visibilityController.add(addAriaControls);

                            ChapterImage image = item.getModelObject();

                            ExternalResource nonColoredImage = image.getNonColoredImage();
                            ExternalResource coloredImage = image.getColoredImage();
                            String url = "";
                            Date date = null;
                            if (nonColoredImage != null)
                            {
                                url = nonColoredImage.getUrl();
                                date = nonColoredImage.getUploadedWhen();
                            }
                            if (coloredImage != null)
                            {
                                if (Strings.isEmpty(url))
                                {
                                    url = coloredImage.getUrl();
                                }
                                if (date == null)
                                {
                                    date = coloredImage.getUploadedWhen();
                                }
                            }
                            if (date == null)
                            {
                                date = new Date();
                            }

                            WebMarkupContainer imageUrl = new WebMarkupContainer("imageUrl");
                            AttributeAppender addSrc = new AttributeAppender("src", url);
                            imageUrl.add(addSrc);
                            visibilityController.add(imageUrl);

                            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY");
                            Label imageUploadDate = new Label("imageUploadDate", sdf.format(date));
                            visibilityController.add(imageUploadDate);

                            item.add(visibilityController);
                        }
                    };
                    item.add(imageInfoRepeater);
                }
            };
            this.add(imageChapterRepeater);

            final ListView<ChapterImage> imageRepeater = new ListView<ChapterImage>("imageRepeater", images)
            {
                @Override
                protected void populateItem(ListItem<ChapterImage> item)
                {
                    int orderNumber = item.getIndex();
                    String generatedImageId = "image" + Integer.toString(orderNumber);

                    WebMarkupContainer imageDiv = new WebMarkupContainer("imageDiv");
                    imageDiv.setOutputMarkupId(true);
                    imageDiv.setMarkupId(generatedImageId);

                    final ChapterImage image = item.getModelObject();

                    HiddenField<Integer> imageId = new HiddenField<Integer>("imageId", new Model<Integer>()
                    {
                        @Override
                        public Integer getObject()
                        {
                            return image.getChapterImageId();
                        }
                    });
                    imageId.setOutputMarkupId(true);
                    imageId.setMarkupId(generatedImageId + "_id");
                    imageDiv.add(imageId);

                    HiddenField<String> imageOrder = new HiddenField<String>("imageOrder", new Model<String>()
                    {

                        @Override
                        public void setObject(String orderNumber)
                        {
                            image.setOrderNumber(Integer.parseInt(orderNumber));
                        }
                    });
                    imageOrder.setOutputMarkupId(true);
                    imageOrder.setMarkupId(generatedImageId + "_order");
                    imageDiv.add(imageOrder);

                    HiddenField<String> imageChapterId = new HiddenField<String>("imageChapterId", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return Integer.toString(image.getChapterId());
                        }

                        @Override
                        public void setObject(String chapterId)
                        {
                            image.setChapterId(Integer.parseInt(chapterId));
                        }
                    });
                    imageChapterId.setOutputMarkupId(true);
                    imageChapterId.setMarkupId(generatedImageId + "_chapter_id");
                    imageDiv.add(imageChapterId);

                    HiddenField<String> imageDeleted = new HiddenField<String>("imageDeleted", new Model<String>()
                    {

                        @Override
                        public void setObject(String object)
                        {
                            Boolean value = Boolean.valueOf(object);
                            if (value)
                            {
                                deletedImages.add(image.getChapterImageId());
                            }
                        }
                    });
                    imageDeleted.setOutputMarkupId(true);
                    imageDeleted.setMarkupId(generatedImageId + "_delete");
                    imageDiv.add(imageDeleted);

                    final ExternalResource nonColoredImage = image.getNonColoredImage();

                    WebMarkupContainer imageNonColoredUrl = new WebMarkupContainer("imageNonColoredUrl");
                    Label imageNonColoredUploadDate = new Label("imageNonColoredUploadDate", "");
                    Label imageNonColoredFileName = new Label("imageNonColoredFileName", "");
                    if (nonColoredImage == null)
                    {
                        imageNonColoredUrl.setVisible(false);
                        imageNonColoredUploadDate.setVisible(false);
                        imageNonColoredFileName.setVisible(false);
                    }
                    else
                    {
                        AttributeAppender addSrc = new AttributeAppender("src", nonColoredImage.getUrl());
                        imageNonColoredUrl.add(addSrc);

                        imageNonColoredUploadDate = new Label("imageNonColoredUploadDate",
                                new Model<String>()
                                {
                                    @Override
                                    public String getObject()
                                    {
                                        return sdf.format(nonColoredImage.getUploadedWhen());
                                    }

                                    private final SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.YYYY HH:mm:ss");
                                }
                        );

                        imageNonColoredFileName = new Label("imageNonColoredFileName", Model.of(nonColoredImage.getTitle()));
                    }
                    imageDiv.add(imageNonColoredUrl);
                    imageDiv.add(imageNonColoredUploadDate);
                    imageDiv.add(imageNonColoredFileName);

                    final ExternalResource coloredImage = image.getColoredImage();

                    WebMarkupContainer imageColoredUrl = new WebMarkupContainer("imageColoredUrl");
                    Label imageColoredUploadDate = new Label("imageColoredUploadDate", "");
                    Label imageColoredFileName = new Label("imageColoredFileName", "");
                    if (coloredImage == null)
                    {
                        imageColoredUrl.setVisible(false);
                        imageColoredUploadDate.setVisible(false);
                        imageColoredFileName.setVisible(false);
                    }
                    else
                    {
                        AttributeAppender addSrc = new AttributeAppender("src", coloredImage.getUrl());
                        imageColoredUrl.add(addSrc);

                        imageColoredUploadDate = new Label("imageColoredUploadDate",
                                new Model<String>()
                                {
                                    @Override
                                    public String getObject()
                                    {
                                        return sdf.format(coloredImage.getUploadedWhen());
                                    }

                                    private final SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.YYYY HH:mm:ss");
                                }
                        );

                        imageColoredFileName = new Label("imageColoredFileName", Model.of(coloredImage.getTitle()));
                    }
                    imageDiv.add(imageColoredUrl);
                    imageDiv.add(imageColoredUploadDate);
                    imageDiv.add(imageColoredFileName);

                    item.add(imageDiv);
                }
            };
            this.add(imageRepeater);

            AjaxButton updateImagesAjax = new AjaxButton("updateImagesAjax", this)
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    SqlSession session = MybatisUtil.getSessionFactory().openSession();
                    try
                    {
                        ChapterImagesMapper chapterImagesMapperCacheable = CachingFacade.getCacheableMapper(session, ChapterImagesMapper.class);
                        String exceptionText = validateData();
                        if (!Strings.isEmpty(exceptionText))
                        {
                            target.appendJavaScript("alert('" + exceptionText + "')");
                        }
                        else
                        {
                            for (ChapterImage image : images)
                            {
                                if (image.getChapterImageId() != null)
                                {
                                    // update
                                    chapterImagesMapperCacheable.updateChapterImage(image);
                                }
                                else
                                {
                                    // insert
                                    chapterImagesMapperCacheable.insertChapterImage(image);
                                }
                            }

                            for (Integer imageId : deletedImages)
                            {
                                // delete
                                chapterImagesMapperCacheable.deleteChapterImage(imageId);
                            }

                            session.commit();
                        }
                    }
                    finally
                    {
                        session.close();
                    }

                    deletedImages.clear();
                }
            };
            this.add(updateImagesAjax);
        }

        private String validateData()
        {
            String exceptionText = null;
            for (ChapterImage image : images)
            {
                if (image.getNonColoredImage() == null && image.getColoredImage() == null)
                {
                    exceptionText = "Для одной из картинок не указан ни покрас ни база";
                }
            }
            return exceptionText;
        }

        public void addImage(String url, final AjaxRequestTarget target)
        {
            ChapterImage image = new ChapterImage();
            image.setVolumeId(volume.getVolumeId());
            image.setAdult(false);
            if (chapters.size() > 0)
            {
                image.setChapterId(chapters.get(0).getChapterId());
            }
            images.add(0, image);
            target.add(this);
            target.appendJavaScript("$('.list-group').listgroup();");
        }

        /*private final BiMap <String, Integer> chapterNameToId = HashBiMap.create();
        private final Map <Integer, String> chapterIdToName;*/
        private final List<Chapter> chapters;
        private final List<ChapterImage> images;
        private final Set<Integer> deletedImages = new HashSet<Integer>();
        private final Volume volume;
    }


    private static class UpdatesForm extends Form
    {
        public UpdatesForm(List<Update> updatesVar, List<Chapter> chapters, Volume volumeVar)
        {
            super("updatesForm");

            updates = updatesVar;
            volume = volumeVar;

            for (Chapter chapter : chapters)
            {
                chapterNameToId.put(chapter.getTitle(), chapter.getChapterId());
            }
            chapterIdToName = chapterNameToId.inverse();

            AjaxButton addUpdate = new AjaxButton("addUpdate", this)
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    Update update = new Update();
                    update.setVolumeId(volume.getVolumeId());
                    update.setProjectId(volume.getProjectId());
                    update.setShowTime(new Date(System.currentTimeMillis()));
                    updates.add(0, update);
                    target.add(form);
                    target.appendJavaScript("$('.list-group').listgroup();");
                }
            };
            this.add(addUpdate);

            final ListView<Update> updateNameRepeater = new ListView<Update>("updateNameRepeater", updates)
            {
                @Override
                protected void populateItem(ListItem<Update> item)
                {
                    int orderNumber = item.getIndex();
                    String generatedUpdateId = "update" + Integer.toString(orderNumber);
                    WebMarkupContainer visibilityController = new WebMarkupContainer("updateVisibilityController");
                    AttributeAppender addHref = new AttributeAppender("href", "#" + generatedUpdateId);
                    visibilityController.add(addHref);
                    AttributeAppender addAriaControls = new AttributeAppender("aria-controls", generatedUpdateId);
                    visibilityController.add(addAriaControls);
                    Update update = item.getModelObject();

                    Date date = update.getShowTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("YYYY.MM.dd HH:mm:ss");
                    Label updateDateInSelector = new Label("updateDateInSelector", sdf.format(date));
                    visibilityController.add(updateDateInSelector);

                    Integer chapterId = update.getChapterId();
                    String chapterName = chapterId == null ? "Весь том" : chapterIdToName.get(chapterId);
                    Label updateChapterInSelector = new Label("updateChapterInSelector", chapterName);
                    visibilityController.add(updateChapterInSelector);

                    item.add(visibilityController);
                }
            };
            this.add(updateNameRepeater);

            final ListView<Update> updateRepeater = new ListView<Update>("updateRepeater", updates)
            {
                @Override
                protected void populateItem(ListItem<Update> item)
                {
                    int orderNumber = item.getIndex();
                    String generatedUpdateId = "update" + Integer.toString(orderNumber);
                    WebMarkupContainer updateDiv = new WebMarkupContainer("updateDiv");
                    updateDiv.setOutputMarkupId(true);
                    updateDiv.setMarkupId(generatedUpdateId);
                    final Update update = item.getModelObject();

                    HiddenField<Integer> updateId = new HiddenField<Integer>("updateId", new Model<Integer>()
                    {
                        @Override
                        public Integer getObject()
                        {
                            return update.getUpdateId();
                        }
                    });
                    updateId.setOutputMarkupId(true);
                    updateId.setMarkupId(generatedUpdateId + "_id");
                    updateDiv.add(updateId);

                    HiddenField<String> updateDeleted = new HiddenField<String>("updateDeleted", new Model<String>()
                    {

                        @Override
                        public void setObject(String object)
                        {
                            Boolean value = Boolean.valueOf(object);
                            if (value)
                            {
                                deletedUpdates.add(update.getUpdateId());
                            }
                        }
                    });
                    updateDeleted.setOutputMarkupId(true);
                    updateDeleted.setMarkupId(generatedUpdateId + "_delete");
                    updateDiv.add(updateDeleted);

                    DropDownChoice<String> updateType = new DropDownChoice<String>("updateType", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return update.getUpdateType();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            update.setUpdateType(value);
                        }

                    }, Arrays.asList("Опубликован",
                            "Глобальная редактура",
                            "Обновление иллюстраций"));
                    updateDiv.add(updateType);

                    List<String> chapterNamesExtended = Lists.newArrayList(chapterNameToId.keySet());
                    chapterNamesExtended.add("Весь том");
                    DropDownChoice<String> updateChapter = new DropDownChoice<String>("updateChapter", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return chapterIdToName.get(update.getChapterId());
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            if ("Весь том".equalsIgnoreCase(value.trim()))
                            {
                                update.setChapterId(null);
                            }
                            else
                            {
                                update.setChapterId(chapterNameToId.get(value));
                            }
                        }

                    }, chapterNamesExtended);
                    updateDiv.add(updateChapter);

                    TextField<String> updateDate = new TextField<String>("updateDate", new Model<String>()
                    {
                        private final SimpleDateFormat sdf = new SimpleDateFormat("YYYY.MM.dd HH:mm:ss");

                        @Override
                        public String getObject()
                        {
                            return update.getShowTime() == null ? null : sdf.format(update.getShowTime());
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            try
                            {
                                update.setShowTime(sdf.parse(value));
                            }
                            catch (ParseException ex)
                            {
                                throw new RuntimeException(ex);
                            }
                        }
                    });
                    updateDiv.add(updateDate);

                    TextField<String> updateDescription = new TextField<String>("updateDescription", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return update.getDescription();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            update.setDescription(value);
                        }
                    });
                    updateDescription.setOutputMarkupId(true);
                    updateDescription.setMarkupId(generatedUpdateId + "_description");
                    updateDiv.add(updateDescription);

                    item.add(updateDiv);
                }
            };
            this.add(updateRepeater);

            AjaxButton updateUpdatesAjax = new AjaxButton("updateUpdatesAjax", this)
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    SqlSession session = MybatisUtil.getSessionFactory().openSession();
                    try
                    {
                        UpdatesMapper updatesMapperCacheable = CachingFacade.getCacheableMapper(session, UpdatesMapper.class);
                        String exceptionText = validateData();
                        if (!Strings.isEmpty(exceptionText))
                        {
                            target.appendJavaScript("alert('" + exceptionText + "')");
                        }
                        else
                        {
                            for (Update update : updates)
                            {
                                if (update.getUpdateId() != null)
                                {
                                    // update
                                    updatesMapperCacheable.updateUpdate(update);
                                }
                                else
                                {
                                    // insert
                                    updatesMapperCacheable.insertUpdate(update);
                                }
                            }

                            for (Integer updateId : deletedUpdates)
                            {
                                // delete
                                updatesMapperCacheable.deleteUpdate(updateId);
                            }

                            session.commit();
                        }
                    }
                    finally
                    {
                        session.close();
                    }

                    deletedUpdates.clear();
                }
            };
            this.add(updateUpdatesAjax);
        }

        private String validateData()
        {
            String exceptionText = null;
            for (Update update : updates)
            {
                if (update.getVolumeId() == null)
                {
                    exceptionText = "Для одного из обновлений не указан том";
                }
                else if (update.getShowTime() == null)
                {
                    exceptionText = "Для одного из обновлений не указана дата";
                }
                else if (Strings.isEmpty(update.getUpdateType()))
                {
                    exceptionText = "Для одного из обновлений не указан тип";
                }
            }
            return exceptionText;
        }

        private BiMap<String, Integer> chapterNameToId = HashBiMap.create();
        private Map<Integer, String> chapterIdToName;
        private final Set<Integer> deletedUpdates = new HashSet<Integer>();
        private final List<Update> updates;
        private final Volume volume;
    }


    private static class ChaptersForm extends Form
    {
        public ChaptersForm(List<Chapter> chaptersVar, Volume volumeVar)
        {
            super("chaptersForm");

            chapters = chaptersVar;
            volume = volumeVar;

            AjaxButton addChapter = new AjaxButton("addChapter", this)
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    Chapter chapter = new Chapter();
                    chapter.setVolumeId(volume.getVolumeId());
                    chapter.setTitle("<Пусто>");
                    chapters.add(0, chapter);
                    target.add(form);
                    target.appendJavaScript("$('.list-group').listgroup();");
                }
            };

            this.add(addChapter);

            final ListView<Chapter> chapterNameRepeater = new ListView<Chapter>("chapterNameRepeater", chapters)
            {
                @Override
                protected void populateItem(ListItem<Chapter> item)
                {
                    int orderNumber = item.getIndex();
                    String generatedChapterId = "chapter" + Integer.toString(orderNumber);
                    WebMarkupContainer visibilityController = new WebMarkupContainer("chapterVisibilityController");
                    AttributeAppender addHref = new AttributeAppender("href", "#" + generatedChapterId);
                    visibilityController.add(addHref);
                    AttributeAppender addAriaControls = new AttributeAppender("aria-controls", generatedChapterId);
                    visibilityController.add(addAriaControls);
                    Chapter chapter = item.getModelObject();
                    String name = chapter.getTitle();
                    Label chapterName = new Label("chapterName", name);
                    chapterName.setOutputMarkupId(true);
                    chapterName.setMarkupId(generatedChapterId + "_name");
                    visibilityController.add(chapterName);
                    item.add(visibilityController);
                }
            };
            this.add(chapterNameRepeater);

            final ListView<Chapter> chapterRepeater = new ListView<Chapter>("chapterRepeater", chapters)
            {
                @Override
                protected void populateItem(ListItem<Chapter> item)
                {
                    int orderNumber = item.getIndex();
                    String generatedChapterId = "chapter" + Integer.toString(orderNumber);
                    WebMarkupContainer chapterDiv = new WebMarkupContainer("chapterDiv");
                    chapterDiv.setOutputMarkupId(true);
                    chapterDiv.setMarkupId(generatedChapterId);
                    final Chapter chapter = item.getModelObject();

                    HiddenField<Integer> chapterId = new HiddenField<Integer>("chapterId", new Model<Integer>()
                    {
                        @Override
                        public Integer getObject()
                        {
                            return chapter.getChapterId();
                        }
                    });
                    chapterId.setOutputMarkupId(true);
                    chapterId.setMarkupId(generatedChapterId + "_id");
                    chapterDiv.add(chapterId);

                    HiddenField<String> chapterOrder = new HiddenField<String>("chapterOrder", new Model<String>()
                    {

                        @Override
                        public void setObject(String orderNumber)
                        {
                            chapter.setOrderNumber(Integer.parseInt(orderNumber));
                        }
                    });
                    chapterOrder.setOutputMarkupId(true);
                    chapterOrder.setMarkupId(generatedChapterId + "_order");
                    chapterDiv.add(chapterOrder);

                    HiddenField<String> chapterDeleted = new HiddenField<String>("chapterDeleted", new Model<String>()
                    {

                        @Override
                        public void setObject(String object)
                        {
                            Boolean value = Boolean.valueOf(object);
                            if (value)
                            {
                                deletedChapters.add(chapter.getChapterId());
                            }
                        }
                    });
                    chapterDeleted.setOutputMarkupId(true);
                    chapterDeleted.setMarkupId(generatedChapterId + "_delete");
                    chapterDiv.add(chapterDeleted);

                    CheckBox chapterNested = new CheckBox("chapterNested", new Model<Boolean>()
                    {
                        @Override
                        public Boolean getObject()
                        {
                            return chapter.isNested();
                        }

                        @Override
                        public void setObject(final Boolean value)
                        {
                            chapter.setNested(value);
                        }
                    });
                    chapterNested.setOutputMarkupId(true);
                    chapterNested.setMarkupId(generatedChapterId + "_is_subchapter");
                    chapterDiv.add(chapterNested);

                    TextField<String> chapterUrl = new TextField<String>("chapterUrl", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return chapter.getUrl();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            chapter.setUrl(value);
                        }
                    });
                    chapterUrl.setOutputMarkupId(true);
                    chapterUrl.setMarkupId(generatedChapterId + "_url_input");
                    chapterDiv.add(chapterUrl);

                    TextField<String> chapterTitle = new TextField<String>("chapterTitle", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return chapter.getTitle();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            chapter.setTitle(value);
                        }
                    });
                    chapterTitle.setOutputMarkupId(true);
                    chapterTitle.setMarkupId(generatedChapterId + "_name_input");
                    chapterDiv.add(chapterTitle);

                    item.add(chapterDiv);
                }
            };
            this.add(chapterRepeater);

            AjaxButton updateChaptersAjax = new AjaxButton("updateChaptersAjax", this)
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    SqlSession session = MybatisUtil.getSessionFactory().openSession();
                    try
                    {
                        ChaptersMapper chaptersMapperCacheable = CachingFacade.getCacheableMapper(session, ChaptersMapper.class);
                        String exceptionText = validateData();
                        if (!Strings.isEmpty(exceptionText))
                        {
                            target.appendJavaScript("alert('" + exceptionText + "')");
                        }
                        else
                        {
                            for (Chapter chapter : chapters)
                            {
                                if (chapter.getChapterId() != null)
                                {
                                    // update
                                    chaptersMapperCacheable.updateChapter(chapter);
                                }
                                else
                                {
                                    // insert
                                    chaptersMapperCacheable.insertChapter(chapter);
                                }
                            }

                            for (Integer chapterId : deletedChapters)
                            {
                                // delete
                                chaptersMapperCacheable.deleteChapter(chapterId);
                            }

                            session.commit();
                        }
                    }
                    finally
                    {
                        session.close();
                    }

                    deletedChapters.clear();
                }
            };
            this.add(updateChaptersAjax);
        }

        private String validateData()
        {
            String exceptionText = null;
            for (Chapter chapter : chapters)
            {
                if (Strings.isEmpty(chapter.getUrl()))
                {
                    exceptionText = "Для одной из глав не указана ссылка";
                }
                else
                {
                    String[] chapterUrlParts = chapter.getUrl().split("/");
                    if (chapterUrlParts.length != 3)
                    {
                        exceptionText = "Для одной из глав не верно указана ссылка. Убедитесь, что формат следующий {projectUrl}/{volumeUrl}/{chapterUrl}, причем projectUrl, volumeUrl, chapterUrl не могут быть пустыми.";
                        return exceptionText;
                    }
                    for (String chapterUrlPart : chapterUrlParts)
                    {
                        if (Strings.isEmpty(chapterUrlPart))
                        {
                            exceptionText = "Для одной из глав не верно указана ссылка. Убедитесь, что формат следующий {projectUrl}/{volumeUrl}/{chapterUrl}, причем projectUrl, volumeUrl, chapterUrl не могут быть пустыми.";
                            return exceptionText;
                        }
                    }
                }

                if (Strings.isEmpty(chapter.getTitle()))
                {
                    exceptionText = "Для одной из глав не верно указано название.";
                    return exceptionText;
                }
            }
            return exceptionText;
        }

        private final Set<Integer> deletedChapters = new HashSet<Integer>();
        private final List<Chapter> chapters;
        private final Volume volume;
    }

    private static class VolumeReleaseActivityForm extends Form
    {
        public VolumeReleaseActivityForm(List<VolumeReleaseActivity> volumeReleaseActivitiesVar, Volume volumeVar, List<VolumeActivity> volumeActivities, List<TeamMember> teamMembers)
        {
            super("volumeReleaseActivityForm");

            volumeReleaseActivities = volumeReleaseActivitiesVar;
            volume = volumeVar;

            for (VolumeActivity volumeActivity : volumeActivities)
            {
                volumeActivityNameToId.put(volumeActivity.getActivityName(), volumeActivity.getActivityId());
            }
            //volumeActivityIdToName = volumeActivityNameToId.inverse();

            for (TeamMember teamMember : teamMembers)
            {
                teamMemberNameToId.put(teamMember.getNikname(), teamMember.getMemberId());
            }
            //teamMemberIdToName = teamMemberNameToId.inverse();

            AjaxButton addVolumeReleaseActivity = new AjaxButton("addVolumeReleaseActivity", this)
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    VolumeReleaseActivity volumeReleaseActivity = new VolumeReleaseActivity();
                    volumeReleaseActivity.setVolumeId(volume.getVolumeId());
                    volumeReleaseActivities.add(0, volumeReleaseActivity);
                    target.add(form);
                    target.appendJavaScript("$('.list-group').listgroup();");
                }
            };

            this.add(addVolumeReleaseActivity);

            final ListView<VolumeReleaseActivity> volumeReleaseActivityNameRepeater = new ListView<VolumeReleaseActivity>("volumeReleaseActivityNameRepeater", volumeReleaseActivities)
            {
                @Override
                protected void populateItem(ListItem<VolumeReleaseActivity> item)
                {
                    int orderNumber = item.getIndex();
                    String generatedParticipantId = "participant" + Integer.toString(orderNumber);
                    WebMarkupContainer visibilityController = new WebMarkupContainer("volumeReleaseActivityVisibilityController");
                    AttributeAppender addHref = new AttributeAppender("href", "#" + generatedParticipantId);
                    visibilityController.add(addHref);
                    AttributeAppender addAriaControls = new AttributeAppender("aria-controls", generatedParticipantId);
                    visibilityController.add(addAriaControls);
                    VolumeReleaseActivity volumeReleaseActivity = item.getModelObject();
                    String memberName = volumeReleaseActivity.getMemberName();
                    Label volumeReleaseActivityMemberName = new Label("volumeReleaseActivityMemberName", memberName);
                    volumeReleaseActivityMemberName.setOutputMarkupId(true);
                    volumeReleaseActivityMemberName.setMarkupId(generatedParticipantId + "_name");
                    visibilityController.add(volumeReleaseActivityMemberName);
                    String activityName = volumeReleaseActivity.getActivityName();
                    Label volumeReleaseActivityName = new Label("volumeReleaseActivityName", activityName);
                    volumeReleaseActivityName.setOutputMarkupId(true);
                    volumeReleaseActivityName.setMarkupId(generatedParticipantId + "_role");
                    visibilityController.add(volumeReleaseActivityName);
                    item.add(visibilityController);
                }
            };
            this.add(volumeReleaseActivityNameRepeater);

            final ListView<VolumeReleaseActivity> volumeReleaseActivityRepeater = new ListView<VolumeReleaseActivity>("volumeReleaseActivityRepeater", volumeReleaseActivities)
            {
                @Override
                protected void populateItem(ListItem<VolumeReleaseActivity> item)
                {
                    int orderNumber = item.getIndex();
                    String generatedParticipantId = "participant" + Integer.toString(orderNumber);
                    WebMarkupContainer volumeReleaseActivityDiv = new WebMarkupContainer("volumeReleaseActivityDiv");
                    volumeReleaseActivityDiv.setOutputMarkupId(true);
                    volumeReleaseActivityDiv.setMarkupId(generatedParticipantId);
                    final VolumeReleaseActivity volumeReleaseActivity = item.getModelObject();

                    HiddenField<Integer> volumeReleaseActivityId = new HiddenField<Integer>("volumeReleaseActivityId", new Model<Integer>()
                    {
                        @Override
                        public Integer getObject()
                        {
                            return volumeReleaseActivity.getReleaseActivityId();
                        }
                    });
                    volumeReleaseActivityId.setOutputMarkupId(true);
                    volumeReleaseActivityId.setMarkupId(generatedParticipantId + "_id");
                    volumeReleaseActivityDiv.add(volumeReleaseActivityId);

                    HiddenField<String> volumeReleaseActivityDeleted = new HiddenField<String>("volumeReleaseActivityDeleted", new Model<String>()
                    {

                        @Override
                        public void setObject(String object)
                        {
                            Boolean value = Boolean.valueOf(object);
                            if (value)
                            {
                                deletedVolumeReleaseActivities.add(volumeReleaseActivity.getReleaseActivityId());
                            }
                        }
                    });
                    volumeReleaseActivityDeleted.setOutputMarkupId(true);
                    volumeReleaseActivityDeleted.setMarkupId(generatedParticipantId + "_delete");
                    volumeReleaseActivityDiv.add(volumeReleaseActivityDeleted);

                    WebMarkupContainer volumeReleaseActivityMemberTitleDiv = new WebMarkupContainer("volumeReleaseActivityMemberTitleDiv");
                    volumeReleaseActivityMemberTitleDiv.setOutputMarkupId(true);
                    volumeReleaseActivityMemberTitleDiv.setMarkupId(generatedParticipantId + "_names_selector");
                    volumeReleaseActivityDiv.add(volumeReleaseActivityMemberTitleDiv);

                    TextField<String> volumeReleaseActivityMemberTitle = new TextField<String>("volumeReleaseActivityMemberTitle", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return volumeReleaseActivity.getMemberName();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            volumeReleaseActivity.setMemberId(volumeActivityNameToId.get(value));
                            volumeReleaseActivity.setMemberName(value);
                        }
                    });
                    volumeReleaseActivityMemberTitle.setOutputMarkupId(true);
                    volumeReleaseActivityMemberTitle.setMarkupId(generatedParticipantId + "_name_input");
                    volumeReleaseActivityMemberTitleDiv.add(volumeReleaseActivityMemberTitle);

                    DropDownChoice<String> volumeReleaseActivityActivityName = new DropDownChoice<String>("volumeReleaseActivityActivityName", new Model<String>()
                    {
                        @Override
                        public String getObject()
                        {
                            return volumeReleaseActivity.getActivityName();
                        }

                        @Override
                        public void setObject(final String value)
                        {
                            volumeReleaseActivity.setActivityName(value);
                            volumeReleaseActivity.setActivityId(volumeActivityNameToId.get(value));
                        }

                    }, Lists.newArrayList(volumeActivityNameToId.keySet()));
                    volumeReleaseActivityActivityName.setOutputMarkupId(true);
                    volumeReleaseActivityActivityName.setMarkupId(generatedParticipantId + "_roles_selector");
                    volumeReleaseActivityDiv.add(volumeReleaseActivityActivityName);

                    CheckBox volumeReleaseActivityTeamHidden = new CheckBox("volumeReleaseActivityTeamHidden", new Model<Boolean>()
                    {
                        @Override
                        public Boolean getObject()
                        {
                            return !volumeReleaseActivity.isTeamHidden();
                        }

                        @Override
                        public void setObject(final Boolean value)
                        {
                            volumeReleaseActivity.setTeamHidden(!value);
                        }
                    });
                    volumeReleaseActivityTeamHidden.setOutputMarkupId(true);
                    volumeReleaseActivityTeamHidden.setMarkupId(generatedParticipantId + "_team_show");
                    volumeReleaseActivityDiv.add(volumeReleaseActivityTeamHidden);

                    item.add(volumeReleaseActivityDiv);
                }
            };

            this.add(volumeReleaseActivityRepeater);

            AjaxButton updateVolumeReleaseActivitiesAjax = new AjaxButton("updateVolumeReleaseActivitiesAjax", this)
            {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form)
                {
                    SqlSession session = MybatisUtil.getSessionFactory().openSession();
                    try
                    {
                        VolumeReleaseActivitiesMapper volumeReleaseActivitiesMapperCacheable = CachingFacade.getCacheableMapper(session, VolumeReleaseActivitiesMapper.class);
                        String exceptionText = validateData();
                        if (!Strings.isEmpty(exceptionText))
                        {
                            target.appendJavaScript("alert('" + exceptionText + "')");
                        }
                        else
                        {
                            for (VolumeReleaseActivity releaseActivity : volumeReleaseActivities)
                            {
                                if (releaseActivity.getActivityId() != null)
                                {
                                    // update
                                    volumeReleaseActivitiesMapperCacheable.updateVolumeReleaseActivity(releaseActivity);
                                }
                                else
                                {
                                    // insert
                                    volumeReleaseActivitiesMapperCacheable.insertVolumeReleaseActivity(releaseActivity);
                                }
                            }

                            for (Integer volumeReleaseActivityId : deletedVolumeReleaseActivities)
                            {
                                // delete
                                volumeReleaseActivitiesMapperCacheable.deleteVolumeReleaseActivity(volumeReleaseActivityId);
                            }

                            session.commit();
                        }
                    }
                    finally
                    {
                        session.close();
                    }

                    deletedVolumeReleaseActivities.clear();
                }
            };

            this.add(updateVolumeReleaseActivitiesAjax);
        }

        private String validateData()
        {
            String exceptionText = null;
            for (VolumeReleaseActivity releaseActivity : volumeReleaseActivities)
            {
                if (releaseActivity.getActivityId() == null)
                {
                    exceptionText = "Для одного пункта из \"Этапы\\Стаф\" не указан вид деятельности";
                }
                else if (releaseActivity.getMemberId() == null)
                {
                    exceptionText = "Для одного пункта из \"Этапы\\Стаф\" не указан никнейм члена команды";
                }
            }
            return exceptionText;
        }

        private final Volume volume;
        private final BiMap<String, Integer> volumeActivityNameToId = HashBiMap.create();
        //private final Map <Integer,String> volumeActivityIdToName;
        private final BiMap<String, Integer> teamMemberNameToId = HashBiMap.create();
        //private final Map <Integer,String> teamMemberIdToName;
        private final Set<Integer> deletedVolumeReleaseActivities = new HashSet<Integer>();
        private final List<VolumeReleaseActivity> volumeReleaseActivities;
    }

    private static class VolumeForm extends Form<Volume>
    {
        public VolumeForm(final Volume volumeVar, final List<Project> projects)
        {
            super("volumeForm", new CompoundPropertyModel<Volume>(volumeVar));

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