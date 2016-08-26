import EProcurement from "../../oce/tabs/e-procurement";
import PercentWithTenders from "../visualizations/percent-with-tenders";

class OCVNProcurement extends EProcurement{}

OCVNProcurement.icon = "eprocurement";
OCVNProcurement.visualizations = EProcurement.visualizations.concat(PercentWithTenders);

export default OCVNProcurement;