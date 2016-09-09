import Tab from "./index";
import PercentEBid from "../visualizations/charts/percent-e-bid";
import NrEbid from "../visualizations/charts/nr-e-bid";

class EProcurement extends Tab{
  static getName(__){return __('E-Procurement')}
}

EProcurement.icon = "eprocurement";
EProcurement.visualizations = [PercentEBid, NrEbid];

export default EProcurement;
