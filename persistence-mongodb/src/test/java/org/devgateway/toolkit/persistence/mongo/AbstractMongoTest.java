package org.devgateway.toolkit.persistence.mongo;

import org.devgateway.toolkit.persistence.mongo.spring.MongoPersistenceApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Configuration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { MongoPersistenceApplication.class })
@ActiveProfiles("integration")
@TestPropertySource("classpath:test.properties")
@WebIntegrationTest
/**
 * Superclass for all integration tests that use MongoDB.
 * Adding this as a superclass will ensure the tests are run with the "integration" spring profile.
 * This will enable a special embedded mongodb server that can be used just during the test. 
 *
 * @author mihai
 *
 */
public abstract class AbstractMongoTest {

}
