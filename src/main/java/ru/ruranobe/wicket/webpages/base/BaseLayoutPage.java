package ru.ruranobe.wicket.webpages.base;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

import ru.ruranobe.wicket.components.LoginPanel;
import ru.ruranobe.wicket.components.UserActionsPanel;

public abstract class BaseLayoutPage extends WebPage {

	protected UserActionsPanel userActionsPanel = null;
	protected LoginPanel loginPanel = null;

	@Override
	protected void onInitialize() {
		if (loginPanel == null) {
			add(loginPanel = new LoginPanel("loginPanel"));
		}
		if (userActionsPanel == null) {
			add(userActionsPanel = new UserActionsPanel("userActionsPanel"));
		}
		add(new Label("pageTitle", getPageTitle()));
		super.onInitialize();
	}

	protected String getPageTitle() {
		return "РуРанобе";
	}
}