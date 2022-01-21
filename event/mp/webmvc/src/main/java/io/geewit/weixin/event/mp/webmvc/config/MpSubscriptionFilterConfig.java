package io.geewit.weixin.event.mp.webmvc.config;


import io.geewit.weixin.event.mp.common.service.IAppTokenService;
import io.geewit.weixin.event.mp.webmvc.filter.WxMpEventSubscriptionFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.geewit.weixin.event.mp.webmvc.config.WxMpEventFilterProperties.APPID_PARAM_NAME;

/**
 * 注册 WxMpEventSubscriptionFilter
 * @author geewit
 * @since 2022-01-20
 */
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Configuration(value = "mpSubscriptionFilterConfig")
@ConditionalOnExpression("${io.geewit.weixin.event.mp.filter.enable:true}")
@EnableConfigurationProperties({WxMpEventFilterProperties.class})
public class MpSubscriptionFilterConfig implements ApplicationContextAware, ApplicationEventPublisherAware {

    private final WxMpEventFilterProperties wxMpEventFilterProperties;

    private ApplicationContext applicationContext;

    private ApplicationEventPublisher applicationEventPublisher;

    public MpSubscriptionFilterConfig(WxMpEventFilterProperties wxMpEventFilterProperties) {
        this.wxMpEventFilterProperties = wxMpEventFilterProperties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * 注册 WxMpEventSubscriptionFilter
     */
    @Bean
    public FilterRegistrationBean<WxMpEventSubscriptionFilter> wxMpEventSubscriptionFilter() {
        FilterRegistrationBean<WxMpEventSubscriptionFilter> registrationBean
                = new FilterRegistrationBean<>();
        String pathPrefix = StringUtils.defaultIfEmpty(wxMpEventFilterProperties.getListenerPath(), "/weixin/mp/lisenters");
        String path = pathPrefix + "/{" + APPID_PARAM_NAME + "}";
        String pathPattern = pathPrefix + "/*";
        String urlPath = StringUtils.appendIfMissing(wxMpEventFilterProperties.getContextPath(), path);
        String urlPatterns = StringUtils.appendIfMissing(wxMpEventFilterProperties.getContextPath(), pathPattern);
        IAppTokenService appTokenService = applicationContext.getBean(IAppTokenService.class);
        registrationBean.setFilter(new WxMpEventSubscriptionFilter(urlPath, appTokenService, applicationEventPublisher));
        registrationBean.setName(StringUtils.defaultIfBlank(wxMpEventFilterProperties.getFilterName(), WxMpEventSubscriptionFilter.class.getSimpleName()));
        registrationBean.addUrlPatterns(urlPatterns);
        if (wxMpEventFilterProperties.getOrder() != null) {
            registrationBean.setOrder(wxMpEventFilterProperties.getOrder());
        }

        return registrationBean;
    }
}
