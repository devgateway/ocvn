package org.devgateway.ocds.persistence.mongo.test;

import org.devgateway.ocds.persistence.mongo.Address;
import org.devgateway.ocds.persistence.mongo.ContactPoint;
import org.devgateway.ocds.persistence.mongo.Identifier;
import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.persistence.mongo.repository.OrganizationRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrganizationRepositoryTest extends AbstractMongoTest {

    @Autowired
    private OrganizationRepository organizationRepository;

    private String ORG_ID = "1234";

    @Test
    public void testOrganizationSaveAndFind() {
        Organization organization = new Organization();
        organization.setName("Development Gateway");
        organization.setId(ORG_ID);

        Address address=new Address();
        address.setCountryName("Romania");
        address.setLocality("Bucuresti");
        address.setPostalCode("022671");
        address.setRegion("Bucuresti");
        address.setStreetAddress("7 Sos. Iancului");
        organization.setAddress(address);


        ContactPoint contactPoint=new ContactPoint();
        contactPoint.setEmail("mpostelnicu@developmentgateway.org");
        contactPoint.setFaxNumber("01234567");
        contactPoint.setTelephone("01234567");
        contactPoint.setUrl("http://developmentgateway.org");
        organization.setContactPoint(contactPoint);

        Identifier identifier = new Identifier();
        organization.getAdditionalIdentifiers().add(identifier);

        Organization savedOrganization = organizationRepository.save(organization);

        Assert.assertNotNull(savedOrganization);
        Assert.assertEquals(ORG_ID, savedOrganization.getId());

        Organization foundOrg = organizationRepository.findOne(ORG_ID);
        Assert.assertNotNull(foundOrg);
    }

}
