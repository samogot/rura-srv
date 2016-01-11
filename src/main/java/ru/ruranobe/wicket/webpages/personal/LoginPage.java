package ru.ruranobe.wicket.webpages.personal;

import org.apache.wicket.markup.html.WebPage;
import ru.ruranobe.wicket.components.LoginPanel;

public class LoginPage extends WebPage
{

    @Override
    protected void onInitialize()
    {
        add(new LoginPanel("loginPanel"));
        super.onInitialize();
    }
}
