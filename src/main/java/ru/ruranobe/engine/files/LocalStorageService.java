package ru.ruranobe.engine.files;

import org.apache.wicket.util.string.Strings;
import ru.ruranobe.engine.image.ImageStorage;
import ru.ruranobe.engine.image.RuraImage;

import java.io.*;

public class LocalStorageService
{

    public static String LOCAL_STORAGE_UPLOAD_DIR;
    public static String LOCAL_STORAGE_PUBLIC_FOLDER;

    public static void initializeService(FileStorageService fileStorageService)
    {
        if (Strings.isEmpty(fileStorageService.getUploadDir()))
        {
            throw new IllegalArgumentException("UploadDir is required for LocalStorage. Correct configuration file.");
        }
        if (Strings.isEmpty(fileStorageService.getPublicFolder()))
        {
            throw new IllegalArgumentException("PublicFolder is required for LocalStorage. Correct configuration file.");
        }
        LOCAL_STORAGE_UPLOAD_DIR = fileStorageService.getUploadDir();
        LOCAL_STORAGE_PUBLIC_FOLDER = fileStorageService.getPublicFolder();
    }

    public static RuraImage uploadFile(RuraImage toUpload, ImageStorage storage)
    {

        String fullFileName = toUpload.getPath() + "/" + toUpload.getFilename();
        String filePath = LOCAL_STORAGE_UPLOAD_DIR + fullFileName;
        File parent = new File(filePath).getParentFile();
        if (!parent.exists() && !parent.mkdirs())
        {
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }
        try
        {
            OutputStream out = new FileOutputStream(filePath);

            InputStream in = toUpload.getInputStream();
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer, 0, buffer.length)) > 0)
            {
                out.write(buffer, 0, bytesRead);
                out.flush();
            }
            out.close();

            toUpload.setPathOnImageServiceSystem(StorageService.LOCAL_STORAGE, LOCAL_STORAGE_PUBLIC_FOLDER + fullFileName);
            String fullFileNameEscaped = fullFileName.replaceAll("%", "%%");
            toUpload.setThumbnailPathOnImageServiceSystem(StorageService.LOCAL_STORAGE,
                    LOCAL_STORAGE_PUBLIC_FOLDER + "/thumb" + fullFileNameEscaped + "/%dpx-" + fullFileNameEscaped);
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Unable to upload file locally on server.", ex);
        }
        return toUpload;
    }
}
