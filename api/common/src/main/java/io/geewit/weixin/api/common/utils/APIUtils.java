package io.geewit.weixin.api.common.utils;

import io.geewit.core.utils.reflection.BeanUtils;
import io.geewit.weixin.api.common.exception.WxApiException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.geewit.weixin.api.common.APIs;
import io.geewit.weixin.api.common.model.API;
import io.geewit.weixin.api.common.model.CommonResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class APIUtils {
    private final static Logger logger = LoggerFactory.getLogger(APIUtils.class);

    /**
     * AccessToken 缓存
     */
    private final static LoadingCache<APIs.AccessToken.Request, APIs.AccessToken.Response> accessTokenCache = CacheBuilder.newBuilder()/*.expireAfterWrite(7000L, TimeUnit.SECONDS)*/.build(new CacheLoader<APIs.AccessToken.Request, APIs.AccessToken.Response>() {
        @Override
        public APIs.AccessToken.Response load(APIs.AccessToken.Request request) {
            return getAccessTokenPersisted(request);
        }
    });

    public static <RES extends CommonResponse> RestTemplate ofRestTemplate(Class<RES> responseType) {
        AbstractHttpMessageConverter responseTypeConverter = new AbstractHttpMessageConverter<RES>() {
            private final ParameterizedTypeReference<Map<String, Object>> STRING_OBJECT_MAP = new ParameterizedTypeReference<Map<String, Object>>() {
            };

            @Override
            protected boolean supports(Class<?> clazz) {
                return responseType.isAssignableFrom(clazz);
            }

            @Override
            protected RES readInternal(Class<? extends RES> clazz, HttpInputMessage inputMessage) throws HttpMessageNotReadableException {
                try {
                    Map<String, Object> responseParameters = (Map<String, Object>) new MappingJackson2HttpMessageConverter()
                            .read(STRING_OBJECT_MAP.getType(), null, inputMessage);
                    Object errcode = responseParameters.get(API.ERRCODE);
                    if (Objects.nonNull(errcode)) {
                        throw new IllegalArgumentException("errcode：" + errcode + " errmsg：" + responseParameters.get("errmsg"));
                    }
                    Map<String, Object> source = responseParameters
                            .entrySet()
                            .stream()
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    RES instance = null;
                    try {
                        instance = clazz.newInstance();
                        MapToPojoUtils.mapToPojo(source, instance);
                    } catch (InstantiationException | IllegalAccessException e) {
                        logger.info(e.getMessage(), e);
                    }
                    return instance;
                } catch (Exception ex) {
                    throw new HttpMessageNotReadableException(
                            "An error occurred reading the OAuth 2.0 Access Token Response: " + ex.getMessage(), ex,
                            inputMessage);
                }
            }

            @Override
            protected void writeInternal(RES res, HttpOutputMessage outputMessage) throws HttpMessageNotWritableException {
            }
        };
        responseTypeConverter.setDefaultCharset(StandardCharsets.UTF_8);
        responseTypeConverter.setSupportedMediaTypes(
                Stream.of(
                        MediaType.APPLICATION_JSON_UTF8,
                        MediaType.TEXT_PLAIN,
                        MediaType.TEXT_HTML,
                        new MediaType(MediaType.APPLICATION_JSON.getType(), "*+json")
                ).collect(Collectors.toList()));
        RestTemplate restTemplate = new RestTemplate(
                Collections.singletonList(responseTypeConverter));
        return restTemplate;
    }

    public static final APIs.AccessToken.Request FETCH_ACCESS_TOKEN_REQUEST = new APIs.AccessToken.Request();

    private static final String tempPath = getTempPath();

    private static String getTempPath() {
        String tempPath = SystemUtils.JAVA_IO_TMPDIR;
        if (StringUtils.isNoneBlank(tempPath)) {
            return tempPath;
        }
        tempPath = SystemUtils.JAVA_HOME;
        if (StringUtils.isNoneBlank(tempPath)) {
            tempPath = tempPath + File.separator + "temp";
            return tempPath;
        }
        if(SystemUtils.IS_OS_WINDOWS) {
            return "C:\\temp";
        } else {
            return "~/temp";
        }
    }

    private synchronized static APIs.AccessToken.Response readPersistedAccessToken(Path tempFile) {
        APIs.AccessToken.Response response = null;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(Files.newInputStream(tempFile, StandardOpenOption.READ));
            response = (APIs.AccessToken.Response) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.warn(e.getMessage());
        } finally {
            if(ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    logger.warn(e.getMessage());
                }
            }
        }
        return response;
    }

    private synchronized static void persistedAccessToken(APIs.AccessToken.Response response, Path tempFile) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(Files.newOutputStream(tempFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
            oos.writeObject(response);
        } catch (IOException e) {
            logger.warn(e.getMessage());
        } finally {
            if(oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    logger.warn(e.getMessage());
                }
            }
        }
    }

    private static APIs.AccessToken.Response getAccessToken(APIs.AccessToken.Request request) throws IllegalArgumentException {
        RestTemplate restTemplate = ofRestTemplate(APIs.AccessToken.Response.class);
        APIs.AccessToken.Response response = APIs.AccessToken.FETCH_ACCESS_TOKEN.invoke(restTemplate, request);
        return response;
    }

    private static APIs.AccessToken.Response getAccessTokenPersisted(APIs.AccessToken.Request request) {

        String tempFilepath = tempPath + File.separator + "access_token_cache";
        Path tempPath = Paths.get(tempFilepath);
        logger.debug("tempPath : " + tempPath);
        if (Files.notExists(tempPath)) {
            try {
                logger.debug("createDirectories : " + tempPath);
                Files.createDirectories(tempPath);
            } catch (IOException e) {
                logger.warn(e.getMessage());
                return null;
            }
        }
        Path tempFile = Paths.get(tempFilepath, request.getAppId() + ".dat");
        logger.debug("tempFile = " + tempFile);
        APIs.AccessToken.Response accessToken = readPersistedAccessToken(tempFile);
        logger.debug("accessToken : " + accessToken);
        boolean expired;
        if (accessToken == null) {
            logger.debug("accessToken == null, expired = true");
            expired = true;
        } else {
            expired = accessToken.expired();
        }
        logger.debug("expired : " + expired);
        if (expired) {
            accessToken = getAccessToken(request);
            if(accessToken != null) {
                persistedAccessToken(accessToken, tempFile);
            }
        }
        return accessToken;
    }

    public static APIs.AccessToken.Response getAccessTokenCached(APIs.AccessToken.Request request) {
        if (request == null) {
            logger.debug("request == null, return null");
            throw new WxApiException("获取accessToken的参数不能为null");
        }

        try {
            APIs.AccessToken.Response accessToken = accessTokenCache.get(request);
            logger.debug("accessToken from cache : " + accessToken);
            boolean expired;
            expired = accessToken.expired();
            logger.debug("accessToken: {}, expired : {}", accessToken.getAccessToken(), expired);
            if (expired) {
                accessToken = getAccessTokenPersisted(request);
                logger.debug("accessToken from persisted = {}", accessToken);
                if (accessToken != null) {
                    accessTokenCache.put(request, accessToken);
                }
            }
            return accessToken;
        } catch (Exception e) {
            String message = "accessToken获取失败, " + e.getMessage();
            logger.warn(e.getMessage(), e);
            throw new WxApiException(message);
        }
    }

}
