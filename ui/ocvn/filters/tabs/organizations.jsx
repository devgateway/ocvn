import Organizations from "../../../oce/filters/tabs/organizations";
import PEGroup from "../pe-group";
import PEDepartment from "../pe-department";

class OCVNOrganizations extends Organizations{}

OCVNOrganizations.FILTERS = Organizations.FILTERS.concat([
    ['procuringEntityGroupId', PEGroup],
    ['procuringEntityDepartmentId', PEDepartment]
]);

export default OCVNOrganizations;