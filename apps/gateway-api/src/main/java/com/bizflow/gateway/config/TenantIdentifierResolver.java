package com.bizflow.gateway.config;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Component
public class TenantIdentifierResolver {
    public static final String DEFAULT_TENANT_ID = "11111111-1111-1111-1111-111111111111";

    private static final Map<String, String> TENANT_ALIASES = Map.of(
            "demo", DEFAULT_TENANT_ID,
            "demo-tenant", DEFAULT_TENANT_ID
    );

    public String resolveRequired(String tenantId) {
        if (!StringUtils.hasText(tenantId)) {
            return DEFAULT_TENANT_ID;
        }

        String normalized = tenantId.trim();
        String alias = TENANT_ALIASES.get(normalized.toLowerCase(Locale.ROOT));
        if (alias != null) {
            return alias;
        }

        try {
            return UUID.fromString(normalized).toString();
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "tenantId must be a UUID or a supported alias such as demo-tenant"
            );
        }
    }

    public String defaultTenantId() {
        return DEFAULT_TENANT_ID;
    }
}
