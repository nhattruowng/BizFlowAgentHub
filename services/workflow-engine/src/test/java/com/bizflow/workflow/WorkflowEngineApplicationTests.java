package com.bizflow.workflow;

import org.junit.jupiter.api.Test;
import com.bizflow.workflow.engine.WorkflowRepository;
import com.bizflow.workflow.engine.WorkflowRunRepository;
import com.bizflow.workflow.engine.WorkflowStepRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration", "otel.sdk.disabled=true"})
class WorkflowEngineApplicationTests {
    @MockBean
    private WorkflowRepository workflowRepository;

    @MockBean
    private WorkflowRunRepository workflowRunRepository;

    @MockBean
    private WorkflowStepRepository workflowStepRepository;

    @Test
    void contextLoads() {}
}

