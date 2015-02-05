package ru.ruranobe.wicket.webpages;

import org.apache.wicket.markup.html.WebPage;
import ru.ruranobe.wicket.components.EmailPasswordRecoveryPanel;

public class EmailPasswordRecoveryPage extends WebPage
{
    @Override
    protected void onInitialize() 
    {
        add(new EmailPasswordRecoveryPanel("emailPasswordRecoveryPanel"));
        super.onInitialize();
    }
}
