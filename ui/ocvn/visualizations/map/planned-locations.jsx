import Map from "./index.jsx";

class PlannedLocations extends Map{
  getData(){
    let data = super.getData();
    if(!data) return [];
    return data
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
        .toArray()
  }

  static getLayerName(__){
    return __('Planning');
  }
}

PlannedLocations.endpoint = 'plannedFundingByLocation';

export default PlannedLocations;
