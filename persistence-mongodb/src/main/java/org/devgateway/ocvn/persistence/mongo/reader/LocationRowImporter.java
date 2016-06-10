package org.devgateway.ocvn.persistence.mongo.reader;

import org.devgateway.ocds.persistence.mongo.Gazetteer;
import org.devgateway.ocds.persistence.mongo.Location;
import org.devgateway.ocds.persistence.mongo.reader.RowImporter;
import org.devgateway.ocds.persistence.mongo.spring.ImportService;
import org.devgateway.ocvn.persistence.mongo.dao.VNLocation;
import org.devgateway.ocvn.persistence.mongo.repository.VNLocationRepository;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.text.ParseException;

/**
 * Specific {@link RowImporter} for {@link Location}, in the custom Excel format
 * provided by Vietnam
 *
 * @author mihai
 * @see Location
 */
public class LocationRowImporter extends RowImporter<VNLocation, VNLocationRepository> {

    public LocationRowImporter(final VNLocationRepository locationRepository, final ImportService importService,
                               final int skipRows) {
        super(locationRepository, importService, skipRows);
    }

    @Override
    public void importRow(final String[] row) throws ParseException {

        VNLocation location = repository.findByDescription(row[0]);
        if (location != null) {
            throw new RuntimeException("Duplicate location name " + row[0]);
        }

        location = new VNLocation();

        location.setId(row[3]);
        location.setDescription(row[0]);

        GeoJsonPoint coordinates = new GeoJsonPoint(getDouble(row[2]), getDouble(row[1]));
        location.setGeometry(coordinates);

        Gazetteer gazetteer = new Gazetteer();
        gazetteer.getIdentifiers().add(row[3]);
        location.setGazetteer(gazetteer);
        location.setUri(location.getGazetteerPrefix() + row[3]);

        repository.insert(location);
    }
}
