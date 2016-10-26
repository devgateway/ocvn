import FrontendYearFilterableChart from "./frontend-filterable";
import {pluckImm} from "../../tools";

class NrCancelled extends FrontendYearFilterableChart{
  static getName(t){return t('charts:nrCancelled:title')};

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
        title: this.t('charts:nrCancelled:xAxisTitle'),
        type: 'category'
      },
      yaxis: {
        title: this.t('charts:nrCancelled:yAxisTitle'),
        hoverformat: '.2f'
      }
    }
  }
}

NrCancelled.endpoint = 'percentTendersCancelled';
NrCancelled.excelEP = 'numberCancelledFundingExcelChart';
NrCancelled.getMaxField = imm => imm.get('totalCancelled');

export default NrCancelled;


