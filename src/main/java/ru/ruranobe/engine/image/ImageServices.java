package ru.ruranobe.engine.image;

import com.sun.nio.sctp.IllegalUnbindException;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.ibatis.session.SqlSession;
import ru.ruranobe.engine.files.LocalStorageService;
import ru.ruranobe.engine.files.StorageService;
import ru.ruranobe.engine.files.YandexDiskService;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ExternalResourcesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.mybatis.tables.ExternalResource;

import java.util.*;
import static ru.ruranobe.engine.files.StorageService.*;

public class ImageServices
{
    public static List<ExternalResource> uploadImage(RuraImage toUpload, List<ImageStorage> imageStorageList, Map<String, String> pageContextVariables)
    {
        if (imageStorageList == null || imageStorageList.isEmpty())
        {
            throw new IllegalUnbindException("Unable to upload image because storage list is empty. Check configuration file.");
        }

        List<ExternalResource> externalResources = new ArrayList<ExternalResource>();
        SqlSession session = MybatisUtil.getSessionFactory().openSession();
        try
        {
            ExternalResourcesMapper externalResourcesMapperCacheable = CachingFacade.getCacheableMapper(session, ExternalResourcesMapper.class);

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

                // TODO: only for testing purposes
                resource.setUserId(947);
                resource.setTitle(toUpload.getTitle());
                resource.setMimeType(toUpload.getMimeType());
                resource.setUploadedWhen(new Date());

                if (storagePath.contains("${"))
                {
                    StrSubstitutor sub = new StrSubstitutor(pageContextVariables);
                    storagePath = sub.replace(storagePath);
                }
                if (storagePath.contains("${"))
                {
                    throw new IllegalArgumentException("Storage path for picassa " + storagePath + " is illegal. Not enough data to replace templates provided.  pageContextVariables: " + pageContextVariables + "  Check configuration file.");
                }
                toUpload.setPath(storagePath);

                if (PICASA == storageService)
                {
                    PicasaService.uploadImage(toUpload, storage);

                    resource.setUrl(toUpload.getPathOnImageServiceSystem(PICASA));
                    externalResourcesMapperCacheable.insertExternalResource(resource);
                    externalResources.add(resource);
                }
                else if (YANDEX_DISK == storageService)
                {
                    YandexDiskService.uploadFile(toUpload, storage);

                    resource.setUrl(toUpload.getPathOnImageServiceSystem(YANDEX_DISK));
                    externalResourcesMapperCacheable.insertExternalResource(resource);
                    externalResources.add(resource);
                }
                else if (LOCAL_STORAGE == storageService)
                {
                    LocalStorageService.uploadFile(toUpload, storage);

                    resource.setUrl(toUpload.getPathOnImageServiceSystem(LOCAL_STORAGE));
                    externalResourcesMapperCacheable.insertExternalResource(resource);
                    externalResources.add(resource);
                }
                i++;
            }
            session.commit();
        }
        finally
        {
            session.close();
        }

        return externalResources;
    }
}
