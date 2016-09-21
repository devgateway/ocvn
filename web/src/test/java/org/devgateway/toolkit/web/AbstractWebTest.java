package org.devgateway.toolkit.web;

import org.apache.log4j.Logger;
import org.devgateway.toolkit.web.spring.WebApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("integration")
@SpringBootTest(classes = { WebApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
public abstract class AbstractWebTest {
	protected static Logger logger = Logger.getLogger(AbstractWebTest.class);

}
