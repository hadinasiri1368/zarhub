package org.zarhub.config.authentication;

import org.zarhub.exception.ZarHubException;
import org.zarhub.model.Users;

public interface TokenService<K, V> {
    String generateToken(Users user) throws Exception;

    boolean exists(V value);

    void removeTokenById(Long userId, String token);

    Users getTokenData(K id, V value) throws ZarHubException;
}
