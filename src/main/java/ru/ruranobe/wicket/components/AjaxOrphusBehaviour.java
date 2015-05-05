package ru.ruranobe.wicket.components;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;

import static ru.ruranobe.wicket.components.AjaxOrphusMessageDialog.*;

public class AjaxOrphusBehaviour extends AjaxEventBehavior
{

    private static final long serialVersionUID = 1L;
    private final AjaxOrphusMessageDialog ajaxOrphusMessageDialog;

    public AjaxOrphusBehaviour(AjaxOrphusMessageDialog ajaxOrphusMessageDialog)
    {
        super("keydown");
        this.ajaxOrphusMessageDialog = ajaxOrphusMessageDialog;
    }

    @Override
    protected void updateAjaxAttributes(AjaxRequestAttributes attributes)
    {
        super.updateAjaxAttributes(attributes);

        IAjaxCallListener listener = new AjaxCallListener()
        {

            @Override
            public CharSequence getPrecondition(Component component)
            {
                return "return isOrphusPreconditionsMet(attrs);";
            }
        };

        attributes.getAjaxCallListeners().add(listener);
        attributes.getDynamicExtraParameters()
                .add("return getOrphusParameters();");
        attributes.setAllowDefault(true);
    }

    @Override
    protected void onEvent(AjaxRequestTarget target)
    {
        Request request = RequestCycle.get().getRequest();
        String originalText = request.getRequestParameters()
                .getParameterValue(ORPHUS_COMMENT_ORIGINAL_TEXT).toString("");
        String startOffset = request.getRequestParameters()
                .getParameterValue(ORPHUS_COMMENT_START_OFFSET).toString("");
        String paragraph = request.getRequestParameters()
                .getParameterValue(ORPHUS_COMMENT_PARAGRAPH).toString("");
        ajaxOrphusMessageDialog.show(target, originalText, startOffset, paragraph);
    }
}

