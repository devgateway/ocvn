import Component from "../pure-render-component";
import { Map, TileLayer } from 'react-leaflet';
import Cluster from "./cluster";
import Location from "./location";
require("./style.less");

var aggregateLocations = locations => locations
    .groupBy(location => location.getIn(['budget.projectLocation', '_id']))
    .map(locations => locations.reduce((reducedLocation, location) => {
        return {
          "_id": location.getIn(['budget.projectLocation', '_id']),
          "name": location.getIn(['budget.projectLocation', 'description']),
          "amount": reducedLocation.amount + location.get('totalPlannedAmount'),
          "count": reducedLocation.count + location.get('recordsCount'),
          "coords": location.getIn(['budget.projectLocation', 'geometry', 'coordinates']).toJS()
        }
      }, {
        "amount": 0,
        "count": 0
      }))
    .toArray();

export default class Planning extends Component{
  render(){
    var {locations, years, translations} = this.props;
    var filteredLocations = locations.filter(location => years.get(location.get('year'), false));
    var aggregatedLocations = aggregateLocations(filteredLocations);
    var maxAmount = Math.max(0, ...aggregatedLocations.map(location => location.amount));
    return (
        <div className="col-sm-12 content map-content">
          <Map center={[14.5, 105]} zoom={5}>
            <TileLayer
                url='http://{s}.tile.osm.org/{z}/{x}/{y}.png'
                attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
            />
            <Cluster maxAmount={maxAmount}>
              {aggregatedLocations.map(location => (
                <Location
                    translations={translations}
                    key={location._id}
                    position={location.coords.reverse()}
                    maxAmount={maxAmount}
                    data={location}
                />
              ))}
            </Cluster>
          </Map>
        </div>
    )
  }
}