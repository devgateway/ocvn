import OCEFilters from "../../oce/filters";
import OrganizationsTab from "../../oce/filters/tabs/organizations";
import ProcurementTypes from "./tabs/procurement-types";
import Locations from "./tabs/locations";
import Amounts from "../../oce/filters/tabs/amounts.jsx";

class OCVNFilters extends OCEFilters{
}

OCVNFilters.TABS=[OrganizationsTab, ProcurementTypes, Locations, Amounts];

export default OCVNFilters;
