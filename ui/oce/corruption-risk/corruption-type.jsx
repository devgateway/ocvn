import URI from "urijs";
import {Map} from "immutable";
import {pluckImm} from "../tools";
import CustomPopupChart from "./custom-popup-chart";
import Table from "../visualizations/tables/index";
import INDICATOR_NAMES from "./indicator-names";

const CORRUPTION_TYPE_DESCRIPTION = {
  fraud: {
    introduction: `Fraud is “Any act or omission, including a misrepresentation, that knowingly or recklessly misleads, or attempts to mislead, a party to obtain a financial or other benefit or to avoid an obligation, according to the International Financial Institutions (IFI) Anti-Corruption Task Force Guidelines. Suppliers may engage in a variety of fraudulent activities in attempt to increase their chances at winning contracts or in demonstration of progress in implementation. Some of these fraud schemes include: false invoicing, false bidding, product substitution, fictitious contracting, and shadow bidding. The flags represented in each tile below indicate the possible existance of fraud in a contracting process. To learn more about each flag, click on the associated tile.`,
    crosstab: `The table below shows the overlap between any two fraud flags. Darker colored cells indicate a higher percentage of overlap between the corresponding flags. Projects with more fraud flags may be at a higher risk for fraud and may warrant additional investigation. Hover the mouse over a cell to learn more about the number of projects that have been flagged and the corresponding flags.`
  },
  process_rigging: {
    introduction: `"Process rigging" is a term used here to refer to an effort to rig the bidding, awarding, contracting or implementation process in favor of a particular player or entity to the exclusion of other legitimate participants. Implicit in this definition is the possible role of government officials, who either initiate the corrupt act or distort the procurement process to facilitate said act. Specific forms of process rigging may include: information withholding or misinformation, riged specifications, unjustified direct (sole source) contracting, split purchasing, bid manipulation, favoring/excluding qualified bidders, change order abuse, and contracting abuse`,
    crosstab: `The table below shows the overlap between any two process rigging flags. Darker colored cells indicate a higher percentage of overlap between the corresponding flags. Projects with more process rigging flags may be at a higher risk for process rigging and may warrant additional investigation. Hover the mouse over a cell to learn more about the number of projects that have been flagged and the corresponding flags.`
  },
  collusion: {
    introduction: `IFI Guidelines define collusive practices as: “…an arrangement between two or more parties designed to achieve an improper purpose, including to influence improperly the actions of another party.” Here, we focus on collusive behavior between and among bidders; not betwen bidders and government officials (for this, see the process rigging page).`,
    crosstab: `The table below shows the overlap between any two collusion flags. Darker colored cells indicate a higher percentage of overlap between the corresponding flags. Projects with more collusion flags may be at a higher risk for collusion and may warrant additional investigation. Hover the mouse over a cell to learn more about the number of projects that have been flagged and the corresponding flags.`
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
        if(datum){
          matrix[xIndicatorID][yIndicatorID] = {
            count: datum[yIndicatorID],
            percent: datum.percent[yIndicatorID]
          }
        } else {
          matrix[xIndicatorID][yIndicatorID] = {
            count: 0,
            percent: 0
          }
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
    const {indicators, onGotoIndicator, corruptionType} = this.props;
    const {crosstab, indicatorTiles} = this.state;
    if(!indicators || !indicators.length) return null;
    return (
      <div className="page-corruption-type">
        <blockquote>{CORRUPTION_TYPE_DESCRIPTION[corruptionType].introduction}</blockquote>
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
        <blockquote>{CORRUPTION_TYPE_DESCRIPTION[corruptionType].crosstab}</blockquote>
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
