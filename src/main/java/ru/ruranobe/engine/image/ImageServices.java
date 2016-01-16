package ru.ruranobe.engine.image;

import com.sun.nio.sctp.IllegalUnbindException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.ibatis.session.SqlSession;
import ru.ruranobe.engine.files.LocalStorageService;
import ru.ruranobe.engine.files.StorageService;
import ru.ruranobe.engine.files.YandexDiskService;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.ExternalResource;
import ru.ruranobe.mybatis.entities.tables.ExternalResourceHistory;
import ru.ruranobe.mybatis.entities.tables.User;
import ru.ruranobe.mybatis.mappers.ExternalResourcesHistoryMapper;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.LoginSession;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static ru.ruranobe.engine.files.StorageService.*;

public class ImageServices
{
    /**
     * Gets image dimensions for given file
     *
     * @param imgFile image file
     * @return dimensions of image
     * @throws IOException if the file is not a known image
     */
    public static Dimension getImageDimension(File imgFile) throws IOException
    {
        int pos = imgFile.getName().lastIndexOf(".");
        if (pos == -1)
        {
            throw new IOException("No extension for file: " + imgFile.getAbsolutePath());
        }
        String suffix = imgFile.getName().substring(pos + 1);
        Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
        if (iter.hasNext())
        {
            ImageReader reader = iter.next();
            try (ImageInputStream stream = new FileImageInputStream(imgFile))
            {
                int width;
                int height;
                reader.setInput(stream);
                width = reader.getWidth(reader.getMinIndex());
                height = reader.getHeight(reader.getMinIndex());
                return new Dimension(width, height);
            }
            finally
            {
                reader.dispose();
            }
        }

        throw new IOException("Not a known image file: " + imgFile.getAbsolutePath());
    }

    public static List<ExternalResource> uploadImage(RuraImage toUpload, List<ImageStorage> imageStorageList, ExternalResourceHistory externalResourceHistory, Map<String, String> pageContextVariables)
    {
        if (imageStorageList == null || imageStorageList.isEmpty())
        {
            throw new IllegalUnbindException("Unable to upload image because storage list is empty. Check configuration file.");
        }

        Map<String, String> nameContextVariables = new HashMap<>(pageContextVariables);
        nameContextVariables.put("uuid", UUID.randomUUID().toString());
        nameContextVariables.put("ext", toUpload.getExtension());
        nameContextVariables.put("fullname", toUpload.getTitle());
        nameContextVariables.put("name", FilenameUtils.getBaseName(toUpload.getTitle()));

        Dimension imageDimension;
        try
        {
            imageDimension = getImageDimension(toUpload.getImageFile());
        }
        catch (IOException e)
        {
            throw new RuntimeException("Cannot get image dimension", e);
        }

        List<ExternalResource> externalResources = new ArrayList<>();
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            ExternalResourcesMapper externalResourcesMapperCacheable = CachingFacade.getCacheableMapper(session, ExternalResourcesMapper.class);
            ExternalResourcesHistoryMapper externalResourcesHistoryMapperCacheable = CachingFacade.getCacheableMapper(session, ExternalResourcesHistoryMapper.class);
            externalResourcesHistoryMapperCacheable.insertExternalResourceHistory(externalResourceHistory);

            int i = 0;
            for (ImageStorage storage : imageStorageList)
            {
                if (i > 0)
                {
                    toUpload.rewind();
                }
                ExternalResource resource = new ExternalResource();
                StorageService storageService = StorageService.resolve(storage.getServiceName());
                String storagePath = storage.getStoragePath();
                String storageFileName = storage.getStorageFileName();

                User user = LoginSession.get().getUser();
                resource.setUserId(user == null ? 1 : user.getUserId()); // TODO: 1 only for testing purposes
                resource.setTitle(toUpload.getTitle());
                resource.setMimeType(toUpload.getMimeType());
                resource.setUploadedWhen(externalResourceHistory.getUploadedWhen());
                resource.setHistoryId(externalResourceHistory.getHistoryId());
                resource.setWidth(imageDimension.width);
                resource.setHeight(imageDimension.height);

                if (storagePath.contains("${") || storageFileName.contains("${"))
                {
                    StrSubstitutor sub = new StrSubstitutor(nameContextVariables);
                    storagePath = sub.replace(storagePath);
                    storageFileName = sub.replace(storageFileName);
                }
                if (storagePath.contains("${") || storageFileName.contains("${"))
                {
                    throw new IllegalArgumentException("Storage path " + storagePath + "/" + storageFileName + " is illegal. Not enough data to replace templates provided.  pageContextVariables: " + nameContextVariables + "  Check configuration file.");
                }
                toUpload.setPath(storagePath);
                toUpload.setFilename(storageFileName);

                if (PICASA == storageService)
                {
                    PicasaService.uploadImage(toUpload, storage);
                }
                else if (YANDEX_DISK == storageService)
                {
                    YandexDiskService.uploadFile(toUpload, storage);
                }
                else if (LOCAL_STORAGE == storageService)
                {
                    LocalStorageService.uploadFile(toUpload, storage);
                }
                else
                {
                    throw new IllegalArgumentException("Unknown storage service. Check configuration file.");
                }

                resource.setUrl(toUpload.getPathOnImageServiceSystem(storageService));
                resource.setThumbnail(toUpload.getThumbnailPathOnImageServiceSystem(storageService));
                externalResourcesMapperCacheable.insertExternalResource(resource);
                externalResources.add(resource);
                i++;
            }
            session.commit();
        }

        return externalResources;
    }
}
