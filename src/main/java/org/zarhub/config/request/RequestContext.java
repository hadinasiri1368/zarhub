package org.zarhub.config.request;

import org.zarhub.common.Utils;
import org.zarhub.model.Users;

public class RequestContext {
    private static final ThreadLocal<String> uuid = new ThreadLocal<>();
    private static final ThreadLocal<Users> user = new ThreadLocal<>();
    private static final ThreadLocal<String> token = new ThreadLocal<>();

    public static void setUuid(String uuidValue) {
        uuid.set(uuidValue);
    }

    public static void setUser(Users userValue) {
        user.set(userValue);
    }

    public static void setToken(String tokenValue) {
        token.set(tokenValue);
    }

    public static String getUuid() {
        return uuid.get();
    }

    public static Users getUser() {
        return Utils.isNull(user.get()) ? null : user.get();
    }

    public static Long getUserId() {
        return Utils.isNull(getUser()) ? null : getUser().getId();
    }

    public static String getToken() {
        return Utils.isNull(token.get()) ? null : token.get();
    }

    public static String getTokenId() {
        return Utils.isNull(getUserId()) ? null : getUserId().toString();
    }

    public static void clear() {
        uuid.remove();
        user.remove();
        token.remove();
    }
}

