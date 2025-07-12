package org.zarhub.authentication.otp.constant;

import java.util.Arrays;
import java.util.Objects;

public enum OtpConsumerType {
    USER(0, "user"),
    CUSTOMER(1, "customer");

    private final Integer id;
    private final String title;

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    OtpConsumerType(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public static OtpConsumerType getItemById(Integer id) {
        return Arrays.stream(values())
                .filter(item -> Objects.equals(item.getId(), id))
                .findFirst()
                .orElse(null);
    }
}
