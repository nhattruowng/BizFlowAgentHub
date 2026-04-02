package com.bizflow.knowledge;

import org.junit.jupiter.api.Test;
import com.bizflow.knowledge.core.KnowledgeChunkRepository;
import com.bizflow.knowledge.core.KnowledgeDocRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration", "otel.sdk.disabled=true"})
class KnowledgeServiceApplicationTests {
    @MockBean
    private KnowledgeDocRepository knowledgeDocRepository;

    @MockBean
    private KnowledgeChunkRepository knowledgeChunkRepository;

    @Test
    void contextLoads() {}
}

