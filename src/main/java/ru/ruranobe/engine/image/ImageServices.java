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

import java.util.*;

import static ru.ruranobe.engine.files.StorageService.*;

public class ImageServices
{
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
