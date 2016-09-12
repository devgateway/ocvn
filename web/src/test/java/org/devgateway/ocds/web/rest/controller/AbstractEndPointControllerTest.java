package org.devgateway.ocds.web.rest.controller;

import org.apache.log4j.Logger;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.repository.ReleaseRepository;
import org.devgateway.ocds.persistence.mongo.spring.json.JsonImportPackage;
import org.devgateway.ocds.persistence.mongo.spring.json.ReleasePackageJsonImport;
import org.devgateway.toolkit.web.AbstractWebTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * @author idobre
 * @since 9/9/16
 *
 * Class that imports some test releases from 'endpoint-data-test.json' file and is used to test each endpoint.
 */
public abstract class AbstractEndPointControllerTest extends AbstractWebTest {
    protected static Logger logger = Logger.getLogger(AbstractEndPointControllerTest.class);

    private Collection<Release> releases;

    private List<Release> importedRelease;

    @Autowired
    private ReleaseRepository releaseRepository;

    @Before
    public final void setUp() throws Exception {
        releaseRepository.deleteAll();

        final ClassLoader classLoader = getClass().getClassLoader();

        final File file = new File(classLoader.getResource("json/endpoint-data-test.json").getFile());
        final JsonImportPackage releasePackageJsonImport = new ReleasePackageJsonImport(releaseRepository, file);

        this.releases = releasePackageJsonImport.importObjects();
        this.importedRelease = releaseRepository.findAll();
    }

    @After
    public final void tearDown() {
        releaseRepository.deleteAll();
    }

    @Test
    public void testImportForEndpoints() {
        // just test that the import was done correctly
        Assert.assertEquals(3, releases.size());
        Assert.assertNotNull(importedRelease);
        Assert.assertEquals(3, importedRelease.size());
    }
}
