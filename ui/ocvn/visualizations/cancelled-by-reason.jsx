import FrontendYearFilterableChart from "../../oce/visualizations/charts/frontend-filterable";

class CancelledFunding extends FrontendYearFilterableChart{
  static getName(t){return t('charts:cancelledFunding:title')}

  getData(){
    let data = super.getData();
    if(!data) return [];
    let {traceColors, hoverFormatter} = this.props.styling.charts;
    let trace = {
      x: [],
      y: [],
      type: 'bar',
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
      let totalCancelledTendersAmount = datum.get('totalCancelledTendersAmount');
      trace.x.push(year);
      trace.y.push(totalCancelledTendersAmount);
      if(hoverFormatter) trace.text.push(hoverFormatter(totalCancelledTendersAmount));
    });

    return [trace];
  }


  getLayout(){
    return {
      xaxis: {
        title: this.t('charts:cancelledFunding:xAxisTitle'),
        type: 'category'
      },
      yaxis: {
        title: this.t('charts:cancelledFunding:yAxisTitle')
      }
    }
  }
}

CancelledFunding.endpoint = 'totalCancelledTendersByYearByRationale';
CancelledFunding.excelEP = 'cancelledTendersByYearByRationaleExcelChart';

export default CancelledFunding;
