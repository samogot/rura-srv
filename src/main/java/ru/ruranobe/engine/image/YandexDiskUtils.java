package ru.ruranobe.engine.image;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;

public class YandexDiskUtils 
{
    public static Image uploadFile(Image image)
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
             
            InputStream in = image.getImageSource().getInputStream();
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
            
            image.putPathOnImageServiceSystem(Image.ImageServiceSystem.YANDEX_DISK, 
                    "Samogot Yandex Disk path: " + image.getPath() + "/" + image.getTitle());
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
            throws MalformedURLException, IOException, JSONException
    {
        URL url = new URL("https://cloud-api.yandex.net/v1/disk/resources/upload/?overwrite=true&path="+path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "546c9d23e516468f8bd5d3298d8af447");
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

    static void isImagePresentOnServer(Image image)
    {
        
    }
}
