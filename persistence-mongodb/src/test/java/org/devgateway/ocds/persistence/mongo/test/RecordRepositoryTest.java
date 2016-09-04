package org.devgateway.ocds.persistence.mongo.test;

import java.math.BigDecimal;

import org.devgateway.ocds.persistence.mongo.Amount;
import org.devgateway.ocds.persistence.mongo.Award;
import org.devgateway.ocds.persistence.mongo.Budget;
import org.devgateway.ocds.persistence.mongo.Planning;
import org.devgateway.ocds.persistence.mongo.Record;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.repository.RecordRepository;
import org.devgateway.ocds.persistence.mongo.repository.ReleaseRepository;
import org.devgateway.ocds.persistence.mongo.spring.ReleaseCompilerService;
import org.devgateway.toolkit.persistence.mongo.test.AbstractMongoTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RecordRepositoryTest extends AbstractMongoTest {

	private String ocid = "release-1";

	@Autowired
	private ReleaseRepository releaseRepository;

	@Autowired
	private RecordRepository recordRepository;

	@Autowired
	private ReleaseCompilerService releaseCompilerService;

	@Test
	public void testReleaseSaveAndFind() {

		Release release = new Release();

		release.setOcid(ocid);

		releaseRepository.insert(release);

		Release byOcid = releaseRepository.findByOcid(ocid);

		Assert.assertEquals(ocid, byOcid.getOcid());

	}

	private Release getRelease1() {
		Release release = new Release();
		release.setOcid(ocid);
		Planning planning = new Planning();
		release.setPlanning(planning);
		Budget budget = new Budget();
		planning.setBudget(budget);
		Amount amount = new Amount();
		amount.setAmount(BigDecimal.valueOf(1234));
		amount.setCurrency("VND");
		budget.setAmount(amount);
		budget.setDescription("Some description 1");
		budget.setProject("A nice project! 1");
		budget.setSource("The source! 1");

		Award award1 = new Award();
		award1.setDescription("Some award1");
		award1.setId("1");
		release.getAwards().add(award1);

		Award award2 = new Award();
		award2.setDescription("Some award2");
		award2.setId("2");
		release.getAwards().add(award2);

		return release;
	}

	private Release getRelease2() {
		Release release = new Release();
		release.setOcid(ocid);
		Planning planning = new Planning();
		release.setPlanning(planning);
		Budget budget = new Budget();
		planning.setBudget(budget);
		Amount amount = new Amount();
		amount.setAmount(BigDecimal.valueOf(2345));
		amount.setCurrency("USD");
		budget.setAmount(amount);
		budget.setDescription("Some description 2");
		budget.setProject("A nice project! 2");
		budget.setSource("The source! 2");

		Award award1 = new Award();
		award1.setDescription("SoMe AwArD 2");
		award1.setId("2");
		release.getAwards().add(award1);

		Award award2 = new Award();
		award2.setDescription("Some award3");
		award2.setId("3");
		release.getAwards().add(award2);

		return release;
	}

	@Test
	public void testRecordSave() {

		Record record = new Record();
		record.setOcid(ocid);

		record.getReleases().add(getRelease1());
		record.getReleases().add(getRelease2());

		recordRepository.save(record);

		releaseCompilerService.createSaveCompiledReleaseAndSaveRecord(record);

		Assert.assertNotNull(record.getCompiledRelease());
		
		Assert.assertEquals(3, record.getCompiledRelease().getAwards().size());
	}

}
