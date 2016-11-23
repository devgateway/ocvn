/**
 *
 */
package org.devgateway.ocvn.persistence.mongo.reader;

import java.text.ParseException;

import org.devgateway.ocds.persistence.mongo.Identifier;
import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.persistence.mongo.reader.RowImporter;
import org.devgateway.ocds.persistence.mongo.repository.VNOrganizationRepository;
import org.devgateway.ocds.persistence.mongo.spring.ImportService;
import org.devgateway.ocvn.persistence.mongo.dao.OrgDepartment;
import org.devgateway.ocvn.persistence.mongo.dao.OrgGroup;
import org.devgateway.ocvn.persistence.mongo.dao.VNOrganization;
import org.devgateway.ocvn.persistence.mongo.repository.CityRepository;
import org.devgateway.ocvn.persistence.mongo.repository.OrgDepartmentRepository;
import org.devgateway.ocvn.persistence.mongo.repository.OrgGroupRepository;

/**
 * @author mpostelnicu Specific {@link RowImporter} for Public Institutions, in
 *         the custom Excel format provided by Vietnam
 * @see VNOrganization
 */
public class PublicInstitutionRowImporter extends OrganizationRowImporter<VNOrganization> {

    private final OrgGroupRepository orgGroupRepository;
    private final OrgDepartmentRepository orgDepartmentRepository;

    public PublicInstitutionRowImporter(final VNOrganizationRepository repository, final CityRepository cityRepository,
            final OrgGroupRepository orgGroupRepository, final OrgDepartmentRepository orgDepartmentRepository,
            final ImportService importService, final int skipRows) {
        super(repository, cityRepository, importService, skipRows);
        this.orgGroupRepository = orgGroupRepository;
        this.orgDepartmentRepository = orgDepartmentRepository;
    }
    
    @Override
    public void importRow(final String[] row) throws ParseException {
        if (getRowCell(row, 0) == null) {
            throw new RuntimeException("Main identifier empty!");
        }

        String newIdentifier = getRowCellUpper(row, 0);

        String name = getRowCellUpper(row, 1);

        VNOrganization organization = repository.findByName(name);
        Identifier identifier = new Identifier();
        identifier.setId(newIdentifier);

        if (organization == null) {
            organization = (VNOrganization) newOrganization(new VNOrganization(), identifier, name);
            newAddress(organization, getRowCell(row, 14));
            
            if (getRowCell(row, 44) != null) {
                Identifier additionalIdentifier = new Identifier();
                additionalIdentifier.setId(getRowCellUpper(row, 44));
                addAditionalIdentifierOrFail(organization, additionalIdentifier);
            }

            newContactPoint(organization, getRowCell(row, 5), getRowCell(row, 7), getRowCell(row, 8),
                    getRowCell(row, 9), getRowCell(row, 18));

            if (getRowCell(row, 13) != null) {
                newCity(organization, getInteger(getRowCell(row, 13)));
            }
            
            if (getRowCell(row, 47) != null && getInteger(getRowCell(row, 47)) != 0) {
                newDepartment(organization, getInteger(getRowCell(row, 47)));
            }
            
            if (getRowCell(row, 48) != null) { 
                newOrgGroup(organization, getInteger(getRowCell(row, 48)));
            }

        } else {
            addAditionalIdentifierOrFail(organization, identifier);
        }

        organization.getTypes().add(Organization.OrganizationType.supplier);

        repository.save(organization);

    }
    
    protected void newDepartment(VNOrganization organization, Integer departmentId) {
        OrgDepartment orgDepartment = orgDepartmentRepository.findOne(departmentId);
        if (orgDepartment == null) {
            orgDepartment = new OrgDepartment();
            orgDepartment.setId(departmentId);
            orgDepartment = orgDepartmentRepository.save(orgDepartment);
        }
        organization.setDepartment(orgDepartment);
    }
    
    protected void newOrgGroup(VNOrganization organization, Integer orgGroupId) {
        OrgGroup orgGroup = orgGroupRepository.findOne(orgGroupId);
        if (orgGroup == null) {
            orgGroup = new OrgGroup();
            orgGroup.setId(orgGroupId);
            orgGroup = orgGroupRepository.save(orgGroup);
        }
        organization.setGroup(orgGroup);
    }

}
