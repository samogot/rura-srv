package ru.ruranobe.wicket.resources.rest;

import org.apache.wicket.request.http.WebResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.ResourcePath;
import org.wicketstuff.rest.annotations.parameters.RequestParam;
import org.wicketstuff.rest.resource.gson.GsonRestResource;

@ResourcePath("/api/radio")
public class RadioRestWebService extends GsonRestResource
{

    @MethodMapping("/nowplaying/{token}")
    public NowPlayingInfo setNowPlayingInfo(String token,
                                            @RequestParam(value = "title", required = false) String title,
                                            @RequestParam(value = "artist", required = false) String artist,
                                            @RequestParam(value = "duration", required = false) String duration)
    {
        if (token.equals("123") && title != null)
        {
            nowPlayingInfo = new NowPlayingInfo();
            nowPlayingInfo.setArtist(artist);
            nowPlayingInfo.setTitle(title);
            nowPlayingInfo.setDuration(duration);
        }
        return nowPlayingInfo;
    }

    @MethodMapping("/nowplaying")
    public NowPlayingInfo getNowPlayingInfo()
    {
        return nowPlayingInfo;
    }

    @Override
    protected void handleException(WebResponse response, Exception exception)
    {
        super.handleException(response, exception);
        LOG.error("Error in REST API call", exception);
    }

    class NowPlayingInfo
    {
        public String getTitle()
        {
            return title;
        }

        public void setTitle(String title)
        {
            this.title = title;
        }

        public String getArtist()
        {
            return artist;
        }

        public void setArtist(String artist)
        {
            this.artist = artist;
        }

        public String getDuration()
        {
            return duration;
        }

        public void setDuration(String duration)
        {
            this.duration = duration;
        }

        String title;
        String artist;
        String duration;
    }

    private static final Logger LOG = LoggerFactory.getLogger(RadioRestWebService.class);
    static NowPlayingInfo nowPlayingInfo;
}
