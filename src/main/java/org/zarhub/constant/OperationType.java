package org.zarhub.constant;

public enum OperationType {
    INSERT("insert"),
    UPDATE("update"),
    DELETE("delete"),
    SELECT("select");

    private final String value;

    OperationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
