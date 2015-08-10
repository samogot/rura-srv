package ru.ruranobe.engine.files;

import com.google.common.collect.ImmutableMap;
import ru.ruranobe.engine.image.PicasaService;

import java.util.Map;

public enum StorageService
{
    PICASA,
    YANDEX_DISK,
    LOCAL_STORAGE;

    public static StorageService resolve(String value)
    {
        return SERVICE_NAME_TO_STORAGE_SERVICE.get(value.trim().toLowerCase());
    }

    public static void initializeService(FileStorageService fileStorageService, StorageService storageService)
    {
        switch (storageService)
        {
            case PICASA:
                PicasaService.initializeService(fileStorageService);
                break;
            case YANDEX_DISK:
                YandexDiskService.initializeService(fileStorageService);
                break;
            case LOCAL_STORAGE:
                break;
        }
    }

    private static final Map<String, StorageService> SERVICE_NAME_TO_STORAGE_SERVICE =
            new ImmutableMap.Builder<String, StorageService>()
            .put("picasa", PICASA)
            .put("picassa", PICASA)
            .put("yandexdisk", YANDEX_DISK)
            .put("localstorage", LOCAL_STORAGE)
            .build();

}
