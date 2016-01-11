package ru.ruranobe.wicket.components.admin;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.handler.resource.ResourceRequestHandler;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.util.string.Strings;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.ruranobe.config.ApplicationContext;
import ru.ruranobe.engine.Webpage;
import ru.ruranobe.engine.image.ImageServices;
import ru.ruranobe.engine.image.RuraImage;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.mybatis.entities.tables.ExternalResource;
import ru.ruranobe.mybatis.entities.tables.ExternalResourceHistory;
import ru.ruranobe.mybatis.entities.tables.Project;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

/**
 * Created by samogot on 29.08.15.
 */
public class BannerUploadComponent extends ImageUploaderComponent
{

    public Project getProject()
    {
        return project;
    }

    public BannerUploadComponent setProject(Project project)
    {
        this.project = project;
        if (project != null)
        {
            if (getContextVariables() == null)
            {
                setContextVariables(new HashMap<String, String>());
            }
            getContextVariables().put("project", project.getUrl());
        }
        return this;
    }

    @Override
    public boolean isVisible()
    {
        return project != null && !Strings.isEmpty(project.getUrl());
    }

    @Override
    protected void onInitialize()
    {
        super.onInitialize();
        getImage().add(new AttributeAppender("class", " banner-upload"))
                  .add(new AttributeModifier("height", "73"))
                  .add(new AttributeModifier("width", "220"));
    }

    @Override
    protected void processUpload(HttpServletRequest request)
    {
        super.processUpload(request);
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

            ExternalResourceHistory externalResourceHistory = new ExternalResourceHistory();
            externalResourceHistory.setProjectId(project.getProjectId());

            ApplicationContext context = RuranobeUtils.getApplicationContext();
            Webpage webpage = context.getWebpageByPageClass(this.getPage().getClass().getName());
            RuraImage image = new RuraImage(imageTempFile, uploadingFileExtension, filename);
            List<ExternalResource> externalResources = ImageServices.uploadImage(image, webpage.getImageStorages(),
                    externalResourceHistory, getContextVariables());
            ExternalResource externalResource = externalResources.iterator().next();
            setDefaultModelObject(externalResource);
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

    public BannerUploadComponent(String id)
    {
        super(id);
    }

    public BannerUploadComponent(String id, IModel<Project> model)
    {
        super(id, model);
    }

    private Project project;

}
