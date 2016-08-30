import Tab from "./index";
import BidPeriod from "../visualizations/charts/bid-period";
import Cancelled from "../visualizations/charts/cancelled";
import BidsByItem from "../visualizations/charts/bids-by-item";

class Efficiency extends Tab{
  static getName(__){return __('Efficiency')}
}

Efficiency.icon = "efficiency";
Efficiency.visualizations = [BidPeriod, Cancelled, BidsByItem];

export default Efficiency;