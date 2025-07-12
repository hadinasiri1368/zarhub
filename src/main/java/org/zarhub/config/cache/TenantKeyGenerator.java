package org.zarhub.config.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;
import org.zarhub.common.Utils;

import java.lang.reflect.Method;

@Component
@Slf4j
public class TenantKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        String key = getKey(params);
        return key;
    }

    private String getKey(Object... params) {
        if (params != null && params.length > 0) {
            return Utils.getClassName(params[0]);
        }
        log.info("tenantKeyGenerator getKey exception : params is null or empty");
        return null;
    }
}

