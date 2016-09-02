import EProcurement from "../../oce/tabs/e-procurement";
import PercentWithTenders from "../visualizations/percent-with-tenders";
import PercentEProcurement from "../visualizations/percent-e-procurement";

class OCVNProcurement extends EProcurement{}

OCVNProcurement.icon = "eprocurement";
OCVNProcurement.visualizations = [PercentEProcurement].concat(EProcurement.visualizations).concat(PercentWithTenders);

export default OCVNProcurement;