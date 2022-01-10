package io.geewit.weixin.api.common.utils;

import io.geewit.weixin.api.common.exception.WxApiException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.geewit.weixin.api.common.APIs;
import io.geewit.weixin.api.common.model.API;
import io.geewit.weixin.api.common.model.CommonRequest;
import io.geewit.weixin.api.common.model.CommonResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.*;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class APIUtils {
    private final static Logger logger = LoggerFactory.getLogger(APIUtils.class);

    /**
     * AccessToken 缓存
     */
    private final static LoadingCache<APIs.AccessToken.Request, APIs.AccessToken.Response> accessTokenCache = CacheBuilder.newBuilder()
            .expireAfterWrite(3600L, TimeUnit.SECONDS)
            .build(new CacheLoader<APIs.AccessToken.Request, APIs.AccessToken.Response>() {
        @Override
        public APIs.AccessToken.Response load(APIs.AccessToken.Request request) {
            return getAccessTokenPersisted(request);
        }
    });

    public static <REQ extends CommonRequest, RES extends CommonResponse> RestTemplate ofRestTemplate(API<REQ, RES> api) {
        RestTemplate restTemplate = new RestTemplate(Collections.singletonList(api.getResponse().getResponseConverter()));
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
        APIs.AccessToken.Response response = APIs.AccessToken.INVOKER.invoke(request);
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
