package ru.ruranobe.wicket;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy;
import org.apache.wicket.core.request.handler.BookmarkableListenerInterfaceRequestHandler;
import org.apache.wicket.core.request.handler.ListenerInterfaceRequestHandler;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.core.util.file.WebApplicationPath;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebResponse;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.info.PageComponentInfo;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.crypt.CachingSunJceCryptFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.pageserializer.kryo.KryoSerializer;
import org.wicketstuff.rest.utils.mounting.PackageScanner;
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.wicket.webpages.admin.*;
import ru.ruranobe.wicket.webpages.common.*;
import ru.ruranobe.wicket.webpages.personal.*;
import ru.ruranobe.wicket.webpages.special.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

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
        getFrameworkSettings().setSerializer(new KryoSerializer());

        // preload rura configuration
        RuranobeUtils.getApplicationContext();

        getResourceSettings().getResourceFinders().add(
                new WebApplicationPath(getServletContext(), "markupFolder"));

        mountPages();

        getJavaScriptLibrarySettings().setJQueryReference(new ResourceReference("")
        {
            @Override
            public IResource getResource()
            {
                return null;
            }
        });

        getApplicationSettings().setAccessDeniedPage(NotFound.class);

        getRequestCycleSettings().setResponseRequestEncoding("UTF-8");
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
        getMarkupSettings().setStripWicketTags(true);
        getSecuritySettings().setAuthenticationStrategy(new RuranobeAuthenticationStrategy("ruranobe_global_up_key"));
        getSecuritySettings().setCryptFactory(new CachingSunJceCryptFactory("randomlyGeneratedRuraCryptoKey"));

        getSecuritySettings().setAuthorizationStrategy(new AnnotationsRoleAuthorizationStrategy(this));

        if (getConfigurationType() == RuntimeConfigurationType.DEVELOPMENT)
        {
            notifyRedeploy();
        }
    }

    private void notifyRedeploy()
    {
        try
        {
            File redeployNotifier = new File(getServletContext().getRealPath(File.separator), "redeploy.touch");
            if (!redeployNotifier.exists())
            {
                redeployNotifier.createNewFile();
            }
            redeployNotifier.setLastModified(System.currentTimeMillis());
        }
        catch (IOException ignored)
        {
        }
    }

    private static final Logger log = LoggerFactory.getLogger(WicketApplication.class);

    private void mountPages()
    {
        mount(new MountedMapper("/diary", Diary.class));
        mount(new MountedMapper("/projects", FullProjects.class));
        mount(new MountedMapper("/works", WorksProjects.class));
        mount(new MountedMapper("/faq", Faq.class));
        mount(new MountedMapper("/r/${project}", ProjectPage.class));
        mount(new MountedMapper("/r/${project}/${volume}", VolumePage.class));
        mount(new MountedMapper("/updates", Updates.class));
        mount(new MountedMapper("/r/${project}/${volume}/text", TextPage.class));
        mount(new MountedMapper("/r/${project}/${volume}/${chapter}", TextPage.class));
        mount(new MountedMapper("/user/cabinet", Cabinet.class));
        mount(new MountedMapper("/user/register", Register.class));
        mount(new MountedMapper("/user/login", LoginPage.class));
        mount(new MountedMapper("/user/recover/pass", PasswordRecoveryPage.class));
        mount(new MountedMapper("/user/recover/pass/email", EmailPasswordRecoveryPage.class));
        mount(new MountedMapper("/user/email/activate", ActivateEmail.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/a/${project}/${volume}/${chapter}", Editor.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/a/${project}/${volume}", VolumeEdit.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/a/${project}", ProjectEdit.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/a", GlobalEdit.class));
        mount(new Orphus.OrphusMountedMapper("/a/orphus/#{project}/#{volume}/#{chapter}", "/a/orphus"));
        mount(new MountedMapper("/notfound", NotFound.class));
        mount(new MountedMapper("/aboutus", AboutUs.class));
        mount(new MountedMapper("/recruit", Recruit.class));
        mount(new MountedMapper("/contact", Contact.class));
        mount(new MountedMapper("/help", Help.class));

        PackageScanner.scanPackage("ru.ruranobe.wicket.resources");
    }

    protected WebResponse newWebResponse(final WebRequest webRequest,
                                         final HttpServletResponse httpServletResponse)
    {
        return new ServletWebResponse((ServletWebRequest) webRequest, httpServletResponse);
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

    private static class NoVersionMapper extends MountedMapper
    {
        public NoVersionMapper(final Class<? extends IRequestablePage> pageClass)
        {
            this("/", pageClass);
        }

        public NoVersionMapper(String mountPath, final Class<? extends IRequestablePage> pageClass)
        {
            super(mountPath, pageClass, new PageParametersEncoder());
        }

        @Override
        protected void encodePageComponentInfo(Url url, PageComponentInfo info)
        {
            //Does nothing
        }

        @Override
        public Url mapHandler(IRequestHandler requestHandler)
        {
            if (requestHandler instanceof ListenerInterfaceRequestHandler || requestHandler instanceof BookmarkableListenerInterfaceRequestHandler)
            {
                return null;
            }
            else
            {
                return super.mapHandler(requestHandler);
            }
        }
    }

    public static WicketApplication get()
    {
        return (WicketApplication) WebApplication.get();
    }
}
