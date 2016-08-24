import FrontendYearFilterableChart from "./frontend-filterable";
import {pluckImm} from "../../tools";

class PercentEProcurement extends FrontendYearFilterableChart{
  getData(){
    let data = super.getData();
    if(!data) return [];
    return [{
      x: data.map(pluckImm('year')).toArray(),
      y: data.map(pluckImm('percentEgp')).toArray(),
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
        title: this.__("Percent"),
        hoverformat: '.2f'
      }
    }
  }
}


PercentEProcurement.endpoint = 'percentTendersUsingEgp';
PercentEProcurement.getName = __ => __('Percent of Tenders Using e-Procurement');
PercentEProcurement.getMaxField = imm => imm.get('percentEgp', 0);

export default PercentEProcurement;