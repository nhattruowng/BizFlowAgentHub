package com.bizflow.audit;

import org.junit.jupiter.api.Test;
import com.bizflow.audit.core.AuditLogRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration", "otel.sdk.disabled=true"})
class AuditServiceApplicationTests {
    @MockBean
    private AuditLogRepository auditLogRepository;

    @Test
    void contextLoads() {}
}

