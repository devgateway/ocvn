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
      this.mkTrace(this.t('charts:costEffectiveness:traces:awardPrice'), 0),
      this.mkTrace(this.t('charts:costEffectiveness:traces:difference'), 1)
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
      barmode: "relative",
      xaxis: {
        title: this.t('charts:costEffectiveness:xAxisTitle'),
        type: "category"
      },
      yaxis: {
        title: this.t('charts:costEffectiveness:yAxisTitle')
      }
    }
  }
}

CostEffectiveness.getName = t => t('charts:costEffectiveness:title');
CostEffectiveness.endpoint = 'costEffectivenessTenderAwardAmount';
CostEffectiveness.excelEP = 'costEffectivenessExcelChart';
CostEffectiveness.getFillerDatum = year => Map({
  year,
  tender: 0,
  diff: 0
});

CostEffectiveness.getMaxField = imm => imm.get('tender') + imm.get('diff');

export default CostEffectiveness;
