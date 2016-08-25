import Map from "../../../oce/visualizations/map";

class PlannedLocations extends Map{
  transform(data){
    return data.filter(location => {
      if(!location['budget.projectLocation'].geometry){
        console.warn("Invalid delivery location! Missing geometry!", location);
        return false;
      }
      return true;
    });
  }


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