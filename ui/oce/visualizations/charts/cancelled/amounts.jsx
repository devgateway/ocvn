import FrontendYearFilterableChart from "../frontend-filterable";
import {pluckImm} from "../../../tools";

class CancelledFunding extends FrontendYearFilterableChart{
  transform(data){
    return data.map(({_id, totalCancelledTendersAmount}) => ({
      year: _id,
      count: totalCancelledTendersAmount
    }));
  }

  getData(){
    let data = super.getData();
    if(!data) return [];
    let {traceColors, hoverFormatter} = this.props.styling.charts;
    let trace = {
      x: [],
      y: [],
      type: 'scatter',
      fill: 'tonexty',
      marker: {
        color: traceColors[0]
      }
    };

    if(hoverFormatter){
      trace.text = [];
      trace.hoverinfo = "text";
    }

    data.forEach(datum => {
      let year = datum.get('year');
      let count = datum.get('count');
      trace.x.push(year);
      trace.y.push(count);
      if(hoverFormatter) trace.text.push(hoverFormatter(count));
    });

    return [trace];
  }

  getLayout(){
    return {
      xaxis: {
        title: this.t('charts:cancelledAmounts:xAxisName'),
        type: 'category'
      },
      yaxis: {
        title: this.t('charts:cancelledAmounts:yAxisName')
      }
    }
  }
}

CancelledFunding.endpoint = 'totalCancelledTendersByYear';
CancelledFunding.excelEP = 'cancelledFundingExcelChart';

export default CancelledFunding;
