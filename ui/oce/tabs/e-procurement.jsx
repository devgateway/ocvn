import Tab from "./index";
import PercentEProcurement from "../visualizations/charts/percent-e-procurement";
import PercentEBid from "../visualizations/charts/percent-e-bid";

class EProcurement extends Tab{
  static getName(__){return __('E-Procurement')}
}

EProcurement.icon = "eprocurement";
EProcurement.visualizations = [PercentEProcurement, PercentEBid];

export default EProcurement;