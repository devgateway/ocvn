import Competitiveness from "../../oce/tabs/competitiveness";
import CostEffectiveness from "../../oce/visualizations/charts/cost-effectiveness";
import BidSelectionMethod from "../visualizations/charts/bid-selection-method";
import AvgNrBids from "../../oce/visualizations/charts/avg-nr-bids";

class OCVNCompetitiveness extends Competitiveness{}

OCVNCompetitiveness.visualizations = [CostEffectiveness, BidSelectionMethod, AvgNrBids];
export default OCVNCompetitiveness;