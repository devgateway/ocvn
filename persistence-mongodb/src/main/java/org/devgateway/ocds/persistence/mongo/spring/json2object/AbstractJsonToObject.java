package org.devgateway.ocds.persistence.mongo.spring.json2object;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.io.IOUtils;
import org.devgateway.ocds.persistence.mongo.spring.json2object.deserializer.GeoJsonPointDeserializer;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Default implementation for converting a Json String to an Object
 *
 * @author idobre
 * @since 6/1/16
 */
public abstract class AbstractJsonToObject<T> implements JsonToObject<T> {
    protected final ObjectMapper mapper;

    protected final String jsonObject;

    public AbstractJsonToObject(final String jsonObject) {
        this.jsonObject = jsonObject;
        this.mapper = new ObjectMapper();
        SimpleModule geoJsonPointDeserializer = new SimpleModule()
                .addDeserializer(GeoJsonPoint.class, new GeoJsonPointDeserializer());
        mapper.registerModule(geoJsonPointDeserializer);

        // this are non-standard features that are disabled by default.
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);

        // Note that enabling this feature will incur performance overhead
        // due to having to store and check additional information: this typically
        // adds 20-30% to execution time for basic parsing.
        mapper.configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, true);
    }

    public AbstractJsonToObject(final InputStream inputStream) throws IOException {
        this(IOUtils.toString(inputStream, "UTF-8"));
    }

    public AbstractJsonToObject(final File file) throws IOException {
        this(new FileInputStream(file));
    }
}
