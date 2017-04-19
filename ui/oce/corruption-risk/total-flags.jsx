import Plotly from "plotly.js/lib/core";
import Chart from "../visualizations/charts/index.jsx";
import {pluckImm} from "../tools";
import backendYearFilterable from "../backend-year-filterable";
import Visualization from "../visualization";

Plotly.register([
  require('plotly.js/lib/pie')
]);

class TotalFlags extends backendYearFilterable(Chart){
  getData(){
		const data = super.getData();
		if(!data) return [];
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

TotalFlags.endpoint = 'totalFlaggedIndicatorsByIndicatorType';

class TotalFlagsCounter extends backendYearFilterable(Visualization){
	render(){
		const {data} = this.props;
		if(!data) return null;
		console.log(data.toJS());
		return (
			<div className="totalFlagsCounter">
				
			</div>
		)
	}
}

TotalFlagsCounter.endpoint = 'totalFlags';

export {TotalFlags, TotalFlagsCounter};
