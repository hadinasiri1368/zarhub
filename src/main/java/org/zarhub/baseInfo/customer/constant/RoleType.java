package org.zarhub.baseInfo.customer.constant;

import java.util.Arrays;
import java.util.Objects;

public enum RoleType {
    CUSTOMER(1, "customer"),
    STORE_MANAGER(2, "store_manager"),
    STORE_CLERK(3, "store_clerk");

    private final Integer id;
    private final String title;

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    RoleType(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public static RoleType getItemById(Integer id) {
        return Arrays.stream(values())
                .filter(item -> Objects.equals(item.getId(), id))
                .findFirst()
                .orElse(null);
    }
}
