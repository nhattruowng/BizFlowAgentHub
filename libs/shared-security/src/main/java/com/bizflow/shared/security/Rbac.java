package com.bizflow.shared.security;

import java.util.Set;

public final class Rbac {
    private Rbac() {}

    public static boolean canApprove(String role) {
        return Set.of("ADMIN", "SUPERVISOR").contains(role);
    }

    public static boolean canExecuteWriteTool(String role) {
        return Set.of("ADMIN", "OPERATOR").contains(role);
    }
}
