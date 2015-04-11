package ru.ruranobe.wicket;

import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.core.util.file.WebApplicationPath;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.parameter.UrlPathPageParametersEncoder;
import org.apache.wicket.request.resource.UrlResourceReference;
import org.apache.wicket.util.crypt.CachingSunJceCryptFactory;
import ru.ruranobe.wicket.webpages.*;

public class WicketApplication extends AuthenticatedWebApplication    		
{

    @Override
    public Class<? extends WebPage> getHomePage()
    {
        return HomePage.class;
    }

    @Override
    public void init()
    {
        super.init();

        getResourceSettings().getResourceFinders().add(
			new WebApplicationPath(getServletContext(), "markupFolder"));
        
        mountPages();

        getJavaScriptLibrarySettings().setJQueryReference(new UrlResourceReference(
                Url.parse("http://cdnjs.cloudflare.com/ajax/libs/jquery/1.9.1/jquery.min.js")));
        
        getRequestCycleSettings().setResponseRequestEncoding("UTF-8");
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
        getMarkupSettings().setStripWicketTags(true);
        getSecuritySettings().setAuthenticationStrategy(new RuranobeAuthenticationStrategy("ruranobe_global_up_key"));
        getSecuritySettings().setCryptFactory(new CachingSunJceCryptFactory("randomlyGeneratedRuraCryptoKey"));
    } 

    private void mountPages()
    {
        mount(new MountedMapper("/diary", Diary.class));
        mount(new MountedMapper("/projects", FullProjects.class));
        mount(new MountedMapper("/faq", Faq.class));
        mount(new MountedMapper("/project", Project.class, new PageParameterEncoder()));
        mount(new MountedMapper("/release", Release.class, new PageParameterEncoder()));
        mount(new MountedMapper("/updates", Updates.class));
        
        
        mount(new MountedMapper("/text", FullVolumeTextViewer.class, 
                new UrlPathPageParametersEncoder()));
        
        mount(new MountedMapper("/user/register", RegistrationPage.class));
        mount(new MountedMapper("/user/login", LoginPage.class));
        mount(new MountedMapper("/user/recover/pass", PasswordRecoveryPage.class));
        mount(new MountedMapper("/user/recover/pass/email", EmailPasswordRecoveryPage.class));
        mount(new MountedMapper("/user/email/activate", ActivateEmail.class));
        mount(new MountedMapper("/upload/image", UploadImage.class));
        mount(new MountedMapper("/admin/volume", VolumeEdit.class, new PageParameterEncoder()));
    }
    
    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass()
    {
        return LoginSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass()
    {
        return LoginPage.class;
    }
}
