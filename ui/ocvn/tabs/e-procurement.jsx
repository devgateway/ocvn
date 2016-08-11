import EProcurement from "../../oce/tabs/e-procurement";
import PercentEBid from "../../oce/visualizations/charts/percent-e-bid";
import PercentEProcurement from "../visualizations/charts/percent-e-procurement";

class OCVNEProcurement extends EProcurement{}

OCVNEProcurement.visualizations = [PercentEProcurement, PercentEBid];
export default OCVNEProcurement;