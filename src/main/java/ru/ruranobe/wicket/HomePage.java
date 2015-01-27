package ru.ruranobe.wicket;

import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.resource.PackageResourceReference;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.mappers.SeriesMapper;
import ru.ruranobe.mybatis.tables.Series;

public class HomePage extends WebPage 
{
    public HomePage(final PageParameters parameters) 
    {
        super(parameters);
        
        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        System.out.println(sessionFactory);
        SqlSession session = sessionFactory.openSession();
        
        SeriesMapper seriesMapper = session.getMapper(SeriesMapper.class);
        
        List<Series> seriesList = seriesMapper.getAllSeries();
        
        session.close();
        
        add(new Image("headerImage",new PackageResourceReference(getClass(), "header-m.png")));
        
        ListView<Series> seriesListView = new ListView<Series> ("seriesListView", seriesList)
        {
            @Override
            protected void populateItem(final ListItem <Series> listItem)
            {
                final Series series = listItem.getModelObject();
                ExternalLink seriesListViewLink = new ExternalLink("seriesListViewLink", "/r/"+series.getNameUrl());
                Image seriesListViewImage = new Image("seriesListViewImage", series.getNameUrl()+".png");
                seriesListViewImage.add(new Behavior() 
                {
                    @Override
                    public void onComponentTag(Component component, ComponentTag tag) 
                    {
                       // tag.put("src", series.getNameUrl());
                        tag.put("alt", series.getTitle());
                        tag.put("width", 220);
                        tag.put("height", 73);
                    }
                });
                seriesListViewLink.add(seriesListViewImage);
                listItem.add(seriesListViewLink);
            }
            
        };
        add(seriesListView);
    }    
}
