/**
 * 
 */
package org.devgateway.ocds.persistence.mongo.spring;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

import org.devgateway.ocds.persistence.mongo.FlaggedRelease;
import org.devgateway.ocds.persistence.mongo.flags.AbstractFlaggedReleaseFlagProcessor;
import org.devgateway.ocds.persistence.mongo.flags.processors.release.ReleaseFlagI038Processor;
import org.devgateway.ocds.persistence.mongo.repository.FlaggedReleaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * @author mpostelnicu
 *
 */
@Service
public class ReleaseFlaggingService {

    @Autowired
    private FlaggedReleaseRepository releaseRepository;

    public static final int FLAGGING_BATCH_SIZE = 5000;

    private final Collection<AbstractFlaggedReleaseFlagProcessor> releaseFlagProcessors =
            Collections.unmodifiableList(Arrays.asList(ReleaseFlagI038Processor.INSTANCE));

    private void processAndSaveFlagsForRelease(FlaggedRelease release) {
        releaseFlagProcessors.forEach(processor -> processor.process(release));
        releaseRepository.save(release);
    }

    public void processAndSaveFlagsForAllReleases(Consumer<String> logMessage) {

        logMessage.accept("<b>RUNNING SCHEMA VALIDATION.</b>");

        int pageNumber = 0;
        int processedCount = 0;

        Page<FlaggedRelease> page;
        do {
            page = releaseRepository.findAll(new PageRequest(pageNumber++, FLAGGING_BATCH_SIZE));
            page.getContent().parallelStream().forEach(r -> processAndSaveFlagsForRelease(r));
            processedCount += page.getNumberOfElements();
            logMessage.accept("Validated " + processedCount + " releases");
        } while (!page.isLast());

        logMessage.accept("<b>SCHEMA VALIDATION COMPLETE.</b>");
    }
}
