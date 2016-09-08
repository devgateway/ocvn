import TenderLocations from "../../../oce/visualizations/map/tender-locations";
import TenderLocation, {ChartTab, OverviewTab, OverviewChartTab, CostEffectivenessTab}
  from "../../../oce/visualizations/map/tender-locations/location";
import BidSelectionMethod from "../../visualizations/bid-selection-method";

class BidSelectionMethodTab extends ChartTab{
  static getName(__){
    return __('Bid selection method');
  }
}

BidSelectionMethodTab.Chart = BidSelectionMethod;

class OCVNTenderLocation extends TenderLocation{}

OCVNTenderLocation.TABS = [OverviewTab, OverviewChartTab, CostEffectivenessTab, BidSelectionMethodTab];

class OCVNTenderLocations extends TenderLocations{
    static getLayerName(__){
        return __('Invitation to Bid Locations');
    }
}

OCVNTenderLocations.Location = OCVNTenderLocation;

export default OCVNTenderLocations;
