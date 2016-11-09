import Efficiency from "../../oce/tabs/efficiency";
import CancelledByReason from "../visualizations/cancelled-by-reason";
import BidPeriod from "../../oce/visualizations/charts/bid-period";
import OCVNBidPeriod from "../visualizations/bid-period";

class OCVNEfficiency extends Efficiency{}

let arrReplace = (a, b, [head, ...tail]) => "undefined" == typeof head ?
                                          tail :
                                          [a == head ? b : head].concat(arrReplace(a, b, tail));

OCVNEfficiency.visualizations = arrReplace(BidPeriod, OCVNBidPeriod, Efficiency.visualizations)
    .concat(CancelledByReason);

export default OCVNEfficiency;
