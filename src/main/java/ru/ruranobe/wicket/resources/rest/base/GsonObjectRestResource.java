package ru.ruranobe.wicket.resources.rest.base;

import org.apache.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.http.WebResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.rest.contenthandling.json.objserialdeserial.GsonObjectSerialDeserial;
import org.wicketstuff.rest.contenthandling.json.webserialdeserial.JsonWebSerialDeserial;
import org.wicketstuff.rest.resource.AbstractRestResource;
import org.wicketstuff.rest.resource.MethodMappingInfo;
import ru.ruranobe.wicket.LoginSession;
import ru.ruranobe.wicket.WicketApplication;

public class GsonObjectRestResource extends AbstractRestResource<JsonWebSerialDeserial>
{
    public GsonObjectRestResource()
    {
        this(new GsonObjectSerialDeserial(), WicketApplication.get());
    }

    public GsonObjectRestResource(GsonObjectSerialDeserial gsonSerialDeserial)
    {
        this(gsonSerialDeserial, WicketApplication.get());
    }

    public GsonObjectRestResource(GsonObjectSerialDeserial gsonSerialDeserial, IRoleCheckingStrategy roleCheckingStrategy)
    {
        super(new JsonWebSerialDeserial(gsonSerialDeserial), roleCheckingStrategy);
    }

    @Override
    protected void handleException(WebResponse response, Exception exception)
    {
        super.handleException(response, exception);
        LOG.error("Error in REST API call", exception);
    }

    @Override
    protected void onBeforeMethodInvoked(MethodMappingInfo mappedMethod, Attributes attributes)
    {
        WebResponse response = (WebResponse) attributes.getResponse();
        response.addHeader("Access-Control-Allow-Origin", "*");
        AuthorizeInvocation authorizeInvocation = mappedMethod.getMethod().getAnnotation(AuthorizeInvocation.class);
        if (authorizeInvocation != null)
        {
            Roles roles = new Roles(authorizeInvocation.value());
            if (!WicketApplication.get().hasAnyRole(roles))
            {
                if (LoginSession.get().getUser() == null)
                {
                    objectToResponse(new RestApiHandledErrorException("Unauthorized", "Bad authorization header"), response, mappedMethod.getOutputFormat());
                    response.setStatus(401);
                }
                else
                {
                    objectToResponse(new RestApiHandledErrorException("AccessDenied", USER_IS_NOT_ALLOWED), response, mappedMethod.getOutputFormat());
                    response.setStatus(403);

                }
            }
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(GsonObjectRestResource.class);
}