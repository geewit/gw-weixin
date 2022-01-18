package io.geewit.weixin.api.common.utils;

import com.google.common.hash.Hashing;
import io.geewit.weixin.api.common.exception.WxApiException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.geewit.weixin.api.common.model.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 请求构造工具类
 * @author geewit
 */
public class AccessTokenUtils {
    private final static Logger logger = LoggerFactory.getLogger(AccessTokenUtils.class);

    /**
     * AccessToken 缓存
     */
    private final static Map<Class<AccessTokenInvoker<? extends AccessTokenRequest, ? extends AccessTokenResponse>>, LoadingCache<? extends AccessTokenRequest, ? extends AccessTokenResponse>> accessTokenCache = new ConcurrentHashMap<>();

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

    /**
     * 从持久化文件读取 access_token
     * @param tempFile 持久化文件
     * @return access_token
     */
    private synchronized static <RES extends AccessTokenResponse> RES readPersistedAccessToken(Path tempFile) {
        RES response = null;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(Files.newInputStream(tempFile, StandardOpenOption.READ));
            response = (RES) ois.readObject();
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

    /**
     * 持久化 access_token 到文件
     * @param response
     * @param tempFile
     * @param <RES>
     */
    private synchronized static <RES extends AccessTokenResponse> void persistAccessToken(RES response, Path tempFile) {
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

    /**
     * 读取 access_token 并更新持久化到文件, 如果文件中的 access_token 不存在或过期则调用接口并保存/更新文件
     * @param invoker AccessTokenInvoker
     * @return RES extends AccessTokenResponse
     */
    private static <REQ extends AccessTokenRequest, RES extends AccessTokenResponse> RES getAccessTokenFromPersistence(AccessTokenInvoker<REQ, RES> invoker) {
        String tempFilepath = StringUtils.appendIfMissing(tempPath, File.separator) + "access_token_cache" + File.separator + Hashing.sha1().hashString(invoker.getClass().getSimpleName(), StandardCharsets.UTF_8) + File.separator + Hashing.sha1().hashString(invoker.getName(), StandardCharsets.UTF_8);
        logger.debug("tempPath : {}", tempFilepath);
        Path tempPath = Paths.get(tempFilepath);
        if (Files.notExists(tempPath)) {
            try {
                logger.debug("createDirectories : " + tempPath);
                Files.createDirectories(tempPath);
            } catch (IOException e) {
                logger.warn(e.getMessage());
                return null;
            }
        }
        Path tempFile = Paths.get(tempFilepath, invoker.getRequestParam().getAppId() + ".dat");
        logger.debug("tempFile = {}", tempFile);
        RES accessTokenResponse = readPersistedAccessToken(tempFile);
        logger.debug("accessTokenResponse : {}", accessTokenResponse);
        boolean expired;
        if (accessTokenResponse == null) {
            logger.debug("accessToken == null, expired = true");
            expired = true;
        } else {
            expired = accessTokenResponse.expired();
        }
        logger.debug("expired : {}", expired);
        if (expired) {
            accessTokenResponse = invoker.invoke();
            if(accessTokenResponse != null) {
                persistAccessToken(accessTokenResponse, tempFile);
            }
        }
        return accessTokenResponse;
    }

    /**
     * 从缓存获取 access_token
     * @param invoker AccessTokenInvoker
     * @return AccessTokenResponse
     */
    public static <REQ extends AccessTokenRequest, RES extends AccessTokenResponse> RES getAccessTokenCached(AccessTokenInvoker<REQ, RES> invoker) {
        if (invoker.getRequestParam() == null) {
            throw new WxApiException("获取accessToken的请求参数不能为null");
        }

        try {
            LoadingCache<REQ, RES> loadingCache = (LoadingCache<REQ, RES>) accessTokenCache.get(invoker.getClass());

            if (loadingCache == null) {
                loadingCache = CacheBuilder.newBuilder()
                        .expireAfterWrite(invoker.getExpiredSeconds(), TimeUnit.SECONDS)
                        .build(new CacheLoader<REQ, RES>() {
                            @Override
                            public RES load(REQ request) {
                                return getAccessTokenFromPersistence(invoker);
                            }
                        });
            }
            RES response = loadingCache.get(invoker.getRequestParam());
            logger.debug("accessTokenResponse from cache : {}", response);
            boolean expired = response.expired();
            logger.debug("accessToken: {}, expired : {}", response.getAccessToken(), expired);
            if (expired) {
                response = getAccessTokenFromPersistence(invoker);
                logger.debug("accessTokenResponse from persisted = {}", response);
                if (response != null) {
                    loadingCache.put(invoker.getRequestParam(), response);
                }
            }
            return response;
        } catch (Exception e) {
            String message = "accessToken获取失败, " + e.getMessage();
            logger.warn(e.getMessage(), e);
            throw new WxApiException(message);
        }
    }

}
