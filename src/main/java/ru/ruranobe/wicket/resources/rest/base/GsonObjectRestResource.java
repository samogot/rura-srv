package ru.ruranobe.wicket.resources.rest.base;

import com.google.gson.GsonBuilder;
import org.apache.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.rest.contenthandling.RestMimeTypes;
import org.wicketstuff.rest.contenthandling.json.objserialdeserial.GsonObjectSerialDeserial;
import org.wicketstuff.rest.contenthandling.json.webserialdeserial.JsonWebSerialDeserial;
import org.wicketstuff.rest.resource.AbstractRestResource;
import org.wicketstuff.rest.resource.MethodMappingInfo;
import ru.ruranobe.wicket.LoginSession;
import ru.ruranobe.wicket.RuranobeAuthenticationStrategy;
import ru.ruranobe.wicket.WicketApplication;

import java.lang.reflect.InvocationTargetException;

public class GsonObjectRestResource extends AbstractRestResource<JsonWebSerialDeserial>
{
    @Override
    protected void handleException(WebResponse response, Exception exception)
    {
        if (exception instanceof InvocationTargetException
            && ((InvocationTargetException) exception).getTargetException() instanceof RestApiHandledErrorException)
        {
            RestApiHandledErrorException restException = (RestApiHandledErrorException) ((InvocationTargetException) exception).getTargetException();
            respondError(response, restException);
        }
        else
        {
            super.handleException(response, exception);
        }
        LOG.error("Error in REST API call", exception);
    }

    @Override
    protected void onBeforeMethodInvoked(MethodMappingInfo mappedMethod, Attributes attributes)
    {
        checkLogin((WebRequest) attributes.getRequest());
        WebResponse response = (WebResponse) attributes.getResponse();
        response.addHeader("Access-Control-Allow-Origin", "*");
        AuthorizeInvocation authorizeInvocation = mappedMethod.getMethod().getAnnotation(AuthorizeInvocation.class);
        if (authorizeInvocation != null)
        {
            Roles roles = new Roles(authorizeInvocation.value());
            if (!WicketApplication.get().hasAnyRole(roles))
            {
                respondError(response, getUnauthorizedException());
            }
        }
    }

    protected RestApiHandledErrorException getUnauthorizedException()
    {
        if (LoginSession.get().getUser() == null)
        {
            return new RestApiHandledErrorException(401, "Unauthorized", "Bad authorization header");
        }
        else
        {
            return new RestApiHandledErrorException(403, "AccessDenied", USER_IS_NOT_ALLOWED);
        }
    }

    protected RestApiHandledErrorException getNotFoundException()
    {
        return new RestApiHandledErrorException(404, "NotFound", "No object is found");
    }

    private void respondError(WebResponse response, RestApiHandledErrorException restException)
    {
        objectToResponse(restException, response, RestMimeTypes.APPLICATION_JSON);
        response.setStatus(restException.getHttpResponseCode());
    }

    private void checkLogin(WebRequest request)
    {
        if (!LoginSession.get().isSignedIn())
        {
            RuranobeAuthenticationStrategy authenticationStrategy = (RuranobeAuthenticationStrategy)
                    WicketApplication.get().getSecuritySettings().getAuthenticationStrategy();

            // get username and password from persistence store
            String[] data = authenticationStrategy.loadFromHeader(request);
            if ((data != null) && (data.length > 1))
            {
                // try to sign in the user
                LoginSession.get().signIn(data[0], data[1]);
            }
        }
    }

    public GsonObjectRestResource()
    {
        this(new GsonObjectSerialDeserial(new GsonBuilder().setDateFormat("dd.MM.yyyy HH:mm:ss Z").create()), WicketApplication.get());
    }

    public GsonObjectRestResource(GsonObjectSerialDeserial gsonSerialDeserial)
    {
        this(gsonSerialDeserial, WicketApplication.get());
    }

    public GsonObjectRestResource(GsonObjectSerialDeserial gsonSerialDeserial, IRoleCheckingStrategy roleCheckingStrategy)
    {
        super(new JsonWebSerialDeserial(gsonSerialDeserial), roleCheckingStrategy);
    }
    private static final Logger LOG = LoggerFactory.getLogger(GsonObjectRestResource.class);
}