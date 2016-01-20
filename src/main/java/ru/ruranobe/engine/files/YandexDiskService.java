package ru.ruranobe.engine.files;

import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ruranobe.engine.image.ImageStorage;
import ru.ruranobe.engine.image.RuraImage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;

public class YandexDiskService
{

    public static void initializeService(FileStorageService fileStorageService)
    {
        if (Strings.isEmpty(fileStorageService.getAccessToken()))
        {
            throw new IllegalArgumentException("AccessToken is required for YandexDisk. Correct configuration file.");
        }
        if (Strings.isEmpty(fileStorageService.getUploadDir()))
        {
            throw new IllegalArgumentException("UploadDir is required for YandexDisk. Correct configuration file.");
        }
        if (Strings.isEmpty(fileStorageService.getPublicFolder()))
        {
            throw new IllegalArgumentException("PublicFolder is required for YandexDisk. Correct configuration file.");
        }
        YANDEX_DISK_ACCESS_TOKEN = fileStorageService.getAccessToken();
        YANDEX_DISK_UPLOAD_DIR = fileStorageService.getUploadDir();
        try
        {
            YANDEX_DISK_PUBLIC_FOLDER = URLEncoder.encode(fileStorageService.getPublicFolder(), "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static RuraImage uploadFile(RuraImage image, ImageStorage storage)
    {
        DataOutputStream out = null;
        try
        {
            String uploadLink = requestLinkForUpload(YANDEX_DISK_UPLOAD_DIR + image.getPath() + "/" + image.getFilename());
            URL url = new URL(uploadLink);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            out = new DataOutputStream(connection.getOutputStream());

            InputStream in = image.getInputStream();
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer, 0, buffer.length)) > 0)
            {
                out.write(buffer, 0, bytesRead);
                out.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 201)
            {
                String response = IOUtils.toString(connection.getInputStream(), null);
                throw new RuntimeException("Uploading failed. Received response code " + responseCode + " received while sending GET to " + url + ". Response is: \n" + response);
            }
            try
            {

                String previewUrl = URLDecoder.decode(getPreviewUrl(image.getPath() + "/" + image.getFilename()), "UTF-8");
                image.setPathOnImageServiceSystem(StorageService.YANDEX_DISK,
                        previewUrl.replace("/preview/", "/disk/"));
                image.setThumbnailPathOnImageServiceSystem(StorageService.YANDEX_DISK,
                        previewUrl.replaceAll("%", "%%").replace("&size=S&", "&size=%dx99999&"));
            }
            catch (UnsupportedEncodingException e)
            {
                throw new RuntimeException(e);
            }
            return image;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
            }
            catch (Exception ex)
            {
                throw new RuntimeException(ex);
            }
        }
    }

    private static void checkAndCreateFolder(Path path)
            throws IOException, JSONException
    {
        URL url = new URL("https://cloud-api.yandex.net/v1/disk/resources/?path=" + URLEncoder.encode(path.toString(), "UTF-8"));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", YANDEX_DISK_ACCESS_TOKEN);
        if (connection.getResponseCode() != 200)
        {
            checkAndCreateFolder(path.getParent());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Authorization", YANDEX_DISK_ACCESS_TOKEN);
            connection.getResponseCode();
        }
    }

    private static String getPreviewUrl(String path)
            throws IOException, JSONException
    {
        return getPreviewUrl(path, 2);
    }

    private static String getPreviewUrl(String path, int count)
            throws IOException, JSONException
    {
        URL url = new URL("https://cloud-api.yandex.net/v1/disk/public/resources/?public_key=" + YANDEX_DISK_PUBLIC_FOLDER
                          + "&fields=preview&path=" + URLEncoder.encode(path, "UTF-8"));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", YANDEX_DISK_ACCESS_TOKEN);
        int responseCode = connection.getResponseCode();
        String response = IOUtils.toString(connection.getInputStream(), null);

        if (responseCode == 200)
        {
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.has("preview"))
            {
                return jsonResponse.getString("preview");
            }
            else if (count > 0)
            {
                return getPreviewUrl(path, --count);
            }
            else
            {
                return YANDEX_DISK_PUBLIC_FOLDER + path;
            }
        }
        else
        {

            throw new RuntimeException("Irregular response code " + responseCode + " received while sending GET to " + url + ". Response is: \n" + response);
        }
    }

    private static String requestLinkForUpload(String path)
            throws IOException, JSONException
    {
        checkAndCreateFolder(Paths.get(path).getParent());


        URL url = new URL("https://cloud-api.yandex.net/v1/disk/resources/upload/?overwrite=true&path=" + URLEncoder.encode(path, "UTF-8"));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", YANDEX_DISK_ACCESS_TOKEN);
        int responseCode = connection.getResponseCode();

        String response = IOUtils.toString(connection.getInputStream(), null);
        if (responseCode == 200)
        {
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.getString("href");
        }
        else
        {
            throw new RuntimeException("Irregular response code " + responseCode + " received while sending GET to " + url + ". Response is: \n" + response);
        }
    }

    public static String YANDEX_DISK_UPLOAD_DIR;
    public static String YANDEX_DISK_PUBLIC_FOLDER;
    public static String YANDEX_DISK_ACCESS_TOKEN;
    private static final Logger LOG = LoggerFactory.getLogger(YandexDiskService.class);
}
