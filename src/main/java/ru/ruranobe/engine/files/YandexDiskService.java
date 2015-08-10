package ru.ruranobe.engine.files;

import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.engine.files.FileStorageService;
import ru.ruranobe.engine.files.StorageService;
import ru.ruranobe.engine.image.ImageStorage;
import ru.ruranobe.engine.image.RuraImage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class YandexDiskService
{

    public static void initializeService(FileStorageService fileStorageService)
    {
        if (Strings.isEmpty(fileStorageService.getAccessToken()))
        {
            throw new IllegalArgumentException("AccessToken is required for YandexDisk. Correct configuration file.");
        }
        YANDEX_DISK_ACCESS_TOKEN = fileStorageService.getAccessToken();
    }

    public static RuraImage uploadFile(RuraImage image, ImageStorage storage)
    {
        DataOutputStream out = null;
        try
        {
            String uploadLink = requestLinkForUpload(image.getPath() + "/" + image.getTitle());
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
                throw new RuntimeException("Uploading failed. Received response code " + responseCode);
            }

            image.setPathOnImageServiceSystem(StorageService.YANDEX_DISK,
                    image.getPath() + "/" + image.getTitle() + "." + image.getExtension());
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

    private static String requestLinkForUpload(String path)
            throws IOException, JSONException
    {
        URL url = new URL("https://cloud-api.yandex.net/v1/disk/resources/upload/?overwrite=true&path=" + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", YANDEX_DISK_ACCESS_TOKEN);
        int responseCode = connection.getResponseCode();

        if (responseCode == 200)
        {
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = input.readLine()) != null)
            {
                response.append(inputLine);
            }
            input.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getString("href");
        }
        else
        {
            throw new RuntimeException("Irregular response code " + responseCode + " received while sending GET to " + url);
        }
    }

    public static String YANDEX_DISK_ACCESS_TOKEN;
}
