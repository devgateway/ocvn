package org.devgateway.ocds.persistence.mongo.test;

import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.repository.ReleaseRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ReleaseRepositoryTest extends AbstractMongoTest {

    private String OCID="release-1";

    @Autowired
    private ReleaseRepository releaseRepository;

    @Test
    public void testReleaseSaveAndFind() {

        Release release=new Release();

        release.setOcid(OCID);

        releaseRepository.insert(release);


        Release byOcid = releaseRepository.findByOcid(OCID);

        Assert.assertEquals(OCID, byOcid.getOcid());

    }

}
