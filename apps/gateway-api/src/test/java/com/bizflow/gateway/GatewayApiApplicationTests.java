package com.bizflow.gateway;

import org.junit.jupiter.api.Test;
import com.bizflow.gateway.agents.AgentRepository;
import com.bizflow.gateway.tasks.TaskInputRepository;
import com.bizflow.gateway.tasks.TaskRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration", "otel.sdk.disabled=true"})
class GatewayApiApplicationTests {
    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private TaskInputRepository taskInputRepository;

    @MockBean
    private AgentRepository agentRepository;

    @Test
    void contextLoads() {}
}

