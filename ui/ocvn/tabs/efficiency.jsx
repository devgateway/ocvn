import Efficiency from "../../oce/tabs/efficiency";
import CancelledByReason from "../visualizations/cancelled-by-reason";

class OCVNEfficiency extends Efficiency{}

OCVNEfficiency.visualizations = Efficiency.visualizations.concat(CancelledByReason);

export default OCVNEfficiency;