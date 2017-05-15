package org.devgateway.ocds.web.spring;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.devgateway.ocds.persistence.mongo.Classification;
import org.devgateway.ocds.persistence.mongo.Identifiable;
import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.repository.ClassificationRepository;
import org.devgateway.ocds.persistence.mongo.repository.OrganizationRepository;
import org.devgateway.ocds.persistence.mongo.repository.ReleaseRepository;
import org.devgateway.toolkit.persistence.mongo.spring.MongoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Created by mpostelnicu on 10-May-17.
 */

@Service
public class OCDSPopulatorService {

    protected static Logger logger = Logger.getLogger(OCDSPopulatorService.class);
    @Autowired
    private ReleaseRepository releaseRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private ClassificationRepository classificationRepository;

    private String getRandomTxt() {
        return RandomStringUtils.randomAlphabetic(10, 15);
    }

    public void logMessage(String message) {
        logger.info(message);
    }

    public void randomizeOrganizations(Consumer<String> logMessage) {
        logMessage.accept("<b>RANDOMIZE ORGS.</b>");

        MongoUtil.processRepositoryItemsPaginated(organizationRepository, this::randomizeOrganization,
                this::logMessage);

        logMessage.accept("<b>RANDOMIZE ORGS COMPLETED.</b>");
    }

    public void randomizeReleases(Consumer<String> logMessage) {
        logMessage.accept("<b>RANDOMIZE RELEASES.</b>");

        MongoUtil.processRepositoryItemsPaginated(releaseRepository, this::randomizeRelease,
                this::logMessage);

        logMessage.accept("<b>RANDOMIZE RELEASES COMPLETED.</b>");
    }

    public void randomizeClassifications(Consumer<String> logMessage) {
        logMessage.accept("<b>RANDOMIZE CLASSIFICATIONS.</b>");

        MongoUtil.processRepositoryItemsPaginated(classificationRepository, this::randomizeClassification,
                this::logMessage);

        logMessage.accept("<b>RANDOMIZE CLASSIFICATIONS COMPLETED.</b>");
    }


    public void randomizeOrganization(Organization o) {
        o.setName(getRandomTxt());
        if (o.getAddress() != null) {
            o.getAddress().setCountryName(getRandomTxt());
            o.getAddress().setLocality(getRandomTxt());
            o.getAddress().setPostalCode(getRandomTxt());
            o.getAddress().setRegion(getRandomTxt());
            o.getAddress().setStreetAddress(getRandomTxt());
        }
        if (o.getContactPoint() != null) {
            o.getContactPoint().setEmail(getRandomTxt());
            o.getContactPoint().setFaxNumber(getRandomTxt());
            o.getContactPoint().setName(getRandomTxt());
            o.getContactPoint().setTelephone(getRandomTxt());
            o.getContactPoint().setUrl(getRandomTxt());
        }
        organizationRepository.save(o);
    }

    public void randomizeClassification(Classification c) {
        c.setDescription(getRandomTxt());
        classificationRepository.save(c);
    }

    public <T extends Identifiable, ID extends Serializable> T getSavedEntityFromEntity(T t,
                                                                                        MongoRepository<T, ID>
                                                                                                repository) {
        T newOrg = repository.findOne((ID) t.getIdProperty());
        if (newOrg == null) {
            throw new RuntimeException("An unidentified element was used inline");
        }
        return newOrg;
    }


    public <T extends Identifiable, ID extends Serializable>
    void replaceEntitiesWithSavedEntities(Collection<T> c,
                                          MongoRepository<T, ID> repository) {
        Iterator<T> i = c.iterator();
        while (i.hasNext()) {
            T o = i.next();
            i.remove();
            c.add(getSavedEntityFromEntity(o, repository));
        }
    }

    public void randomizeRelease(Release r) {
        if (r.getBids() != null && r.getBids().getDetails() != null) {
            r.getBids().getDetails().forEach(d ->
                    replaceEntitiesWithSavedEntities(d.getTenderers(), organizationRepository));
        }
        if (r.getAwards() != null) {
            r.getAwards().forEach(award -> {
                if (award.getSuppliers() != null) {
                    replaceEntitiesWithSavedEntities(award.getSuppliers(),
                            organizationRepository);
                }
                award.setDescription(getRandomTxt());
                award.setTitle(getRandomTxt());
            });
            if (r.getBuyer() != null) {
                r.setBuyer(getSavedEntityFromEntity(r.getBuyer(), organizationRepository));
            }

            if (r.getPlanning() != null && r.getPlanning().getBudget() != null) {
                r.getPlanning().getBudget().setProject(getRandomTxt());
                r.getPlanning().getBudget().setProjectID(getRandomTxt());
            }

            if (r.getTender() != null) {
                if (r.getTender().getProcuringEntity() != null) {
                    r.getTender().setProcuringEntity(getSavedEntityFromEntity(r.getTender().getProcuringEntity(),
                            organizationRepository));
                }

                if (r.getTender().getItems() != null) {
                    r.getTender().getItems().forEach(i -> {
                        i.setDescription(getRandomTxt());
                        if (i.getClassification() != null) {
                            i.setClassification(getSavedEntityFromEntity(i.getClassification(),
                                    classificationRepository));
                        }
                    });
                }
            }


        }
        releaseRepository.save(r);

    }


//    @PostConstruct
//    public void setProcessors() {
//        randomizeOrganizations(this::logMessage);
//        randomizeClassifications(this::logMessage);
//        randomizeReleases(this::logMessage);
//    }

}
