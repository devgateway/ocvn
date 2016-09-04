import FrontendYearFilterableChart from "../../oce/visualizations/charts/frontend-filterable";

class CancelledFunding extends FrontendYearFilterableChart{
  static getName(__){
    return __(' Cancelled funding by reason');
  }

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

    for(let datum of data){
      let year = datum.get('year');
      let totalCancelledTendersAmount = datum.get('totalCancelledTendersAmount');
      trace.x.push(year);
      trace.y.push(totalCancelledTendersAmount);
      if(hoverFormatter) trace.text.push(hoverFormatter(totalCancelledTendersAmount));
    }

    return [trace];
  }


  getLayout(){
    return {
      xaxis: {
        title: this.__("Year"),
        type: 'category'
      },
      yaxis: {
        title: this.__("Amount (in VND)")
      }
    }
  }
}

CancelledFunding.endpoint = 'totalCancelledTendersByYearByRationale';
CancelledFunding. = 'cancelledTendersByYearByRationaleExcelChart';

export default CancelledFunding;
