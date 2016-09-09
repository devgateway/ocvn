import FrontendYearFilterableChart from "./frontend-filterable";
import {pluckImm} from "../../tools";
import {Map} from "immutable";
import Plotly from "plotly.js/lib/core";

class AvgNrBids extends FrontendYearFilterableChart{
  getData(){
    let data = super.getData();
    if(!data) return [];
    return [{
      x: data.map(pluckImm('year')).toArray(),
      y: data.map(pluckImm('averageNoTenderers')).toArray(),
      type: 'bar',
      marker: {
        color: this.props.styling.charts.traceColors[0]
      }
    }];
  }

  getLayout(){
    return {
      xaxis: {
        title: this.__("Year"),
        type: "category"
      },
      yaxis: {
        title: this.__("Number"),
        hoverformat: '.2f'
      }
    }
  }
}

AvgNrBids.endpoint = 'averageNumberOfTenderers';
AvgNrBids.excelEP = 'averageNumberBidsExcelChart';
AvgNrBids.getName = __ => __('Average number of bids');
AvgNrBids.getFillerDatum = year => Map({
  year,
  averageNoTenderers: 0
});
export default AvgNrBids;