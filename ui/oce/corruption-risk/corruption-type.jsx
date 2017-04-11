import URI from "urijs";
import {Map} from "immutable";
import {pluckImm} from "../tools";
import CustomPopupChart from "./custom-popup-chart";
import Table from "../visualizations/tables/index";

const INDICATOR_NAMES = {
  i002: {
	  name: "Low Bid Price",
    description: "Winning supplier provides a substantially lower bid price than competitors"
  },
  i003: {
	  name: "Only Winner Eligible",
    description: "Only the winning bidder was eligible to have received the contract for a tender when 2+ bidders apply"
  },
  i004: {
	  name: "Ineligible Direct Award",
    description: "Sole source award is awarded above the competitive threshold despite legal requirements"
  },
  i007: {
	  name: "Single Bidder Only",
    description: "This awarded competitive tender only featured a single bid "
  },
  i019: {
	  name: "Contract Negotiation Delay",
    description: "Long delays in contract negotiations or award (as bribe demands are possibly negotiated)"
  },
  i038: {
	  name: "Short Bid Period",
    description: "Bid period is shorter than 7 number of days "
  },
  i077: {
	  name: "Multiple Contract Winner",
    description: "High number of contract awards to one supplier within a given time period by a single procurement entity"
  },
  i083: {
	  name: "Winner-Loser Pattern",
    description: "When X supplier wins, same tenderers always lose (this could be linked to a certain PE)"
  },
  i085: {
	  name: "Whole % Bid Prices",
    description: "Difference between bid prices is an exact percentage (whole number)"
  },
  i171: {
	  name: "Bid Near Estimate",
    description: "Winning bid is within 1% of estimated price"
  },
  i180: {
	  name: "Multiple Direct Awards",
    description: "Supplier receives multiple single-source/non-competitive contracts from a single procuring entity during a defined time period"
  }
};

class IndicatorTile extends CustomPopupChart{
  getCustomEP(){
    const {indicator} = this.props;
    return `flags/${indicator}/stats`;
  }

  getData(){
    const data = super.getData();
    if(!data) return [];
    const sortedData = data.sort((a, b) => a.get('year') - b.get('year'));
    return [{
      x: sortedData.map(pluckImm('year')).toJS(),
      y: sortedData.map(pluckImm('totalTrue')).toJS(),
      type: 'scatter',
      fill: 'tozerox'
    }];
  }

  getLayout(){
    return {
      xaxis: {
        type: 'category',
        showgrid: false,
        showline: false
      },
      yaxis: {
        zeroline: false,
        showline: false,
        showticklabels: false,
        showgrid: false
      }
    }
  }

  getPopup(){
    const {indicator} = this.props;
    const {popup} = this.state;
    const {year} = popup;
    const data = super.getData();
    const datum = data.find(datum => datum.get('year') == year);
    return (
      <div className="crd-popup" style={{top: popup.top, left: popup.left}}>
        <div className="row">
          <div className="col-sm-12 info text-center">
            {year}
          </div>
          <div className="col-sm-12">
            <hr/>
          </div>
          <div className="col-sm-7 text-right title">Projects Flagged</div>
          <div className="col-sm-5 text-left info">{datum.get('totalTrue')}</div>
          <div className="col-sm-7 text-right title">Eligible Projects</div>
          <div className="col-sm-5 text-left info">{datum.get('totalPrecondMet')}</div>
          <div className="col-sm-7 text-right title">Eligible Projects %</div>
          <div className="col-sm-5 text-left info">{datum.get('percentPrecondMet').toFixed(2)} %</div>
          <div className="col-sm-7 text-right title">Total Eligible %</div>
          <div className="col-sm-5 text-left info">{datum.get('percentTruePrecondMet').toFixed(2)} %</div>
        </div>
        <div className="arrow"/>
      </div>
    )
  }
}

class Crosstab extends Table{
  getCustomEP(){
    const {indicators} = this.props;
    return indicators.map(indicator => `flags/${indicator}/crosstab`);
  }

  componentDidUpdate(prevProps, ...args){
    if(this.props.indicators != prevProps.indicators){
      this.fetch();
    }
    super.componentDidUpdate(prevProps, ...args);
  }

