import Overview from "../../oce/visualizations/charts/overview";
import {pluckImm, response2obj} from "../../oce/tools";

class OCVNOverview extends Overview{
  transform([bidplansResponse, tendersResponse, awardsResponse]){
    const transformed = super.transform([tendersResponse, awardsResponse]);
    const bidplans = response2obj('count', bidplansResponse);
    return transformed.map(datum => {
      datum.bidplan = bidplans[datum.year];
      return datum;
    })
  }

  getData(){
    const data = super.getRawData();
    if(!data) return [];
    return [{
      x: data.map(pluckImm('year')).toArray(),
      y: data.map(pluckImm('bidplan')).toArray(),
      type: 'scatter',
      name: this.t('charts:overview:traces:bidplan'),
      marker: {
        color: this.props.styling.charts.traceColors[2]
      }
    }].concat(super.getData());
  }
}

OCVNOverview.endpoints = ['countBidPlansByYear'].concat(Overview.endpoints);

export default OCVNOverview;