package com.greem4.springmedicines.domain;

public enum UserAction {
    ENABLE,
    DISABLE;

    public static UserAction fromString(String action) {
        try {
            return UserAction.valueOf(action.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid action: " + action);
        }
    }
}

