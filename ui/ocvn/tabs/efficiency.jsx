import Efficiency from "../../oce/tabs/efficiency";
import BidPeriod from "../../oce/visualizations/charts/bid-period";
import Cancelled from "../../oce/visualizations/charts/cancelled";

class OCVNEfficiency extends Efficiency{}

OCVNEfficiency.visualizations = [BidPeriod, Cancelled];

export default OCVNEfficiency;