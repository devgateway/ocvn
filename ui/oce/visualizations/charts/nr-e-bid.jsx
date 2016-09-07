import FrontendYearFilterableChart from "./frontend-filterable";
import {pluckImm} from "../../tools";

class NrEbid extends FrontendYearFilterableChart{
  getData(){
    let data = super.getData();
    if(!data) return [];
    return [{
      x: data.map(pluckImm('year')).toArray(),
      y: data.map(pluckImm('totalTendersUsingEbid')).toArray(),
      type: 'scatter',
      fill: 'tonexty',
      marker: {
        color: this.props.styling.charts.traceColors[0]
      }
    }];
  }

  getLayout(){
    return {
      xaxis: {
        title: this.__("Year"),
        type: 'category'
      },
      yaxis: {
        title: this.__("Count"),
        hoverformat: '.2f'
      }
    }
  }
}

NrEbid.endpoint = 'percentTendersUsingEBid';
NrEbid.getName = __ => __('Number of eBid Awards');
NrEbid.getMaxField = pluckImm('totalTendersUsingEbid');

export default NrEbid;
