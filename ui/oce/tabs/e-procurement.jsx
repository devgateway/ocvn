import Tab from "./index";
import PercentEBid from "../visualizations/charts/percent-e-bid";

class EProcurement extends Tab{
  static getName(__){return __('E-Procurement')}
}

EProcurement.icon = "eprocurement";
EProcurement.visualizations = [PercentEBid];

export default EProcurement;