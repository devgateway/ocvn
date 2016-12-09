import FrontendYearFilterableChart from "./frontend-filterable";
import {response2obj, pluckImm} from "../../tools";

class OverviewChart extends FrontendYearFilterableChart{
  transform([tendersResponse, awardsResponse]){
    let tenders = response2obj('count', tendersResponse);
    let awards = response2obj('count', awardsResponse);
    return Object.keys(tenders).map(year => ({
      year: year,
      tender: tenders[year],
      award: awards[year]
    }));
  }

  getRawData(){
    return super.getData();
  }

  getData(){
    var data = super.getData();
    if(!data) return [];
    let LINES = {
      award: this.t('charts:overview:traces:award'),
      tender: this.t('charts:overview:traces:tender')
    };
    let years = data.map(pluckImm('year')).toArray();
    return Object.keys(LINES).map((key, index) => ({
          x: years,
          y: data.map(pluckImm(key)).toArray(),
          type: 'scatter',
          name: LINES[key],
          marker: {
            color: this.props.styling.charts.traceColors[index]
          }
        })
    );
  }

  getLayout(){
    return {
      xaxis: {
        title: this.t('charts:overview:xAxisName'),
        type: "category"
      },
      yaxis: {
        title: this.t('charts:overview:yAxisName'),
        exponentformat: 'none'
      }
    }
  }
}

OverviewChart.endpoints = ['countTendersByYear', 'countAwardsByYear'];
OverviewChart.excelEP = 'procurementActivityExcelChart';

OverviewChart.getName = t => t('charts:overview:title');

export default OverviewChart;
