import Organizations from "../../../oce/filters/tabs/organizations";
import PEGroup from "../pe-group";

class OCVNOrganizations extends Organizations{}

OCVNOrganizations.FILTERS = Organizations.FILTERS.concat([
    ['procuringEntityGroupId', PEGroup]
]);

export default OCVNOrganizations;