package ru.ruranobe.wicket.resources.rest;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.ResourcePath;
import org.wicketstuff.rest.annotations.parameters.RequestParam;
import org.wicketstuff.rest.resource.gson.GsonRestResource;
import ru.ruranobe.config.ApplicationContext;

import java.util.Collection;

@ResourcePath("/api/radio")
public class RadioRestWebService extends GsonRestResource
{

    @MethodMapping("/nowplaying")
    public Collection<NowPlayingInfo> setNowPlayingInfo(@RequestParam(value = "token", required = false) String token,
                                                        @RequestParam(value = "title", required = false) String title,
                                                        @RequestParam(value = "artist", required = false) String artist,
                                                        @RequestParam(value = "len", required = false) String len,
                                                        @RequestParam(value = "playcount", required = false) Integer playcount,
                                                        @RequestParam(value = "listeners", required = false) Integer listeners,
                                                        @RequestParam(value = "showname", required = false) String showname)
    {
        if (ApplicationContext.getRadioApiToken().equals(token))
        {
            synchronized (nowPlayingInfo)
            {
                NowPlayingInfo info;
                boolean toadd = false;
                if (!nowPlayingInfo.isEmpty())
                {
                    info = nowPlayingInfo.get(nowPlayingInfo.size() - 1);
                    if (!Strings.isEqual(info.title, title) ||
                        !Strings.isEqual(info.artist, artist) ||
                        !Strings.isEqual(info.len, len))
                    {
                        info = new NowPlayingInfo();
                        toadd = true;
                    }
                }
                else
                {
                    info = new NowPlayingInfo();
                    toadd = true;
                }
                info.title = title;
                info.artist = artist;
                info.len = len;
                info.playcount = playcount;
                info.listeners = listeners;
                info.showname = showname;
                if (toadd)
                {
                    nowPlayingInfo.add(info);
                }
            }
        }

        return nowPlayingInfo;
    }

    @Override
    protected void handleException(WebResponse response, Exception exception)
    {
        super.handleException(response, exception);
        LOG.error("Error in REST API call", exception);
    }

    private class NowPlayingInfo
    {
        public String title;
        public String artist;
        public String len;
        public Integer playcount;
        public Integer listeners;
        public String showname;
    }

    private static final Logger LOG = LoggerFactory.getLogger(RadioRestWebService.class);
    private static final CircularFifoQueue<NowPlayingInfo> nowPlayingInfo = new CircularFifoQueue<>(10);
}
