package com.bizflow.shared.security;

public final class TenantContext {
    private static final ThreadLocal<String> TENANT = new ThreadLocal<>();
    private static final ThreadLocal<String> USER = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE = new ThreadLocal<>();

    private TenantContext() {}

    public static void set(String tenantId, String userId, String role) {
        TENANT.set(tenantId);
        USER.set(userId);
        ROLE.set(role);
    }

    public static String tenantId() {
        return TENANT.get();
    }

    public static String userId() {
        return USER.get();
    }

    public static String role() {
        return ROLE.get();
    }

    public static void clear() {
        TENANT.remove();
        USER.remove();
        ROLE.remove();
    }
}
