package com.bizflow.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TenantContextFilter implements WebFilter {
    private static final String TENANT_ID_HEADER = "X-Tenant-Id";
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    private final TenantIdentifierResolver tenantIdentifierResolver;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var mutatedRequest = exchange.getRequest().mutate()
                .headers(headers -> {
                    applyDefault(headers.getFirst(TENANT_ID_HEADER), TENANT_ID_HEADER, tenantIdentifierResolver.defaultTenantId(), headers);
                    applyDefault(headers.getFirst(USER_ID_HEADER), USER_ID_HEADER, "demo-user", headers);
                    applyDefault(headers.getFirst(USER_ROLE_HEADER), USER_ROLE_HEADER, "ADMIN", headers);
                })
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private void applyDefault(String currentValue, String headerName, String defaultValue,
                              org.springframework.http.HttpHeaders headers) {
        if (!StringUtils.hasText(currentValue)) {
            headers.set(headerName, defaultValue);
        }
    }
}
