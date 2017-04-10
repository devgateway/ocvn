import CustomPopupChart from "./custom-popup-chart";
import {Map} from "immutable";
import {pluckImm} from "../tools";
import Table from "../visualizations/tables/index";

class IndividualIndicatorChart extends CustomPopupChart{
  getCustomEP(){
    const {indicator} = this.props;
    return `flags/${indicator}/stats`;
  }

  getData(){
    const data = super.getData();
    if(!data) return [];
    const sortedData = data.sort((a, b) => a.get('year') - b.get('year'));
    const years = sortedData.map(pluckImm('year')).toJS();
    return [{
      x: years,
      y: sortedData.map(pluckImm('totalTrue')).toJS(),
      type: 'scatter',
      fill: 'tozerox',
      name: 'Flagged',
      hoverinfo: 'name',
      fillcolor: '#85cbfe',
      line: {
        color: '#85cbfe'
      }
    }, {
      x: years,
      y: sortedData.map(pluckImm('totalPrecondMet')).toJS(),
      type: 'scatter',
      fill: 'tonexty',
      name: 'Eligible to be flagged',
      hoverinfo: 'name',
      fillcolor: '#336ba6',
      line: {
        color: '#336ba6'
      }
    }];
  }

  getLayout(){
    return {
      showlegend: false,
      hovermode: 'closest',
      xaxis: {
        type: 'category',
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

class ProjectTable extends Table{
  getCustomEP(){
    const {indicator} = this.props;
    return `flags/${indicator}/releases?pageSize=10`;
  }

  row(entry, index){
    const tenderValue = entry.getIn(['tender', 'value']);
    const awardValue = entry.getIn(['awards', 0, 'value']);
    const tenderPeriod = entry.get('tenderPeriod');
    const startDate = new Date(tenderPeriod.get('startDate'));
    const endDate = new Date(tenderPeriod.get('endDate'));
    const flaggedStats = entry.get('flaggedStats');
    return (
      <tr key={index}>
        <td></td>
        <td>{entry.get('ocid')}</td>
        <td>{entry.get('title')}</td>
        <td>{entry.getIn(['procuringEntity', 'name'])}</td>
        <td>{tenderValue.get('amount')} {tenderValue.get('currency')}</td>
        <td>{awardValue.get('amount')} {awardValue.get('currency')}</td>
        <td>{startDate.toLocaleDateString()}&mdash;{endDate.toLocaleDateString()}</td>
        <td>{flaggedStats.get('type')}</td>
      </tr>
    )
  }

  render(){
    const {data} = this.props;
    if(!data) return null;
    return (
      <table className="table table-striped table-hover table-project-table">
        <thead>
          <tr>
            <th>Status</th>
            <th>Contract ID</th>
            <th>Title</th>
            <th>Procuring Entity</th>
            <th>Tender Amount</th>
            <th>Award Amount</th>
            <th>Tender Date</th>
            <th>Flag Type</th>
          </tr>
        </thead>
        <tbody>
          {data && data.map(this.row.bind(this))}
        </tbody>
      </table>
    )
  }
}

class IndividualIndicatorPage extends React.Component{
  constructor(...args){
    super(...args);
    this.state = {
    }
  }

  render(){
    const {chart, table} = this.state;
    const {indicator} = this.props;
    return (
      <div className="page-corruption-type">
        <h4>Individual Indicator Page</h4>
        <IndividualIndicatorChart
            indicator={indicator}
            translations={{}}
            filters={Map()}
            requestNewData={(_, data) => this.setState({chart: data})}
            data={chart}
        />
        <ProjectTable
            indicator={indicator}
            requestNewData={(_, data) => this.setState({table: data})}
            data={table}
            filters={Map()}
            years={Map()}
        />
      </div>
    )
  }
}

export default IndividualIndicatorPage;
