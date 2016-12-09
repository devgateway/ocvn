import FrontendYearFilterableChart from "../frontend-filterable";
import {pluckImm} from "../../../tools";

class CancelledPercents extends FrontendYearFilterableChart{
  getData(){
    let data = super.getData();
    if(!data) return [];
    return [{
      x: data.map(pluckImm('year')).toArray(),
      y: data.map(pluckImm('percentCancelled')).toArray(),
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
        title: this.t('charts:cancelledPercents:xAxisName'),
        type: 'category'
      },
      yaxis: {
        title: this.t('charts:cancelledPercents:yAxisName'),
        hoverformat: '.2f'
      }
    }
  }
}

CancelledPercents.endpoint = 'percentTendersCancelled';
CancelledPercents.excelEP = 'cancelledFundingPercentageExcelChart';
CancelledPercents.getMaxField = imm => imm.get('percentCancelled');

export default CancelledPercents;


