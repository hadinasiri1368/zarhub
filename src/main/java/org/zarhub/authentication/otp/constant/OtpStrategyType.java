package org.zarhub.authentication.otp.constant;

import java.util.Arrays;
import java.util.Objects;

public enum OtpStrategyType {
    SMS(1, "پیامک"),
    EMAIL(2, "ایمیل"),
    PHYSICAL(3, "سخت افزاری");

    private final Integer id;
    private final String title;

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    OtpStrategyType(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public static OtpStrategyType getItemById(Integer id) {
        return Arrays.stream(values())
                .filter(item -> Objects.equals(item.getId(), id))
                .findFirst()
                .orElse(null);
    }
}
