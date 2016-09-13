package org.devgateway.toolkit.web;

import org.devgateway.toolkit.web.spring.WebApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Configuration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { WebApplication.class })
@ActiveProfiles("integration")
@TestPropertySource("classpath:test.properties")
@WebIntegrationTest
public abstract class AbstractWebTest {
    protected boolean testDataInitialized;
}
