import FrontendYearFilterableChart from "./frontend-filterable";
import {pluckImm} from "../../tools";

class NrCancelled extends FrontendYearFilterableChart{
  static getName(__){
    return __('Number of cancelled bids');
  }
    
  getData(){
    let data = super.getData();
    if(!data) return [];
    return [{
      x: data.map(pluckImm('year')).toArray(),
      y: data.map(pluckImm('totalCancelled')).toArray(),
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

NrCancelled.endpoint = 'percentTendersCancelled';
NrCancelled.getMaxField = imm => imm.get('totalCancelled');

export default NrCancelled;


