package com.bizflow.gateway.config;

import com.bizflow.shared.security.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantContextFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String tenantId = request.getHeader("X-Tenant-Id");
        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-Role");
        if (tenantId == null || tenantId.isBlank()) {
            tenantId = "demo-tenant";
        }
        if (userId == null || userId.isBlank()) {
            userId = "demo-user";
        }
        if (role == null || role.isBlank()) {
            role = "ADMIN";
        }
        TenantContext.set(tenantId, userId, role);
        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
