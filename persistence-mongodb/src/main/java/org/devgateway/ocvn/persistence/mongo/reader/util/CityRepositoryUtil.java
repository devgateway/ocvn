package org.devgateway.ocvn.persistence.mongo.reader.util;

import org.devgateway.ocvn.persistence.mongo.dao.City;
import org.devgateway.ocvn.persistence.mongo.repository.CityRepository;

public final class CityRepositoryUtil {

    private CityRepositoryUtil() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Ensures a city by the given id exists, or else create one.
     * @see OCVN-332
     * 
     * @param cityId
     * @param cityRepository
     * @return
     */
    public static City ensureExistsCityById(Integer cityId, CityRepository cityRepository) {
        // these are skipped because are s korea provinces, see OCVN-332
        if (cityId != 3 && cityId != 100 && cityId != 28 && cityId != 695840) {
            City city = cityRepository.findOne(cityId);
            if (city == null) {
                city = new City();
                city.setId(cityId);
                city = cityRepository.save(city);
            }
            return city;
        }
        return null;
    }

}
