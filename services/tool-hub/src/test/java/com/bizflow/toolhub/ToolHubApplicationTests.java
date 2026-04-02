package com.bizflow.toolhub;

import org.junit.jupiter.api.Test;
import com.bizflow.toolhub.tools.ToolCallRepository;
import com.bizflow.toolhub.tools.ToolRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration",
        "otel.sdk.disabled=true",
        "toolhub.seed.enabled=false"
})
class ToolHubApplicationTests {
    @MockBean
    private ToolRepository toolRepository;

    @MockBean
    private ToolCallRepository toolCallRepository;

    @Test
    void contextLoads() {}
}

