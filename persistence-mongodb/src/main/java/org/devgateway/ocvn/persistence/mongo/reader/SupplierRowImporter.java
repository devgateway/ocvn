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
 * @author mihai Specific {@link RowImporter} for Suppliers, in the custom Excel
 *         format provided by Vietnam
 * @see VNOrganization
 */
public class SupplierRowImporter extends RowImporter<Organization, String, OrganizationRepository> {

    public SupplierRowImporter(final OrganizationRepository repository, final ImportService importService,
            final int skipRows) {
        super(repository, importService, skipRows);
    }

    @Override
    public void importRow(final String[] row) throws ParseException {
        if (getRowCell(row, 0) == null) {
            throw new RuntimeException("Main identifier empty!");
        }
        Organization organization = repository.findOne(getRowCellUpper(row, 0));

        if (organization != null) {
            throw new RuntimeException("Duplicate identifer for organization " + organization);
        }
        organization = new Organization();
        Identifier identifier = new Identifier();

        identifier.setId(getRowCellUpper(row, 0));
        organization.setId(getRowCellUpper(row, 0));
        organization.setIdentifier(identifier);
        organization.setName(getRowCellUpper(row, 2));
        organization.getTypes().add(Organization.OrganizationType.supplier);

        Address address = new Address();
        address.setStreetAddress(getRowCell(row, 18));
        address.setPostalCode(getRowCell(row, 17));

        organization.setAddress(address);

        ContactPoint cp = new ContactPoint();
        cp.setTelephone(getRowCell(row, 20));
        cp.setFaxNumber(getRowCell(row, 21));
        cp.setUrl(getRowCell(row, 22));

        organization.setContactPoint(cp);

        repository.insert(organization);

    }

}
