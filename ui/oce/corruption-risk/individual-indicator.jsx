import CustomPopupChart from "./custom-popup-chart";
import {Map} from "immutable";
import {pluckImm} from "../tools";
import Table from "../visualizations/tables/index";
import translatable from "../translatable";

class IndividualIndicatorChart extends CustomPopupChart{
  getCustomEP(){
    const {indicator} = this.props;
    return `flags/${indicator}/stats`;
  }

  getData(){
    const data = super.getData();
    if(!data) return [];
    const {monthly} = this.props;
    let dates = monthly ?
                data.map(datum => {
                  const month = datum.get('month');
                  return this.t(`general:months:${month}`);
                }).toJS() :
                data.map(pluckImm('year')).toJS();

    let totalTrueValues = data.map(pluckImm('totalTrue', 0)).toJS();
    let totalPrecondMetValues = data.map(pluckImm('totalPrecondMet', 0)).toJS();

    if(dates.length == 1){
      dates.unshift("");
      dates.push(" ");
      totalTrueValues.unshift(0);
      totalTrueValues.push(0);
      totalPrecondMetValues.unshift(0);
      totalPrecondMetValues.push(0);
    }

    return [{
      x: dates,
      y: totalTrueValues,
      type: 'scatter',
      fill: 'tonexty',
      name: 'Flagged Procurements',
      hoverinfo: 'none',
      fillcolor: '#85cbfe',
      line: {
        color: '#63a0cd'
      },
    }, {
      x: dates,
      y: totalPrecondMetValues,
      type: 'scatter',
      fill: 'tonexty',
      name: 'Eligible Procurements',
      hoverinfo: 'none',
      fillcolor: '#336ba6',
      line: {
        color: '#224a74'
      }
    }];
  }

  getLayout(){
    return {
      legend: {
        orientation: 'h',
        xanchor: 'right',
        yanchor: 'top',
        x: 1,
        y: 1.3
      },
      hovermode: 'closest',
      xaxis: {
        type: 'category',
        showgrid: false
      }
    }
  }

  getPopup(){
    const {indicator, monthly} = this.props;
    const {popup} = this.state;
    const {year} = popup;
    const data = super.getData();
    if(!data) return null;
    let datum;
    if(monthly){
      datum = data.find(datum => {
        const month = datum.get('month');
        return year == this.t(`general:months:${month}`);
      })
    } else {
      datum = data.find(datum => datum.get('year') == year);
    }
    return (
      <div className="crd-popup" style={{top: popup.top, left: popup.left}}>
        <div className="row">
          <div className="col-sm-12 info text-center">
            {year}
          </div>
          <div className="col-sm-12">
            <hr/>
          </div>
          <div className="col-sm-8 text-right title">Projects Flagged</div>
          <div className="col-sm-4 text-left info">{datum.get('totalTrue')}</div>
          <div className="col-sm-8 text-right title">Eligible Projects</div>
          <div className="col-sm-4 text-left info">{datum.get('totalPrecondMet')}</div>
          <div className="col-sm-8 text-right title">% Eligible Projects Flagged</div>
          <div className="col-sm-4 text-left info">{datum.get('percentTruePrecondMet').toFixed(2)} %</div>
          <div className="col-sm-8 text-right title">% Projects Eligible</div>
          <div className="col-sm-4 text-left info">{datum.get('percentPrecondMet').toFixed(2)} %</div>
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
    const flags = entry.get('flags');
    const flaggedStats = flags.get('flaggedStats');
    const type = flaggedStats.get('type');
    const flagIds = 
      flags
        .filter(
          flag => flag.has && flag.has('types') && flag.get('types').includes(type) && flag.get('value')
        )
        .keySeq();

    const procuringEntityName = entry.getIn(['procuringEntity', 'name']);
    return (
      <tr key={index}>
        <td>{entry.get('tag', []).join(', ')}</td>
        <td>{entry.get('ocid')}</td>
        <td>{entry.get('title')}</td>
        <td>
          <div title={procuringEntityName} className="oce-3-line-text">
            {procuringEntityName}
          </div>
        </td>
        <td>{tenderValue && tenderValue.get('amount')} {tenderValue && tenderValue.get('currency')}</td>
        <td>{awardValue.get('amount')} {awardValue.get('currency')}</td>
        <td>{startDate.toLocaleDateString()}&mdash;{endDate.toLocaleDateString()}</td>
        <td>{type}</td>
        <td className="hoverable popup-left">
          {flaggedStats.get('count')}
          <div className="crd-popup text-center">
            <div className="row">
              <div className="col-sm-12 info">
                Associated {type[0] + type.substr(1).toLowerCase()} Flags
              </div>
              <div className="col-sm-12">
                <hr/>
              </div>
              <div className="col-sm-12 info">
                {flagIds.map(flagId => <p key={flagId}>{this.t(`crd:indicators:${flagId}:name`)}</p>)}
              </div>
            </div>
            <div className="arrow"/>
          </div>
        </td>
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
            <th>No. of Flags</th>
          </tr>
        </thead>
        <tbody>
          {data && data.map(this.row.bind(this))}
        </tbody>
      </table>
    )
  }
}

class IndividualIndicatorPage extends translatable(React.Component){
  constructor(...args){
    super(...args);
    this.state = {
    }
  }

  render(){
    const {chart, table} = this.state;
    const {indicator, translations, filters, years, monthly, months, width} = this.props;
    return (
      <div className="page-corruption-type">
        <h2 className="page-header">{this.t(`crd:indicators:${indicator}:name`)}</h2>
        <p className="definition">
          <strong>{this.t("crd:indicators:general:indicator")}</strong>
          &nbsp;
          {this.t(`crd:indicators:${indicator}:indicator`)}
        </p>
        <p className="definition">
          <strong>{this.t("crd:indicators:general:eligibility")}</strong>
          &nbsp;
          {this.t(`crd:indicators:${indicator}:eligibility`)}
        </p>
        <p className="definition">
          <strong>{this.t("crd:indicators:general:thresholds")}</strong>
          &nbsp;
          {this.t(`crd:indicators:${indicator}:thresholds`)}
        </p>
        <p className="definition">
          <strong>{this.t("crd:indicators:general:description")}</strong>
          &nbsp;
          {this.t(`crd:indicators:${indicator}:description`)}
        </p>
        <section>
          <h3 className="page-header">
            Eligible Procurements and Flagged Procurements for {this.t(`crd:indicators:${indicator}:name`)}
          </h3>
          <IndividualIndicatorChart
              indicator={indicator}
              translations={translations}
              filters={filters}
              years={years}
              monthly={monthly}
              months={months}
              requestNewData={(_, data) => this.setState({chart: data})}
              data={chart}
              width={width}
              title=""
          />
        </section>
        <section>
          <h3 className="page-header">
            List of Procurements Flagged for {this.t(`crd:indicators:${indicator}:name`)}
          </h3>
          <ProjectTable
              indicator={indicator}
              requestNewData={(_, data) => this.setState({table: data})}
              data={table}
              translations={translations}
              filters={filters}
              years={years}
              monthly={monthly}
              months={months}
          />
        </section>
      </div>
    )
  }
}

export default IndividualIndicatorPage;
