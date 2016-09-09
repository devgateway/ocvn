import Tab from "./index";
import CostEffectiveness from "../visualizations/charts/cost-effectiveness";
import AvgNrBids from "../visualizations/charts/avg-nr-bids";
import ProcurementMethod from "../visualizations/charts/procurement-method";

class Competitiveness extends Tab{
  static getName(__){return __('Competitiveness')}
}

Competitiveness.icon = "competitive";
Competitiveness.visualizations = [CostEffectiveness, ProcurementMethod, AvgNrBids];
export default Competitiveness;