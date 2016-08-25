import Location from "../../oce/tabs/location";
import PlannedLocations from '../visualizations/map/planned-locations';
import OCVNTenderLocations from '../visualizations/map/tender-locations';

class OCVNLocation extends Location{}

OCVNLocation.LAYERS = [PlannedLocations, OCVNTenderLocations];

export default OCVNLocation;