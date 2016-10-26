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
        title: this.t('charts:nrEBid:xAxisTitle'),
        type: 'category'
      },
      yaxis: {
        title: this.t('charts:nrEBid:yAxisTitle'),
        hoverformat: '.2f'
      }
    }
  }
}

NrEbid.endpoint = 'percentTendersUsingEBid';
NrEbid.excelEP = 'numberTendersUsingEBidExcelChart';
NrEbid.getName = t => t('charts:nrEBid:title');
NrEbid.getMaxField = pluckImm('totalTendersUsingEbid');

export default NrEbid;
