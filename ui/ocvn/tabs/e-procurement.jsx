import EProcurement from "../../oce/tabs/e-procurement";
import PercentEProcurement from "../../oce/visualizations/charts/percent-e-procurement";
import PercentEBid from "../../oce/visualizations/charts/percent-e-bid";

class OCVNProcurement extends EProcurement{}

OCVNProcurement.icon = "eprocurement";
OCVNProcurement.visualizations = [PercentEProcurement, PercentEBid];

export default OCVNProcurement;