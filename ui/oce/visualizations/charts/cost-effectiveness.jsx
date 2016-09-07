import FrontendYearFilterableChart from "./frontend-filterable";
import {Map} from "immutable";

class CostEffectiveness extends FrontendYearFilterableChart{
  transform(data){
    return data.map(datum => ({
      year: datum._id,
      tender: datum.totalTenderAmount,
      diff: datum.diffTenderAwardAmount
    }));
  }

  mkTrace(name, colorIndex){
    let {traceColors, hoverFormatter} = this.props.styling.charts;
    let trace = {
      x: [],
      y: [],
      text: [],
      name,
      type: 'bar',
      marker: {
        color: traceColors[colorIndex]
      }
    };

    if(hoverFormatter) trace.hoverinfo = "text+name";

    return trace;
  }

  getData(){
    let data = super.getData();
    if(!data) return [];
    let traces = [
      this.mkTrace(this.__('Award Price'), 0),
      this.mkTrace(this.__('Difference'), 1)
    ];

    let {hoverFormatter} = this.props.styling.charts;

    for(let datum of data){
      let year = datum.get('year');
      traces.forEach(trace => trace.x.push(year));
      let tender = datum.get('tender');
      let diff = datum.get('diff');
      traces[0].y.push(tender);
      traces[1].y.push(diff);
      if(hoverFormatter){
        traces[0].text.push(hoverFormatter(tender));
        traces[1].text.push(hoverFormatter(diff));
      }
    }

    return traces;
  }

  getLayout(){
    return {
      barmode: "stack",
      xaxis: {
        title: this.__("Year"),
        type: "category"
      },
      yaxis: {
        title: this.__("Amount (in VND)")
      }
    }
  }
}

CostEffectiveness.getName = __ => __('Cost effectiveness');
CostEffectiveness.endpoint = 'costEffectivenessTenderAwardAmount';
CostEffectiveness.excelEP = 'costEffectivenessExcelChart';
CostEffectiveness.getFillerDatum = year => Map({
  year,
  tender: 0,
  diff: 0
});

CostEffectiveness.getMaxField = imm => imm.get('tender') + imm.get('diff');

export default CostEffectiveness;
