package ru.ruranobe.wicket;

import net.ftlines.wicketsource.WicketSource;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.core.request.handler.BookmarkableListenerInterfaceRequestHandler;
import org.apache.wicket.core.request.handler.ListenerInterfaceRequestHandler;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.core.util.file.WebApplicationPath;
import org.apache.wicket.markup.html.WebPage;
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
import ru.ruranobe.misc.RuranobeUtils;
import ru.ruranobe.wicket.resources.BookmarksRestWebService;
import ru.ruranobe.wicket.resources.OrphusRestWebService;
import ru.ruranobe.wicket.webpages.*;

import javax.servlet.http.HttpServletResponse;

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
        WicketSource.configure(this);

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
        mount(new MountedMapper("/r/${project}", ProjectPage.class));
        mount(new MountedMapper("/r/${project}/${volume}", VolumePage.class));
        mount(new MountedMapper("/updates", Updates.class));
        mount(new MountedMapper("/r/${project}/${volume}/text", Text.class));
        mount(new MountedMapper("/r/${project}/${volume}/${chapter}", Text.class));
        mount(new MountedMapper("/user/register", Register.class));
        mount(new MountedMapper("/user/login", LoginPage.class));
        mount(new MountedMapper("/user/recover/pass", PasswordRecoveryPage.class));
        mount(new MountedMapper("/user/recover/pass/email", EmailPasswordRecoveryPage.class));
        mount(new MountedMapper("/user/email/activate", ActivateEmail.class));
        mount(new MountedMapper("/upload/image", UploadImage.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/a/${project}/${volume}/${chapter}", Editor.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/a/${project}/${volume}", VolumeEdit.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/a/${project}", ProjectEdit.class));
        getRootRequestMapperAsCompound().add(new NoVersionMapper("/a", GlobalEdit.class));

        mountResource("/bookmarks", new ResourceReference("bookmarksResource")
        {
            BookmarksRestWebService bookmarksResource = new BookmarksRestWebService();

            @Override
            public IResource getResource()
            {
                return bookmarksResource;
            }
        });

        mountResource("/orphus", new ResourceReference("orphusResource")
        {
            OrphusRestWebService orphusResource = new OrphusRestWebService();

            @Override
            public IResource getResource()
            {
                return orphusResource;
            }
        });
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
}
