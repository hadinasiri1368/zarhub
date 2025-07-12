package org.zarhub.config.authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.zarhub.common.JwtUtil;
import org.zarhub.exception.AuthenticationExceptionType;
import org.zarhub.exception.ZarHubException;
import org.zarhub.model.Users;

import java.util.concurrent.TimeUnit;

@Service
public class AuthenticationTokenServiceImpl implements TokenService<String, String> {
    @Value("${jwt.expirationMinutes}")
    private long expirationMinutes;

    private final RedisTemplate<String, Object> redisTemplate;
    private final String key = "_login_token";

    public AuthenticationTokenServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String generateToken(Users user) throws Exception {
        if (redisTemplate.hasKey(getId(user.getId().toString())))
            return redisTemplate.opsForValue().get(getId(user.getId().toString())).toString();
        String token = JwtUtil.createToken(user);
        redisTemplate.opsForValue().set(getId(user.getId().toString()), token, expirationMinutes, TimeUnit.MINUTES);
        redisTemplate.opsForHash().put(key, token, user.getId());
        redisTemplate.expire(key, expirationMinutes, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public boolean exists(String value) {
        return redisTemplate.opsForHash().hasKey(key, value);
    }

    @Override
    public void removeTokenById(Long userId, String token) {
        if (!redisTemplate.hasKey(getId(userId.toString())))
            throw new ZarHubException(AuthenticationExceptionType.USER_HAS_NOT_TOKEN);
        String oldToken = redisTemplate.opsForValue().get(getId(userId.toString())).toString();
        if (!oldToken.equals(token))
            throw new ZarHubException(AuthenticationExceptionType.TOKEN_IS_NULL);
        redisTemplate.delete(getId(userId.toString()));
        redisTemplate.opsForHash().delete(key, oldToken);
    }

    @Override
    public Users getTokenData(String id, String value) throws ZarHubException {
        if (!exists(value) || !JwtUtil.validateToken(value))
            throw new ZarHubException(AuthenticationExceptionType.TOKEN_IS_NULL);
        return (Users) JwtUtil.getTokenData(value);
    }

    private String getId(String id) {
        return id + key;
    }
}
