import Plotly from "plotly.js/lib/core";
import Chart from "../visualizations/charts/index.jsx";
import {pluckImm} from "../tools";
import backendYearFilterable from "../backend-year-filterable";
import Visualization from "../visualization";
import {fromJS} from "immutable";

Plotly.register([
  require('plotly.js/lib/pie')
]);

class TotalFlagsChart extends backendYearFilterable(Chart){
  getData(){
		const data = super.getData();
		if(!data || !data.count()) return [];
    return [{
      values: data.map(pluckImm('indicatorCount')).toJS(),
      labels: data.map(pluckImm('type')).toJS(),
      textinfo: 'value',
      hole: .85,
      type: 'pie',
			marker: {
				colors: ['#fac329', '#289df5', '#3372b1']
			}
    }];
  }

  getLayout(){
    const {width} = this.props;
    return {
      legend: {
        orientation: 'h',
				width: 500,
        height: 50,
        x: '0',
        y: '0'
      },
      paper_bgcolor: 'rgba(0,0,0,0)'
    }
  }
}

TotalFlagsChart.endpoint = 'totalFlaggedIndicatorsByIndicatorType';

class Counter extends backendYearFilterable(Visualization){
  render(){
    const {data} = this.props;
    if(!data) return null;
    return (
      <h4 className="total-flags-counter">
        Total flags: {data.getIn([0, 'flaggedCount'], 0)} 
      </h4>
    )
  }
}

Counter.endpoint = 'totalFlags';

class TotalFlags extends React.Component{
  render(){
    const {data, requestNewData, translations, filters, years, months, monthly} = this.props;
    return (
      <div className="total-flags">
        <Counter
            data={data.get('counter')}
            requestNewData={(_, data) => requestNewData(['counter'], data)}
            translations={translations}
            filters={filters}
            years={years}
            months={months}
            monthly={monthly}
        />
        <TotalFlagsChart
            data={data.get('chart')}
            requestNewData={(_, data) => requestNewData(['chart'], data)}
            translations={translations}
            width={250}
            height={250}
            margin={{l:40, r:40, t:40, b: 10, pad:20}}
            filters={filters}
            years={years}
            months={months}
            monthly={monthly}
        />
      </div>
    )
  }
}

export default TotalFlags;
