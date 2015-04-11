package ru.ruranobe.wicket.webpages.base;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.StatelessLink;
import ru.ruranobe.wicket.webpages.*;

public abstract class RuraHeaderAndFooter extends WebPage 
{
    @Override
    protected void onInitialize()
    {
        super.onInitialize();
        add(new StatelessLink("linkToMain") 
        {

            @Override
            public void onClick() 
            {
                setResponsePage(HomePage.class);
            }

        });
        
        add(new StatelessLink("linkToUpdates1") 
        {

            @Override
            public void onClick() 
            {
                setResponsePage(Updates.class);
            }

        });
        
        add(new StatelessLink("linkToProjects1") 
        {

            @Override
            public void onClick() 
            {
                setResponsePage(FullProjects.class);
            }

        });
        
        add(new StatelessLink("linkToxxx1") 
        {

            @Override
            public void onClick() 
            {
                setResponsePage(HomePage.class);
            }

        });
        
        add(new StatelessLink("linkToFaq1") 
        {

            @Override
            public void onClick() 
            {
                setResponsePage(Faq.class);
            }

        });
        
        add(new StatelessLink("linkToAbout1") 
        {

            @Override
            public void onClick() 
            {
                setResponsePage(HomePage.class);
            }

        });
        
        add(new StatelessLink("linkToContacts1") 
        {

            @Override
            public void onClick() 
            {
                setResponsePage(HomePage.class);
            }

        });
        
        add(new StatelessLink("linkToUpdates2") 
        {

            @Override
            public void onClick() 
            {
                setResponsePage(Updates.class);
            }

        });
        
        add(new StatelessLink("linkToProjects2") 
        {

            @Override
            public void onClick() 
            {
                setResponsePage(FullProjects.class);
            }

        });
        
        add(new StatelessLink("linkToxxx2") 
        {

            @Override
            public void onClick() 
            {
                setResponsePage(HomePage.class);
            }

        });
        
        add(new StatelessLink("linkToFaq2") 
        {

            @Override
            public void onClick() 
            {
                setResponsePage(Faq.class);
            }

        });
        
        add(new StatelessLink("linkToAbout2") 
        {

            @Override
            public void onClick() 
            {
                setResponsePage(HomePage.class);
            }

        });
        
        add(new StatelessLink("linkToHelp2") 
        {

            @Override
            public void onClick() 
            {
                setResponsePage(HomePage.class);
            }

        });
        
        add(new StatelessLink("linkToContacts2") 
        {

            @Override
            public void onClick() 
            {
                setResponsePage(HomePage.class);
            }

        });
    }
}