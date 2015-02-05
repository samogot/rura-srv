package ru.ruranobe.wicket.webpages;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.ruranobe.engine.wiki.parser.WikiParser;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.ChaptersMapper;
import ru.ruranobe.mybatis.mappers.SeriesMapper;
import ru.ruranobe.mybatis.tables.Chapter;
import ru.ruranobe.mybatis.tables.Series;

public class FullVolumeTextViewer extends WebPage 
{
    public FullVolumeTextViewer(final PageParameters parameters) 
    {
        if (parameters.getNamedKeys().size() != 1)
        {
            throw REDIRECT_TO_404;
        }

        String seriesUrl = parameters.getNamedKeys().iterator().next();
        String volumeUrl = parameters.get(seriesUrl).toString();
        
        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        SqlSession session = sessionFactory.openSession();
        
        SeriesMapper seriesMapper = session.getMapper(SeriesMapper.class);
        Series series = seriesMapper.getSeriesByUrl(seriesUrl);
        
        if (series == null)
        {
            throw REDIRECT_TO_404;
        }
        
        ChaptersMapper chaptersMapper = session.getMapper(ChaptersMapper.class);
        Chapter chapter = chaptersMapper.getChapterByUrl(volumeUrl);
        
        if (chapter == null)
        {
            throw REDIRECT_TO_404;
        }
        
        session.close();
        
        String substituteWikiText = null;
        try
        {
            byte[] encoded = Files.readAllBytes(Paths.get(FullVolumeTextViewer.class.getClassLoader().getResource("substituteWikiText.txt").toURI()));
            substituteWikiText = new String(encoded, "UTF-8");
        } 
        catch (URISyntaxException ex)
        {
            Logger.getLogger(FullVolumeTextViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(FullVolumeTextViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        long l = System.currentTimeMillis();
        
        final String html = WikiParser.parseText(substituteWikiText.toString());
        
        final long l2 = System.currentTimeMillis() - l;
        
        add(new WebComponent("html")
        {
            @Override
            public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) 
            {
                Response response = getRequestCycle().getResponse();
                response.write("На парсинг текста затрачено времени в милисекундах: " + Long.toString(l2));
                response.write(html);
            }
        });
    }
    
    private static final RedirectToUrlException REDIRECT_TO_404 = new RedirectToUrlException("http://404");
}
