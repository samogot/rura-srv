package ru.ruranobe.wicket.components.admin;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ILabelProvider;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by samogot on 29.08.15.
 */
public class ImageUploaderComponent extends Panel implements ILabelProvider
{
    @Override
    public void renderHead(IHeaderResponse response)
    {
        response.render(OnDomReadyHeaderItem.forScript(String.format(";initFileUpload('#%s');", input.getMarkupId())));
    }

    public WebMarkupContainer getImage()
    {
        return image;
    }

    public void setImage(WebMarkupContainer image)
    {
        this.image = image;
    }

    public WebMarkupContainer getInput()
    {
        return input;
    }

    public void setInput(WebMarkupContainer input)
    {
        this.input = input;
    }

    public Map<String, String> getContextVariables()
    {
        return contextVariables;
    }

    public ImageUploaderComponent setContextVariables(Map<String, String> contextVariables)
    {
        this.contextVariables = contextVariables;
        return this;
    }

    @Override
    public IModel getLabel()
    {
        return null;
    }

    public ImageUploaderComponent setLabel(IModel<String> label)
    {
        this.label = label;
        return this;
    }

    @Override
    protected void onInitialize()
    {
        super.onInitialize();
        add(image = new WebMarkupContainer("image"));
        add(input = new WebMarkupContainer("input"));
        image.add(new AttributeModifier("src", PropertyModel.of(getDefaultModel(), "url")));
        input.add(new AbstractAjaxBehavior()
        {
            @Override
            public void onRequest()
            {
                pocessUpload((HttpServletRequest) getRequest().getContainerRequest());
            }

            @Override
            protected void onComponentTag(ComponentTag tag)
            {
                tag.getAttributes().put("data-upload-url", getCallbackUrl());
            }
        });
        input.setOutputMarkupId(true);
    }

    protected void pocessUpload(HttpServletRequest request)
    {
    }

    public ImageUploaderComponent(String id)
    {
        super(id);
    }

    public ImageUploaderComponent(String id, IModel<?> model)
    {
        super(id, model);
    }

    private IModel<String> label;
    private Map<String, String> contextVariables;
    private WebMarkupContainer image;
    private WebMarkupContainer input;
}
