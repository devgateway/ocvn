import Plotly from "plotly.js/lib/core";
import Chart from "../visualizations/charts/index.jsx";
import {pluckImm, debounce} from "../tools";
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
        font: {
          size: 9
        },
        x: 0.15,
        y: -0.2,
        tracegroupgap: 1
      },
      paper_bgcolor: 'rgba(0,0,0,0)'
    }
  }
}

TotalFlagsChart.endpoint = 'totalFlaggedIndicatorsByIndicatorType';

class Counter extends backendYearFilterable(Visualization){
  render(){
    const {data, width} = this.props;
    if(!data) return null;
    return (
      <h4 className="total-flags-counter" style={{width}}>
        Total flags: {data.getIn([0, 'flaggedCount'], 0)} 
      </h4>
    )
  }
}

Counter.endpoint = 'totalFlags';

class TotalFlags extends React.Component{
  constructor(...args){
    super(...args);
    this.state = {
    }

    this.updateSidebarWidth = debounce(() =>
      this.setState({
        width: document.getElementById('crd-sidebar').offsetWidth - 30
      })
    );
  }


  componentDidMount(){
    this.updateSidebarWidth();
    window.addEventListener("resize", this.updateSidebarWidth);
  }

  componentWillUnmount(){
    window.removeEventListener("resize", this.updateSidebarWidth)
  }

  render(){
    const {data, requestNewData, translations, filters, years, months, monthly} = this.props;
    const {width} = this.state;
    if(!width) return null;
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
          width={width}
        />
        <TotalFlagsChart
          data={data.get('chart')}
          requestNewData={(_, data) => requestNewData(['chart'], data)}
          translations={translations}
          width={width}
          height={250}
          margin={{l:0, r:0, t: 40, b: 0, pad:0}}
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
