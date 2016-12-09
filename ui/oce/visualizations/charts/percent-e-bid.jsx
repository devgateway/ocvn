import FrontendYearFilterableChart from "./frontend-filterable";
import {pluckImm} from "../../tools";

class PercentEbid extends FrontendYearFilterableChart{
  getData(){
    let data = super.getData();
    if(!data) return [];
    return [{
      x: data.map(pluckImm('year')).toArray(),
      y: data.map(pluckImm('percentageTendersUsingEbid')).toArray(),
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
        title: this.t('charts:percentEBid:xAxisName'),
        type: 'category'
      },
      yaxis: {
        title: this.t('charts:percentEBid:yAxisName'),
        hoverformat: '.2f'
      }
    }
  }
}

PercentEbid.endpoint = 'percentTendersUsingEBid';
PercentEbid.excelEP = 'percentTendersUsingEBidExcelChart';
PercentEbid.getName = t => t('charts:percentEBid:title');
PercentEbid.getMaxField = pluckImm('percentageTendersUsingEbid');

export default PercentEbid;