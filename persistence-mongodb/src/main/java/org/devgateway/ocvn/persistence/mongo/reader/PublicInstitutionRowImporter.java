/**
 *
 */
package org.devgateway.ocvn.persistence.mongo.reader;

import java.text.ParseException;

import org.devgateway.ocds.persistence.mongo.Address;
import org.devgateway.ocds.persistence.mongo.ContactPoint;
import org.devgateway.ocds.persistence.mongo.Identifier;
import org.devgateway.ocds.persistence.mongo.reader.RowImporter;
import org.devgateway.ocds.persistence.mongo.repository.VNOrganizationRepository;
import org.devgateway.ocds.persistence.mongo.spring.ImportService;
import org.devgateway.ocvn.persistence.mongo.dao.City;
import org.devgateway.ocvn.persistence.mongo.dao.OrgDepartment;
import org.devgateway.ocvn.persistence.mongo.dao.OrgGroup;
import org.devgateway.ocvn.persistence.mongo.dao.VNOrganization;
import org.devgateway.ocvn.persistence.mongo.reader.util.CityRepositoryUtil;
import org.devgateway.ocvn.persistence.mongo.repository.CityRepository;
import org.devgateway.ocvn.persistence.mongo.repository.OrgDepartmentRepository;
import org.devgateway.ocvn.persistence.mongo.repository.OrgGroupRepository;

/**
 * @author mihai Specific {@link RowImporter} for Public Institutions, in the
 *         custom Excel format provided by Vietnam
 * @see VNOrganization
 */
public class PublicInstitutionRowImporter extends RowImporter<VNOrganization, String, VNOrganizationRepository> {

    private final CityRepository cityRepository;
    private final OrgGroupRepository orgGroupRepository;
    private final OrgDepartmentRepository orgDepartmentRepository;

    public PublicInstitutionRowImporter(final VNOrganizationRepository repository, final CityRepository cityRepository,
            final OrgGroupRepository orgGroupRepository, final OrgDepartmentRepository orgDepartmentRepository,
            final ImportService importService,
            final int skipRows) {
        super(repository, importService, skipRows);
        this.cityRepository = cityRepository;
        this.orgGroupRepository = orgGroupRepository;
        this.orgDepartmentRepository = orgDepartmentRepository;
    }
   
    @Override
    public void importRow(final String[] row) throws ParseException {
        if (getRowCell(row, 0) == null) {
            throw new RuntimeException("Main identifier empty!");
        }
        VNOrganization organization = repository.findOne(getRowCellUpper(row, 0));
        if (organization != null) {
            throw new RuntimeException("Duplicate identifer for organization " + organization);
        }
        organization = new VNOrganization();
        Identifier identifier = new Identifier();

        identifier.setId(getRowCellUpper(row, 0));
        organization.setId(getRowCellUpper(row, 0));
        organization.setIdentifier(identifier);
        organization.setName(getRowCellUpper(row, 1));

        if (getRowCell(row, 44) != null) {
            Identifier additionalIdentifier = new Identifier();
            additionalIdentifier.setId(getRowCellUpper(row, 44));
            organization.getAdditionalIdentifiers().add(additionalIdentifier);
        }

        Address address = new Address();
        address.setStreetAddress(getRowCell(row, 14));
        
        if (getRowCell(row, 13) != null) {
            City city = CityRepositoryUtil.ensureExistsCityById(getInteger(getRowCell(row, 13)), cityRepository);
            if (city != null) {
                address.setPostalCode(city.getId().toString());
            }
        }

        organization.setAddress(address);

        ContactPoint cp = new ContactPoint();
        cp.setName(getRowCell(row, 5));
        cp.setTelephone(getRowCell(row, 7));
        cp.setFaxNumber(getRowCell(row, 8));
        cp.setEmail(getRowCell(row, 9));
        cp.setUrl(getRowCell(row, 18));

        organization.setContactPoint(cp);
        
        if (getRowCell(row, 47) != null) {
            OrgDepartment orgDepartment = orgDepartmentRepository.findOne(getInteger(getRowCell(row, 47)));
            if (orgDepartment == null) {
                orgDepartment = new OrgDepartment();
                orgDepartment.setId(getInteger(getRowCell(row, 47)));
                orgDepartment = orgDepartmentRepository.save(orgDepartment);
            }
            organization.setDepartment(orgDepartment);
        }
        
        if (getRowCell(row, 48) != null) {
            OrgGroup orgGroup = orgGroupRepository.findOne(getInteger(getRowCell(row, 48)));
            if (orgGroup == null) {
                orgGroup = new OrgGroup();
                orgGroup.setId(getInteger(getRowCell(row, 48)));
                orgGroup = orgGroupRepository.save(orgGroup);
            }
            organization.setGroup(orgGroup);
        }

        repository.insert(organization);

    }

}
