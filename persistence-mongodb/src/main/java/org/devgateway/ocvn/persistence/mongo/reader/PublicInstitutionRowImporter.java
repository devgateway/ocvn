/**
 *
 */
package org.devgateway.ocvn.persistence.mongo.reader;

import java.text.ParseException;

import org.devgateway.ocds.persistence.mongo.Address;
import org.devgateway.ocds.persistence.mongo.ContactPoint;
import org.devgateway.ocds.persistence.mongo.Identifier;
import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.persistence.mongo.reader.RowImporter;
import org.devgateway.ocds.persistence.mongo.repository.OrganizationRepository;
import org.devgateway.ocds.persistence.mongo.spring.ImportService;

/**
 * @author mihai Specific {@link RowImporter} for Public Institutions, in the
 *         custom Excel format provided by Vietnam
 * @see VNOrganization
 */
public class PublicInstitutionRowImporter extends RowImporter<Organization, OrganizationRepository> {

	public PublicInstitutionRowImporter(final OrganizationRepository repository, final ImportService importService,
			final int skipRows) {
		super(repository, importService, skipRows);
	}

	@Override
	public void importRow(final String[] row) throws ParseException {
		if (getRowCell(row, 0) == null) {
			throw new RuntimeException("Main identifier empty!");
		}
		Organization organization = repository.findOne(getRowCell(row, 0));
		if (organization != null) {
			throw new RuntimeException("Duplicate identifer for organization " + organization);
		}
		organization = new Organization();
		Identifier identifier = new Identifier();

		identifier.setId(getRowCell(row, 0));
		organization.setId(getRowCell(row, 0));
		organization.setIdentifier(identifier);
		organization.setName(getRowCell(row, 1));

		if (getRowCell(row, 44) != null) {
			Identifier additionalIdentifier = new Identifier();
			additionalIdentifier.setId(getRowCell(row, 44));
			organization.getAdditionalIdentifiers().add(additionalIdentifier);
		}

		Address address = new Address();
		address.setStreetAddress(getRowCell(row, 14));
		address.setPostalCode(getRowCell(row, 13));

		organization.setAddress(address);

		ContactPoint cp = new ContactPoint();
		cp.setName(getRowCell(row, 5));
		cp.setTelephone(getRowCell(row, 7));
		cp.setFaxNumber(getRowCell(row, 8));
		cp.setEmail(getRowCell(row, 9));
		cp.setUrl(getRowCell(row, 18));

		organization.setContactPoint(cp);

		repository.insert(organization);

	}

}
