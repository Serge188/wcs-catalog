package ru.wcscatalog.core.model;

import java.util.ArrayList;
import java.util.List;

public enum Role {
    USER,
    ADMIN;

    public static Role getRoleByName(String roleName) {
        if (roleName.equals(USER.name())) {
            return USER;
        }
        if (roleName.equals(ADMIN.name())) {
            return ADMIN;
        }
        return null;
    }
}
