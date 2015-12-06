package ru.ruranobe.engine.image;

import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.media.BaseMediaSource;
import com.google.gdata.data.media.MediaStreamSource;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.GphotoEntry;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.data.photos.UserFeed;
import com.google.gdata.util.ServiceException;
import org.apache.wicket.util.string.Strings;
import ru.ruranobe.engine.files.FileStorageService;
import ru.ruranobe.engine.files.StorageService;
import ru.ruranobe.wicket.RuraConstants;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

public class PicasaService
{

    public static void initializeService(FileStorageService fileStorageService)
    {
        if (Strings.isEmpty(fileStorageService.getClientId())
            || Strings.isEmpty(fileStorageService.getClientSecret())
            || Strings.isEmpty(fileStorageService.getRefreshToken()))
        {
            throw new IllegalArgumentException("ClientId, ClientSecret and RefreshToken are mandatory for Picassa. Correct configuration file.");
        }

        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();

        setRefreshAndAccessTokens(fileStorageService, httpTransport, jsonFactory);

        GoogleCredential credential = new GoogleCredential.Builder()
                .setClientSecrets(fileStorageService.getClientId(), fileStorageService.getClientSecret())
                .setJsonFactory(jsonFactory)
                .setTransport(httpTransport)
                .build()
                .setAccessToken(GOOGLE_OAUTH_ACCESS_TOKEN)
                .setRefreshToken(GOOGLE_OAUTH_REFRESH_TOKEN);

        PICASA_WEBSERVICE = new PicasawebService(RuraConstants.GOOGLE_APPLICATION_NAME);
        PICASA_WEBSERVICE.setOAuth2Credentials(credential);

        reloadAlbumCache();
    }

    private static void setRefreshAndAccessTokens(FileStorageService fileStorageService, HttpTransport httpTransport, JsonFactory jsonFactory)
    {
        GOOGLE_OAUTH_REFRESH_TOKEN = fileStorageService.getRefreshToken();

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(httpTransport,
                jsonFactory,
                new GenericUrl(RuraConstants.GOOGLE_TOKEN_SERVER_URL),
                GOOGLE_OAUTH_REFRESH_TOKEN);

        refreshTokenRequest.setClientAuthentication(
                new BasicAuthentication(fileStorageService.getClientId(),
                        fileStorageService.getClientSecret()));

        List<String> scope = Arrays.asList("http://picasaweb.google.com/data/");
        refreshTokenRequest.setScopes(scope);

        try
        {
            TokenResponse tokenResponse = refreshTokenRequest.execute();
            GOOGLE_OAUTH_ACCESS_TOKEN = tokenResponse.getAccessToken();
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Unable to get access token by refresh token.", ex);
        }
    }

