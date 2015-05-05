package ru.ruranobe.wicket.webpages;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Bytes;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class UploadImage extends WebPage
{

    public UploadImage(final PageParameters parameters)
    {
        add(new FeedbackPanel("feedback"));
        add(new UploadImageForm("form"));
    }

    public class UploadImageForm extends StatelessForm<UploadImage>
    {
        private final FileUploadField fileUploadField;
        private TextField<String> picasaPathField;
        private TextField<String> titleField;
        public UploadImageForm(final String id)
        {
            super(id);

            setMultiPart(true);
            setMaxSize(Bytes.megabytes(10));

            picasaPathField = new TextField<String>("picasaPath", Model.of(""));
            titleField = new TextField<String>("title", Model.of(""));
            fileUploadField = new FileUploadField("fileUpload");

            add(picasaPathField);
            add(titleField);
            add(fileUploadField);
        }

        @Override
        protected void onSubmit()
        {
            final FileUpload uploadedFile = fileUploadField.getFileUpload();
            if (uploadedFile != null)
            {
                String picasaPath = picasaPathField.getDefaultModelObjectAsString();
                String title = titleField.getDefaultModelObjectAsString();
                String mimeType = uploadedFile.getContentType();
                //      RuranobeImageUploader imageUploader = RuranobeImageUploader.getInstance();

                File file = null;
                try
                {
                    file = uploadedFile.writeToTempFile();
                } catch (IOException ex)
                {
                    throw new RuntimeException(ex);
                }

                //String externalLink = imageUploader.uploadImage(file, mimeType, picasaPath, title);

                SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
                SqlSession session = sessionFactory.openSession();
                try
                {
                    ExternalResourcesMapper externalResourcesMapper = session.getMapper(ExternalResourcesMapper.class);
                    Date uploadedWhen = new Date(System.currentTimeMillis());
                    //TODO: replace 0
                    //       ExternalResource externalResource = new ExternalResource(0, mimeType, externalLink, title, uploadedWhen);
//                    externalResourcesMapper.insertExternalResource(externalResource);
                    session.commit();
                } finally
                {
                    session.close();
                }

                info("Файл с именем: " + uploadedFile.getClientFileName() + " был успешно загружен.");
            }
        }
    }
}