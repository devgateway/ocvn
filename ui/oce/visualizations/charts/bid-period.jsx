import FrontendYearFilterableChart from "./frontend-filterable";
import {pluckImm, response2obj} from "../../tools";
import {Map} from "immutable";

let ensureNonNegative = a => a < 0 ? 0 : a;

class BidPeriod extends FrontendYearFilterableChart {
  transform([tenders, awards]) {
    let awardsHash = response2obj('averageAwardDays', awards);
    return tenders.map(tender => ({
      year: tender._id,
      tender: +tender.averageTenderDays,
      award: +(awardsHash[tender._id] || 0)
    }))
  };

  getRawData(){
    return super.getData();
  }  

  getData() {
    let data = super.getData();
    if (!data) return [];
    let years = data.map(pluckImm('year')).toArray();
    return [{
      x: data.map(pluckImm('tender')).map(ensureNonNegative).toArray(),
      y: years,
      name: this.__("Tender"),
      type: "bar",
      orientation: 'h',
      marker: {
        color: this.props.styling.charts.traceColors[0]
      }
    }, {
      x: data.map(pluckImm('award')).map(ensureNonNegative).toArray(),
      y: years,
      name: this.__("Award"),
      type: "bar",
      orientation: 'h',
      marker: {
        color: this.props.styling.charts.traceColors[1]
      }
    }];
  }

  getLayout() {
    let annotations = [];
    let data = super.getData();
    if(data){
      annotations = data.map((imm, index) => {
			  let sum = imm.reduce((sum, val, key) => "year" == key ? sum : sum + ensureNonNegative(val), 0).toFixed(2);
        return {
          y: index,
          x: sum,
          xanchor: 'left',
          yanchor: 'middle',
          text: this.__('Total:') + ' ' + sum,
          showarrow: false
        }
      }).toArray();
    }

    return {
      annotations,
      barmode: "stack",
      xaxis: {
        title: this.__("Number of days"),
        hoverformat: '.2f'
      },
      yaxis: {
        title: this.__("Year"),
        type: "category"
      }
    }
  }
}

BidPeriod.endpoints = ['averageTenderPeriod', 'averageAwardPeriod'];
BidPeriod.excelEP = 'bidTimelineExcelChart';
BidPeriod.getName = __ => __('Bid Timeline');
BidPeriod.horizontal = true;
BidPeriod.getFillerDatum = year => Map({
  year,
  tender: 0,
  award: 0
});
BidPeriod.getMaxField = imm => imm.get('tender', 0) + imm.get('award', 0);

export default BidPeriod;