  transform(data){
    const {indicators} = this.props;
    let matrix = {}, x = 0, y = 0;
    for(x = 0; x<indicators.length; x++){
      const xIndicatorID = indicators[x];
      matrix[xIndicatorID] = {};
      const datum = data[x][0];
      for(y = 0; y<indicators.length; y++){
        const yIndicatorID = indicators[y];
        matrix[xIndicatorID][yIndicatorID] = {
          count: datum[yIndicatorID],
          percent: datum.percent[yIndicatorID]
        }
      }
    }
    return matrix;
  }

  row(rowData, rowIndicatorID){
    const {name: rowIndicatorName, description: rowIndicatorDecription} = INDICATOR_NAMES[rowIndicatorID];
    return (
      <tr key={rowIndicatorID}>
        <td>{rowIndicatorName}</td>
        <td className="nr-flags">{rowData.getIn([rowIndicatorID, 'count'])}</td>
        {rowData.map((datum, indicatorID) => {
           const {name: indicatorName, description: indicatorDecription} = INDICATOR_NAMES[indicatorID];
           if(indicatorID == rowIndicatorID){
             return <td className="not-applicable" key={indicatorID}>&mdash;</td>
           } else {
             const percent = datum.get('percent');
             const count = datum.get('count');
             const color = Math.round(255 - 255 * (percent/100))
             const style = {backgroundColor: `rgb(${color}, 255, ${color})`}
             return (
               <td key={indicatorID} className="hoverable" style={style}>
                 {percent.toFixed(2)} %
                 <div className="crd-popup text-left">
                   <div className="row">
                     <div className="col-sm-12 info">
                       {percent.toFixed(2)}% of projects flagged for "{rowIndicatorName}" are also flagged for "{indicatorName}"
                     </div>
                     <div className="col-sm-12">
                       <hr/>
                     </div>
                     <div className="col-sm-12 info">
                       <h4>{count} Projects flagged with both;</h4>
                       <p><strong>{rowIndicatorName}</strong>: {rowIndicatorDecription}</p>
                       <p className="and">and</p>
                       <p><strong>{indicatorName}</strong>: {indicatorDecription}</p>
                     </div>
                   </div>
                   <div className="arrow"/>
                 </div>
               </td>
             )
           }
         }).toArray()}
      </tr>
    )
  }

  render(){
    const {indicators, data} = this.props;
    if(!data) return null;
    if(!data.count()) return null;
    return (
      <table className="table table-striped table-hover table-bordered table-crosstab">
        <thead>
          <tr>
            <th></th>
            <th># Flags</th>
            {data.map((_, indicatorID) => <th key={indicatorID}>{INDICATOR_NAMES[indicatorID].name}</th>).toArray()}
          </tr>
        </thead>
        <tbody>
          {data.map((rowData, indicatorID) => this.row(rowData, indicatorID)).toArray()}
        </tbody>
      </table>
    )
  }
}

class CorruptionType extends React.Component{
  constructor(...args){
    super(...args);
    this.state = {
      indicatorTiles: {}
    }
  }

  updateIndicatorTile(indicator, data){
    let {indicatorTiles} = this.state;
    indicatorTiles[indicator] = data;
    this.setState({indicatorTiles})
  }

  render(){
    const {indicators, onGotoIndicator} = this.props;
    if(!indicators || !indicators.length) return null;
    const {crosstab, indicatorTiles} = this.state;
    return (
      <div className="page-corruption-type">
        <div className="row">
	        {indicators.map((indicator, index) => {
             const {name: indicatorName, description: indicatorDescription} = INDICATOR_NAMES[indicator];
             return (
               <div className="col-sm-4 indicator-tile-container" key={index} onClick={e => onGotoIndicator(indicator)}>
                 <div className="border">
                   <h4>{indicatorName}</h4>
                   <p>{indicatorDescription}</p>
                   <IndicatorTile
                       indicator={indicator}
                       translations={{}}
                       filters={Map()}
                       requestNewData={(_, data) => this.updateIndicatorTile(indicator, data)}
                       data={indicatorTiles[indicator]}
                       margin={{t: 0, r: 0, b: 40, l: 0, pad: 0}}
                       height={300}
    	             />
                 </div>
               </div>
             )
	         })}
        </div>
        <h4>Fraud Crosstab</h4>
        <p>This tool helps users understand the overlap between any two fraud indicators</p>
        <Crosstab
            filters={Map()}
            years={Map()}
            indicators={indicators}
            data={crosstab}
            requestNewData={(_, data) => this.setState({crosstab: data})}
        />
      </div>
    )
  }
}

export default CorruptionType;
