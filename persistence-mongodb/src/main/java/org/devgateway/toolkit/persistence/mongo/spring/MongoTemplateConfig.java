package org.devgateway.toolkit.persistence.mongo.spring;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

/**
 * Created by mpostelnicu on 6/12/17.
 */
@Configuration
public class MongoTemplateConfig {

    @Autowired
    private MongoProperties properties;

    @Bean(autowire = Autowire.BY_NAME, name = "mongoTemplate")
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(properties.getHost(), properties.getPort()),
                properties.getDatabase()));
    }

    /**
     * Creates a shadow template configuration by adding "-shadow" as postfix of database name.
     * This is used to replicate the entire database structure in a shadow/temporary database location
     *
     * @return
     * @throws Exception
     */
    @Bean(autowire = Autowire.BY_NAME, name = "shadowMongoTemplate")
    public MongoTemplate shadowMongoTemplate() throws Exception {
        return new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(properties.getHost(), properties.getPort()),
                properties.getDatabase() + "-shadow"));
    }


}
