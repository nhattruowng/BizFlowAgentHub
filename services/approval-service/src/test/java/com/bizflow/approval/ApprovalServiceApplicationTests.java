package com.bizflow.approval;

import org.junit.jupiter.api.Test;
import com.bizflow.approval.core.ApprovalRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration", "otel.sdk.disabled=true"})
class ApprovalServiceApplicationTests {
    @MockBean
    private ApprovalRepository approvalRepository;

    @Test
    void contextLoads() {}
}

