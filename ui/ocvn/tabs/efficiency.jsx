import Efficiency from "../../oce/tabs/efficiency";
import BidPeriod from "../../oce/visualizations/charts/bid-period";
import Cancelled from "../../oce/visualizations/charts/cancelled";
import CancelledByReason from "../visualizations/cancelled-by-reason";

class OCVNEfficiency extends Efficiency{}

OCVNEfficiency.visualizations = [BidPeriod, Cancelled, CancelledByReason];

export default OCVNEfficiency;