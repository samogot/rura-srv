package ru.ruranobe.wicket;

import ru.ruranobe.wicket.webpages.HomePage;
import ru.ruranobe.wicket.webpages.FullVolumeTextViewer;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.parameter.UrlPathPageParametersEncoder;

/**
 * Application object for your web application.
 * If you want to run this application without deploying, run the Start class.
 * 
 * @see org.ruranobe.Start#main(String[])
 */
public class WicketApplication extends WebApplication
{
    /**
    * @see org.apache.wicket.Application#getHomePage()
    */
    @Override
    public Class<? extends WebPage> getHomePage()
    {
        return HomePage.class;
    }

    /**
    * @see org.apache.wicket.Application#init()
    */
    @Override
    public void init()
    {
        super.init();

        mount(new MountedMapper("/text", FullVolumeTextViewer.class, 
                new UrlPathPageParametersEncoder()));
        
        getRequestCycleSettings().setResponseRequestEncoding("UTF-8");
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
        getMarkupSettings().setStripWicketTags(true);
    }
}
