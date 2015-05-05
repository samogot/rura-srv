package ru.ruranobe.wicket.webpages;

import org.apache.wicket.markup.html.WebPage;
import ru.ruranobe.wicket.components.RegistrationPanel;

public class RegistrationPage extends WebPage
{
    @Override
    protected void onInitialize()
    {
        add(new RegistrationPanel("registrationPanel"));
        super.onInitialize();
    }
}
