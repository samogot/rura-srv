package ru.ruranobe.wicket.components.sidebar;

import com.rometools.utils.Strings;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ruranobe.mybatis.MybatisUtil;
import ru.ruranobe.mybatis.entities.tables.Project;
import ru.ruranobe.mybatis.entities.tables.Requisite;
import ru.ruranobe.mybatis.entities.tables.Volume;
import ru.ruranobe.mybatis.mappers.RequisitesMapper;
import ru.ruranobe.mybatis.mappers.cacheable.CachingFacade;
import ru.ruranobe.wicket.RuraConstants;
import ru.ruranobe.wicket.components.CreditCardConverter;
import ru.ruranobe.wicket.components.LabelHideableOnNull;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class RequisitesSidebarModule extends SidebarModuleBase
{
    public static RequisitesSidebarModule makeDefault()
    {
        int requisiteId = RuraConstants.DEFAULT_REQUISITE_ID;
        return new RequisitesSidebarModule(requisiteId);
    }

    public static RequisitesSidebarModule makeProjectOrDefault(Project project)
    {

        int requisiteId = project != null && project.getRequisiteId() != null ? project.getRequisiteId() : RuraConstants.DEFAULT_REQUISITE_ID;
        return new RequisitesSidebarModule(requisiteId);
    }

    public static RequisitesSidebarModule makeVolumeOrProjectOrDefault(Volume volume, Project project)
    {

        int requisiteId = volume != null && volume.getRequisiteId() != null ? volume.getRequisiteId() :
                          project != null && project.getRequisiteId() != null ? project.getRequisiteId() : RuraConstants.DEFAULT_REQUISITE_ID;
        return new RequisitesSidebarModule(requisiteId);
    }

    public RequisitesSidebarModule(int requisiteId)
    {
        super("sidebarModule", "requisites", "Реквизиты");
        moduleBody.add(AttributeModifier.replace("class", "actions"));
        Requisite requisite;
        try (SqlSession session = MybatisUtil.getSessionFactory().openSession())
        {
            requisite = CachingFacade.getCacheableMapper(session, RequisitesMapper.class).getRequisiteById(requisiteId);
            setDefaultModel(new CompoundPropertyModel<>(requisite));
        }
        add(new Label("title"));
        add(new LabelHideableOnNull("qiwi"));
        add(new LabelHideableOnNull("wmr"));
        add(new LabelHideableOnNull("wmu"));
        add(new LabelHideableOnNull("wmz"));
        add(new LabelHideableOnNull("wme"));
        add(new LabelHideableOnNull("wmb"));
        add(new LabelHideableOnNull("wmg"));
        add(new LabelHideableOnNull("wmk"));
        add(new LabelHideableOnNull("wmx"));
        add(new LabelHideableOnNull("yandex"));
        add(new AbstractLink("paypal")
        {
            @Override
            public boolean isVisible()
            {
                return !org.apache.wicket.util.string.Strings.isEmpty(getDefaultModelObjectAsString());
            }

            @Override
            protected void onComponentTag(ComponentTag tag)
            {
                super.onComponentTag(tag);
                Object hrefValue = this.getDefaultModelObject();
                if (hrefValue != null)
                {
                    String url = hrefValue.toString();
                    if (!url.contains(":"))
                    {
                        if (url.contains("@"))
                        {
                            return;
                        }
                        else if (url.contains("/"))
                        {
                            url = "https://" + url;
                        }
                        else
                        {
                            url = "https://paypal.me/" + url;
                        }
                    }
                    tag.put("href", url);
                }
            }

            @Override
            public IModel<?> getBody()
            {
                return this.getDefaultModel();
            }
        });
        add(new LabelHideableOnNull("card")
        {
            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type)
            {
                return type.equals(String.class) ? (IConverter<C>) new CreditCardConverter() : super.getConverter(type);
            }
        });
        add(new LabelHideableOnNull("bitcoin"));

        ArrayList<NameValuePair> yandexButtonParams = new ArrayList<>();
        yandexButtonParams.add(new BasicNameValuePair("account", requisite.getYandex()));
        yandexButtonParams.add(new BasicNameValuePair("quickpay", "small"));
        yandexButtonParams.add(new BasicNameValuePair("button-text", "06"));
        yandexButtonParams.add(new BasicNameValuePair("button-size", "s"));
        yandexButtonParams.add(new BasicNameValuePair("button-color", "orange"));
        yandexButtonParams.add(new BasicNameValuePair("targets", requisite.getTitle()));
        yandexButtonParams.add(new BasicNameValuePair("default-sum", "50"));

        try
        {
            URIBuilder yandexMoneyButtonURI = new URIBuilder("https://money.yandex.ru/quickpay/button-widget");
            yandexMoneyButtonURI.addParameters(yandexButtonParams);
            yandexMoneyButtonURI.addParameter("yamoney-payment-type", "on");
            add(new WebMarkupContainer("yandexMoneyButton")
                    .setVisible(requisite.isShowYandexMoneyButton() && !Strings.isEmpty(requisite.getYandex()))
                    .add(AttributeModifier.replace("src", yandexMoneyButtonURI.toString())));

            URIBuilder yandexCardButtonURI = new URIBuilder("https://money.yandex.ru/quickpay/button-widget");
            yandexCardButtonURI.addParameters(yandexButtonParams);
            yandexCardButtonURI.addParameter("any-card-payment-type", "on");
            add(new WebMarkupContainer("yandexCardButton")
                    .setVisible(requisite.isShowYandexCardButton())
                    .add(AttributeModifier.replace("src", yandexCardButtonURI.toString())));

            URIBuilder yandexMobileButtonURI = new URIBuilder("https://money.yandex.ru/quickpay/button-widget");
            yandexMobileButtonURI.addParameters(yandexButtonParams);
            yandexMobileButtonURI.addParameter("mobile-payment-type", "on");
            add(new WebMarkupContainer("yandexMobileButton")
                    .setVisible(requisite.isShowYandexMobileButton())
                    .add(AttributeModifier.replace("src", yandexMobileButtonURI.toString())));
        }
        catch (URISyntaxException e)
        {
            LOG.error(e.getMessage(), e);
        }

        add(new WebMarkupContainer("paypalButton")
                .setVisible(!Strings.isEmpty(requisite.getPaypalButtonId()))
                .add(AttributeModifier.replace("value", requisite.getPaypalButtonId())));
    }

    private static final Logger LOG = LoggerFactory.getLogger(RequisitesSidebarModule.class);
}
