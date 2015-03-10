package ru.ruranobe.wicket.webpages;

import java.util.Collections;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.wicket.components.AjaxOrphusBehaviour;
import ru.ruranobe.wicket.components.AjaxOrphusMessageDialog;

public class HomePage extends WebPage 
{
    public HomePage(final PageParameters parameters) 
    {
        super(parameters);
        
        SqlSessionFactory sessionFactory = MybatisUtil.getSessionFactory();
        System.out.println(sessionFactory);
        SqlSession session = sessionFactory.openSession();
        
        add(new Image("headerImage", new PackageResourceReference(getClass(), "header-m.png")));
        
        //SeriesMapper seriesMapper = session.getMapper(SeriesMapper.class);
        
      /*  List<Series> seriesList = Collections.EMPTY_LIST;//seriesMapper.getAllSeries();
        
        session.close();
        
        
        
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
        add(seriesListView);*/
        
        AjaxOrphusMessageDialog ooo = new AjaxOrphusMessageDialog("orphus", null);
        add(ooo);
        add(new AjaxOrphusBehaviour(ooo));
    }    
    
    @Override
    public void renderHead(IHeaderResponse response) 
    {
        response.render(JavaScriptHeaderItem.forReference(JAVASCRIPT_ORPHUS));
    }
    
    private static final ResourceReference JAVASCRIPT_ORPHUS = new JavaScriptResourceReference(
            AjaxOrphusMessageDialog.class, "orphus.js"); 
}
