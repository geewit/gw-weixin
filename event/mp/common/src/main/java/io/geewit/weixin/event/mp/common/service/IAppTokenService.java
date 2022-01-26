package io.geewit.weixin.event.mp.common.service;

import java.util.Optional;

/**
 * @author geewit
 */
public interface IAppTokenService {

    Optional<String> getTokenByAppId(String appId);

    Optional<String> getSecretByAppId(String appId);
}
