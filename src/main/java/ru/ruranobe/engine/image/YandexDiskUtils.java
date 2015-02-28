package ru.ruranobe.engine.image;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class YandexDiskUtils 
{
    public static void uploadFile(File file, String mimeType, String fileTitle, String path)
    {
        DataOutputStream out = null;
        FileInputStream in = null;
        try
        {
            String uploadLink = requestLinkForUpload(path);
            URL url = new URL(uploadLink);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            out = new DataOutputStream(connection.getOutputStream());
            in = new FileInputStream(file);
            
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer, 0, buffer.length)) > 0) 
            {
                out.write(buffer, 0, bytesRead);
                out.flush();
            }
            
            in.close();
            out.close();
            
            int responseCode = connection.getResponseCode();
            if (responseCode != 201)
            {
                throw new RuntimeException("Uploading failed. Received response code " + responseCode);
            }
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
                if (in != null)
                {
                    in.close();
                }
            }
            catch (Exception ex)
            {
                throw new RuntimeException(ex);
            }
        }
        
    }
    
    private static String requestLinkForUpload(String path) 
            throws MalformedURLException, IOException
    {
        URL url = new URL("https://cloud-api.yandex.net/v1/disk/resources/upload?path=");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
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

            String link = "\"href\"";
            int fromIndex = response.toString().indexOf(link);
            int linkStartIndex = response.indexOf("\"", fromIndex+link.length());
            int linkEndIndex = response.indexOf("\"", linkStartIndex+1);
            return response.substring(linkStartIndex, linkEndIndex);
        }
        else
        {
            throw new RuntimeException("Irregular response code " + responseCode + " received while sending GET to " + url);
        }
    }
}
