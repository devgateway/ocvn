import FrontendYearFilterableChart from "./frontend-filterable";
import {response2obj, pluckImm} from "../../tools";

class OverviewChart extends FrontendYearFilterableChart{
  transform([bidplansResponse, tendersResponse, awardsResponse]){
    let bidplans = response2obj('count', bidplansResponse);
    let tenders = response2obj('count', tendersResponse);
    let awards = response2obj('count', awardsResponse);
    return Object.keys(tenders).map(year => ({
      year: year,
      bidplan: bidplans[year],
      tender: tenders[year],
      award: awards[year]
    }));
  }

  getData(){
    var data = super.getData();
    if(!data) return [];
    let LINES = {
      award: this.__("Award"),
      bidplan: this.__("Bidplan"),
      tender: this.__("Tender")
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
        title: this.__("Year"),
        type: "category"
      },
      yaxis: {
        title: this.__("Count"),
        exponentformat: 'none'
      }
    }
  }
}

OverviewChart.endpoints = ['countBidPlansByYear', 'countTendersByYear', 'countAwardsByYear'];
OverviewChart.excelEP = 'procurementActivityExcelChart';

OverviewChart.getName = __ => __("Procurement activity by year");

export default OverviewChart;
