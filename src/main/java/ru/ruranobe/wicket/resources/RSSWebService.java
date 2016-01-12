package ru.ruranobe.wicket.resources;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.AbstractResource;
import org.wicketstuff.rest.annotations.ResourcePath;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Update;
import ru.ruranobe.mybatis.mappers.UpdatesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

@ResourcePath("/updates.rss")
public class RSSWebService extends AbstractResource
{
    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes)
    {
        ResourceResponse resourceResponse = new ResourceResponse();
        resourceResponse.setContentType("application/rss+xml");
        resourceResponse.setTextEncoding("utf-8");

        resourceResponse.setWriteCallback(new WriteCallback()
        {
            @Override
            public void writeData(Attributes attributes) throws IOException
            {
                OutputStream outputStream = attributes.getResponse().getOutputStream();
                Writer writer = new OutputStreamWriter(outputStream);
                SyndFeedOutput output = new SyndFeedOutput();
                try
                {
                    output.output(getFeed(), writer);
                }
                catch (FeedException e)
                {
                    throw new WicketRuntimeException("Problems writing feed to response...");
                }
            }
        });

        return resourceResponse;
    }

    private SyndFeed getFeed()
    {
        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        List<Update> updateList;
        try (SqlSession session = sessionFactory.openSession())
        {
            UpdatesMapper updatesMapper = CachingFacade.getCacheableMapper(session, UpdatesMapper.class);
            updateList = updatesMapper.getLastUpdatesBy(null, null, null, 0, 20);
        }
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");

        feed.setTitle("Обновления RuRanobe");
        feed.setLink("https://ruranobe.ru");
        feed.setDescription("Последние обновления переводов на проекте RuRanobe");
        List<SyndEntry> entries = new ArrayList<>();
        for (Update update : updateList)
        {
            SyndEntry entry = new SyndEntryImpl();

            String title = update.getVolumeTitle();
            if (update.getChapterTitle() != null)
            {
                title += " - " + update.getChapterTitle();
            }
            entry.setTitle(title);
            entry.setLink("https://ruranobe.ru" + RequestCycle.get().urlFor(update.getLinkClass(),
                    update.getUrlParameters()).toString().substring(1));
            entry.setPublishedDate(update.getShowTime());
            entries.add(entry);
        }
        feed.setEntries(entries);
        return feed;
    }
}