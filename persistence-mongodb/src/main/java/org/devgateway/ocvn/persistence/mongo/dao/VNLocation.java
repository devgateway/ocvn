/**
 *
 */
package org.devgateway.ocvn.persistence.mongo.dao;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.devgateway.ocds.persistence.mongo.Location;
import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExport;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author mihai
 *
 */
@Document(collection = "location")
public class VNLocation extends Location<VNLocation.DefaultGeoJsonPoint> {
    public static final String GEONAMES_URI_PREFIX = "http://www.geonames.org/";

    public static final String GEONAMES_SCHEME = "GEONAMES";

    @ExcelExport
    private GeoJsonPoint geometry;

    public VNLocation() {
        super();
        this.getGazetteer().setScheme(GEONAMES_SCHEME);
    }

    @Override
    public DefaultGeoJsonPoint getGeometry() {
        return (DefaultGeoJsonPoint) geometry;
    }

    @Override
    public void setGeometry(DefaultGeoJsonPoint geometry) {
        this.geometry = geometry;
    }

    @Override
    public String getGazetteerPrefix() {
        return GEONAMES_URI_PREFIX;
    }

    /**
     * Created this inner class so that we can map with jackson a json to GeoJsonPoint class.
     * We do this because GeoJsonPoint doesn't have setters for *type* or *coordinates* properties
     */
    @JsonPropertyOrder({
            "type",
            "coordinates"
    })
    public static final class DefaultGeoJsonPoint extends GeoJsonPoint {
        @JsonCreator
        public DefaultGeoJsonPoint(@JsonProperty("coordinates") List<Double> coordinates) {
            super(coordinates.get(0), coordinates.get(1));
        }
    }
}