    public static synchronized RuraImage uploadImage(RuraImage toUpload, ImageStorage storage)
    {
        String albumTitle = toUpload.getPath();
        if (albumTitle.contains("/") || albumTitle.contains("\\"))
        {
            throw new IllegalArgumentException("Storage path for picassa " + albumTitle + " is illegal. Picassa doesn't support nested albums. Check configuration file.");
        }
        if (Strings.isEmpty(albumTitle))
        {
            albumTitle = "unsorted";
        }
        if (!ALBUM_TITLE_TO_ALBUM_ENTRY.containsKey(albumTitle))
        {
            try
            {
                insertAlbum(albumTitle);
            }
            catch (Exception ex1)
            {
                reloadAlbumCache();
                try
                {
                    insertAlbum(albumTitle);
                }
                catch (Exception ex2)
                {
                    throw new RuntimeException(ex2);
                }
            }
        }
        PhotoEntry photoEntry = new PhotoEntry();
        photoEntry.setTitle(new PlainTextConstruct(toUpload.getFilename()));
        BaseMediaSource imageMediaSource =
                new MediaStreamSource(toUpload.getInputStream(), toUpload.getMimeType());
        photoEntry.setMediaSource(imageMediaSource);
        photoEntry.setAlbumAccess(API_PREFIX);
        try
        {
            photoEntry = PICASA_WEBSERVICE.insert(
                    new URL(ALBUM_TITLE_TO_ALBUM_ENTRY.get(albumTitle).getFeedLink().getHref()),
                    photoEntry);
        }
        catch (Exception ex1)
        {
            reloadAlbumCache();
            try
            {
                photoEntry = PICASA_WEBSERVICE.insert(
                        new URL(ALBUM_TITLE_TO_ALBUM_ENTRY.get(albumTitle).getFeedLink().getHref()),
                        photoEntry);
            }
            catch (Exception ex2)
            {
                throw new RuntimeException(ex2);
            }
        }
        try
        {
            String origUrl = URLDecoder.decode(photoEntry.getMediaThumbnails().get(0).getUrl(), "UTF-8");
            toUpload.setPathOnImageServiceSystem(StorageService.PICASA, origUrl.replace("/s72/", "/"));
            toUpload.setThumbnailPathOnImageServiceSystem(StorageService.PICASA, origUrl.replaceAll("%", "%%").replace("/s72/", "/w%d/"));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
        return toUpload;
    }

    private static AlbumEntry insertAlbum(String albumTitle) throws IOException, ServiceException
    {
        AlbumEntry albumEntry = new AlbumEntry();
        albumEntry.setId(albumTitle);
        albumEntry.setDate(new Date(System.currentTimeMillis()));
        albumEntry.setAccess("private");
        albumEntry.setTitle(new PlainTextConstruct(albumTitle));
        albumEntry.setDescription(new PlainTextConstruct("autogenerated"));
        String feedUrl = API_PREFIX + "default";
        albumEntry = PICASA_WEBSERVICE.insert(new URL(feedUrl), albumEntry);
        ALBUM_TITLE_TO_ALBUM_ENTRY.put(albumTitle, albumEntry);
        return albumEntry;
    }

    private static void reloadAlbumCache()
    {
        ALBUM_TITLE_TO_ALBUM_ENTRY = new HashMap<String, AlbumEntry>();
        try
        {
            List<AlbumEntry> albums = getAlbums();
            for (AlbumEntry entry : albums)
            {
                ALBUM_TITLE_TO_ALBUM_ENTRY.put(entry.getTitle().getPlainText(), entry);
            }
            if (!ALBUM_TITLE_TO_ALBUM_ENTRY.containsKey("unsorted"))
            {
                ALBUM_TITLE_TO_ALBUM_ENTRY.put("unsorted", insertAlbum("unsorted"));
            }
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
        catch (ServiceException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    static List<AlbumEntry> getAlbums() throws ServiceException, IOException
    {
        String albumUrl = API_PREFIX + "default";
        UserFeed userFeed = PICASA_WEBSERVICE.getFeed(new URL(albumUrl), UserFeed.class);

        List<GphotoEntry> entries = userFeed.getEntries();

        List<AlbumEntry> albums = new ArrayList<AlbumEntry>();
        for (GphotoEntry entry : entries)
        {
            GphotoEntry adapted = entry.getAdaptedEntry();

            if (adapted instanceof AlbumEntry)
            {
                albums.add((AlbumEntry) adapted);
            }
        }

        return albums;
    }

   /* static <T extends GphotoEntry> T insert(GphotoEntry<?> parent, T entry)
            throws IOException, ServiceException
    {
        String feedUrl = getLinkByRel(parent.getLinks(), Link.Rel.FEED);
        return PICASA_WEBSERVICE.insert(new URL(feedUrl), entry);
    }

    static String getLinkByRel(List<Link> links, String relValue)
    {
        for (Link link : links)
        {
            if (relValue.equals(link.getRel()))
            {
                return link.getHref();
            }
        }
        throw new IllegalArgumentException("Missing " + relValue + " link.");
    }*/

    private static PicasawebService PICASA_WEBSERVICE;
    private static String API_PREFIX = "https://picasaweb.google.com/data/feed/api/user/";
    private static Map<String, AlbumEntry> ALBUM_TITLE_TO_ALBUM_ENTRY;
    private static String GOOGLE_OAUTH_REFRESH_TOKEN;
    private static String GOOGLE_OAUTH_ACCESS_TOKEN;
}
