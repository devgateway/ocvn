package org.devgateway.ocvn.persistence.mongo.reader;

import org.devgateway.ocds.persistence.mongo.Amount;
import org.devgateway.ocds.persistence.mongo.Award;
import org.devgateway.ocds.persistence.mongo.Identifier;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.Tag;
import org.devgateway.ocds.persistence.mongo.Tender;
import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.devgateway.ocds.persistence.mongo.reader.ReleaseRowImporter;
import org.devgateway.ocds.persistence.mongo.reader.RowImporter;
import org.devgateway.ocds.persistence.mongo.repository.ReleaseRepository;
import org.devgateway.ocds.persistence.mongo.spring.ImportService;
import org.devgateway.ocvn.persistence.mongo.dao.VNAward;
import org.devgateway.ocvn.persistence.mongo.dao.VNOrganization;
import org.devgateway.ocvn.persistence.mongo.dao.VNPlanning;
import org.devgateway.ocvn.persistence.mongo.dao.VNTender;
import org.devgateway.ocvn.persistence.mongo.repository.VNOrganizationRepository;

import java.text.ParseException;

/**
 * Specific {@link RowImporter} for Offline Awards, in the custom Excel format
 * provided by Vietnam
 *
 * @author mihai
 * @see VNAward
 */
public class OfflineAwardRowImporter extends ReleaseRowImporter {

    private VNOrganizationRepository organizationRepository;

    public OfflineAwardRowImporter(final ReleaseRepository releaseRepository, final ImportService importService,
                                   final VNOrganizationRepository organizationRepository, final int skipRows) {
        super(releaseRepository, importService, skipRows);
        this.organizationRepository = organizationRepository;
    }

    @Override
    public Release createReleaseFromReleaseRow(final String[] row) throws ParseException {

        Release release = repository.findByPlanningBidNo(row[0]);

        if (release == null) {
            release = new Release();
            release.getTag().add(Tag.award);
            release.setOcid(MongoConstants.OCDS_PREFIX + "bidno-" + row[0]);
            VNPlanning planning = new VNPlanning();
            release.setPlanning(planning);
            planning.setBidNo(row[0]);
        }
        
		if (release.getTender() == null) {
			VNTender tender = new VNTender();
			tender.setId(release.getOcid());
			release.setTender(tender);
		}
		
		release.getTender().getSubmissionMethod().add(Tender.SubmissionMethod.written.toString());

        VNAward award = new VNAward();
        award.setId(release.getOcid() + "-award-" + release.getAwards().size());

        release.getAwards().add(award);

        award.setTitle(row[1]);

        if (!row[2].isEmpty()) {
            Amount value = new Amount();
            value.setCurrency("VND");
            value.setAmount(getDecimal(row[2]));
            award.setValue(value);
        }

        VNOrganization supplier = organizationRepository.findOne(row[3]);

        if (supplier == null) {
            supplier = new VNOrganization();
            Identifier supplierId = new Identifier();
            supplierId.setId(row[3]);
            supplier.setIdentifier(supplierId);
            supplier = organizationRepository.insert(supplier);
        }

        award.getSuppliers().add(supplier);

        award.setContractTime(row[4]);

        // award.setBidOpenRank(row[4].isEmpty() ? null :
        // Integer.parseInt(row[4]));

        if (row.length > 5) {
            award.setStatus(row[5].equals("Y") ? Award.Status.active : Award.Status.unsuccesful);
        }

        if (row.length > 6) {
            award.setInelibigleYN(row[6]);
        }

        if (row.length > 7) {
            award.setIneligibleRson(row[7]);
        }

        if (row.length > 8) {
            award.setBidType(row[8].isEmpty() ? null : getInteger(row[8]));
        }

        if (row.length > 9) {
            award.setBidSuccMethod(row[9].isEmpty() ? null : getInteger(row[9]));
        }

        if (row.length > 10 && row[10] != null && !row[10].isEmpty()) {
            Amount value2 = new Amount();
            value2.setCurrency("VND");
            value2.setAmount(getDecimal(row[10]));
            award.setValue(value2);
        }

        if (row.length > 11) {
            award.setDate(row[11].isEmpty() ? null : getExcelDate(row[11]));
        }

        // For unsuccessful awards (in both eBid and Offline bid tabs), map the
        // information on the bidder (supplier) to tender.tenderers for that
        // BID_NO
        // we ignore the fields if there are no tenders found
        if (award.getStatus().equals(Award.Status.unsuccesful)) {
                release.getTender().getTenderers().add(supplier);
        }
        release.getTender().setNumberOfTenderers(release.getTender().getTenderers().size());        
        

        return release;
    }
}
