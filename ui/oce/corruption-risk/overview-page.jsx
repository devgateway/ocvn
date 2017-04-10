import {Map} from "immutable";
import {pluck} from "../tools";
import Table from "../visualizations/tables/index";
import ReactDOMServer from "react-dom/server";
import CustomPopupChart from "./custom-popup-chart";

const pluckObj = (field, obj) => Object.keys(obj).map(key => obj[key][field]);

class CorruptionType extends CustomPopupChart{
  groupData(data){
    let grouped = {};

    data.forEach(datum => {
      const type = datum.get('type');
      const year = datum.get('year');
      grouped[type] = grouped[type] || {};
      grouped[type][year] = datum.toJS();
    });

    return grouped;
  }

  getData(){
    const data = super.getData();
    if(!data) return [];
    const grouped = this.groupData(data);
    return Object.keys(grouped).map(type => {
      const dataForType = grouped[type];
      return {
        x: pluckObj('year', dataForType),
        y: pluckObj('indicatorCount', dataForType),
        type: 'scatter',
        name: type
      }
    });
  }

  getLayout(){
    return {
      hovermode: 'closest',
      xaxis: {
        type: 'category'
      }
    }
  }

  getPopup(){
    const {popup} = this.state;
    const {year, traceName: corruptionType} = popup;
    const data = this.groupData(super.getData());
    const dataForPoint = data[corruptionType][year];
    return (
      <div className="crd-popup" style={{top: popup.top, left: popup.left}}>
        <div className="row">
          <div className="col-sm-12 info text-center">
            {year}
          </div>
          <div className="col-sm-12">
            <hr/>
          </div>
          <div className="col-sm-7 text-right title">Indicators</div>
          <div className="col-sm-5 text-left info">{dataForPoint.indicatorCount}</div>
          <div className="col-sm-7 text-right title">Flags</div>
          <div className="col-sm-5 text-left info">{dataForPoint.flaggedProjectCount}</div>
          <div className="col-sm-7 text-right title">Projects</div>
          <div className="col-sm-5 text-left info">{dataForPoint.projectCount}</div>
          <div className="col-sm-7 text-right title">% of Projects Flagged</div>
          <div className="col-sm-5 text-left info">{dataForPoint.percent.toFixed(2)}%</div>
        </div>
        <div className="arrow"/>
      </div>
    )
  }
}

CorruptionType.endpoint = 'percentTotalProjectsFlaggedByYear';

class TopFlaggedContracts extends Table{
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
        <td>{flaggedStats.get('count')}</td>
      </tr>
    )
  }

  render(){
    const {data} = this.props;
    return (
        <table className="table table-striped table-hover table-top-flagged-contracts">
          <thead>
          <tr>
            <th>Status</th>
            <th>Contract ID</th>
            <th>Title</th>
            <th>Procuring Entity</th>
            <th>Tender Amount</th>
            <th>Awards Amount</th>
            <th>Tender Date</th>
            <th className="flag-type">Flag Type</th>
            <th>Number of<br/>risk type flags</th>
          </tr>
          </thead>
          <tbody>
          {data && data.map(this.row.bind(this))}
          </tbody>
        </table>
    )
  }
}

TopFlaggedContracts.endpoint = 'corruptionRiskOverviewTable?pageSize=10';

class OverviewPage extends React.Component{
  constructor(...args){
    super(...args);
    this.state = {
      corruptionType: null,
      topFlaggedContracts: null
    }
  }

  render(){
    const {corruptionType, topFlaggedContracts} = this.state;
    return (
      <div className="page-overview">
        <section className="chart-corruption-types">
          <h4>Corruption Types</h4>
          <CorruptionType
              filters={Map()}
              requestNewData={(_, corruptionType) => this.setState({corruptionType})}
              translations={{}}
              data={corruptionType}
          />
        </section>
        <section>
          <h4>The Projects with the Most Fraud, Collusion and Process Rigging Flags</h4>
          <TopFlaggedContracts
              filters={Map()}
              data={topFlaggedContracts}
              translations={{}}
              years={Map()}
              requestNewData={(_, topFlaggedContracts) => this.setState({topFlaggedContracts})}
          />
        </section>
      </div>
    )
  }
}

export default OverviewPage;
