package org.devgateway.ocds.web.rest.controller;

import org.apache.log4j.Logger;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.repository.ReleaseRepository;
import org.devgateway.ocds.persistence.mongo.spring.json.JsonImportPackage;
import org.devgateway.ocds.persistence.mongo.spring.json.ReleasePackageJsonImport;
import org.devgateway.toolkit.web.AbstractWebTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;

/**
 * @author idobre
 * @since 9/9/16
 *
 * Class that imports some test releases from 'endpoint-data-test.json' file and is used to test each endpoint.
 */
public abstract class AbstractEndPointControllerTest extends AbstractWebTest {
    protected static Logger logger = Logger.getLogger(AbstractEndPointControllerTest.class);

    @Autowired
    private ReleaseRepository releaseRepository;

    private static ReleaseRepository releaseRepositoryStatic;

    @Before
    public final void setUp() throws Exception {
        // just run the setUp only once and be sure that the release collection is empty
        if (testDataInitialized) {
            return;
        }
        releaseRepository.deleteAll();
        releaseRepositoryStatic = releaseRepository;

        final ClassLoader classLoader = getClass().getClassLoader();

        final File file = new File(classLoader.getResource("json/endpoint-data-test.json").getFile());
        final JsonImportPackage releasePackageJsonImport = new ReleasePackageJsonImport(releaseRepository, file);
        releasePackageJsonImport.importObjects();

        testDataInitialized = true;
    }

    @AfterClass
    public static final void tearDown() {
        // be sure to clean up the release collection
        releaseRepositoryStatic.deleteAll();
    }

    @Test
    public void testImportForEndpoints() {
        // just test that the import was done correctly
        final List<Release> importedRelease = releaseRepository.findAll();
        Assert.assertNotNull(importedRelease);
        Assert.assertEquals(3, importedRelease.size());
    }
}
