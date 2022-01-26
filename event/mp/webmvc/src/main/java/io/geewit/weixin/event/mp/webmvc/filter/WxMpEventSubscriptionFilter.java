package io.geewit.weixin.event.mp.webmvc.filter;

import io.geewit.weixin.event.mp.common.model.Signature;
import io.geewit.weixin.event.mp.common.service.IAppTokenService;
import io.geewit.weixin.event.mp.webmvc.context.EventContext;
import io.geewit.weixin.event.mp.webmvc.context.MpEvent;
import io.geewit.weixin.event.mp.webmvc.context.WxMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.geewit.weixin.event.mp.webmvc.config.WxMpEventFilterProperties.APPID_PARAM_NAME;

/**
 * 接收微信公众号事件 监听 Filter
 */
@Slf4j
public class WxMpEventSubscriptionFilter implements Filter {

    private final String urlPatterns;
    private final IAppTokenService appTokenService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public WxMpEventSubscriptionFilter(String urlPatterns, IAppTokenService appTokenService, ApplicationEventPublisher applicationEventPublisher) {
        this.urlPatterns = urlPatterns;
        this.appTokenService = appTokenService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        PathContainer prePathContainer = PathContainer.parsePath(httpServletRequest.getRequestURI());
        PathPattern prePathPattern = PathPatternParser.defaultInstance.parse(this.urlPatterns);
        PathPattern.PathMatchInfo prePathMatchInfo = prePathPattern.matchAndExtract(prePathContainer);
        if (prePathMatchInfo == null) {
            return;
        }
        Map<String, String> prePathVariables = prePathMatchInfo.getUriVariables();
        String appId = prePathVariables.get(APPID_PARAM_NAME);

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        if (HttpMethod.GET.matches(httpServletRequest.getMethod())) {
            this.doGet(appId, httpServletRequest, httpServletResponse);
        } else if (HttpMethod.POST.matches(httpServletRequest.getMethod())) {
            this.doPost(appId, httpServletRequest, httpServletResponse);
        }

    }

    private void doGet(String appId, HttpServletRequest request, HttpServletResponse response) {
        String echostr = request.getParameter("echostr");
        boolean isAccessable = this.isAccessable(appId, request, true, echostr);

        if (isAccessable) {
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("text/plain;charset=UTF-8");
            try (PrintWriter writer = response.getWriter()) {
                writer.write(echostr);
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
            }
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void doPost(String appId, HttpServletRequest request, HttpServletResponse response) {
        boolean isAccessable = this.isAccessable(appId, request, false, null);
        if (isAccessable) {
            try (BufferedReader reader = request.getReader()) {
                String xml = reader.lines()
                        .parallel().collect(Collectors.joining("\n"));
                WxMessage wxMessage = WxMessage.parse(xml, Charset.forName(request.getCharacterEncoding()));
                MpEvent event = MpEvent.builder()
                        .context(EventContext.builder()
                                .appId(appId)
                                .requestXml(xml)
                                .requestMessage(wxMessage)
                                .build())
                        .build();
                applicationEventPublisher.publishEvent(event);

                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setContentType("text/xml;charset=UTF-8");
                int status;
                try (PrintWriter writer = response.getWriter()) {
                    String responseXml = event.getContext().getResponseXml();
                    if (responseXml == null) {
                        status = HttpServletResponse.SC_BAD_REQUEST;
                        writer.write("有异常发生");
                    } else {
                        status = HttpServletResponse.SC_OK;
                        writer.write(responseXml);
                    }
                    response.setStatus(status);
                } catch (IOException e) {
                    log.warn(e.getMessage(), e);
                }

            } catch (IOException e) {
                log.warn(e.getMessage(), e);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private boolean isAccessable(String appId, ServletRequest request, boolean checkEchostr, String echostr) {
        boolean isAccessable = StringUtils.isNotBlank(appId);
        String token = null;
        if (isAccessable) {
            Optional<String> tokenOptional = appTokenService.getTokenByAppId(appId);
            if (tokenOptional.isPresent()) {
                token = tokenOptional.get();
            } else {
                isAccessable = false;
            }

        }
        String timestamp = request.getParameter("timestamp");
        if (isAccessable) {
            isAccessable = StringUtils.isNotBlank(timestamp);
        }
        String nonce = request.getParameter("nonce");
        if (isAccessable) {
            isAccessable = StringUtils.isNotBlank(nonce);
        }
        String signature = request.getParameter("signature");
        if (isAccessable) {
            isAccessable = StringUtils.isNotBlank(signature);
        }
        if (checkEchostr) {
            if (isAccessable) {
                isAccessable = StringUtils.isNotBlank(echostr);
            }
        }

        if (isAccessable) {
            isAccessable = Signature.builder()
                    .token(token)
                    .timestamp(timestamp)
                    .nonce(nonce)
                    .build()
                    .isAccessable(signature);
        }
        return isAccessable;
    }
}
