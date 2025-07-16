package org.zarhub.baseInfo.customer.constant;

import java.util.Arrays;
import java.util.Objects;

public enum UserGroupType {
    CUSTOMER_GROUP(1L, "گروه مشتریان"),
    STORE_MANAGER_GROUP(2L, "گروه مدیران فروشگاه"),
    STORE_CLERK_GROUP(3L, "گروه کارمند فروشگاه");

    private final Long id;
    private final String title;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    UserGroupType(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public static UserGroupType getItemById(Long id) {
        return Arrays.stream(values())
                .filter(item -> Objects.equals(item.getId(), id))
                .findFirst()
                .orElse(null);
    }
}
