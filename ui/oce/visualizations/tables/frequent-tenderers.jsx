import Table from "./index";
import orgNamesFetching from "../../orgnames-fetching";
import {pluckImm} from "../../tools";

class FrequentTenderers extends orgNamesFetching(Table){
  constructor(...args){
    super(...args);
    this.state = this.state || {};
    this.state.showAll = false;
  }

  row(entry, index){
    return <tr key={index}>
      <td>{this.getOrgName(entry.getIn(['id', 'tendererId1']))}</td>
      <td>{this.getOrgName(entry.getIn(['id', 'tendererId2']))}</td>
      <td>{entry.getIn(['value', 'pairCount'])}</td>
      <td>{entry.getIn(['value', 'winner1Count'])}</td>
      <td>{entry.getIn(['value', 'winner2Count'])}</td>
    </tr>
  }

  maybeSlice(flag, list){
    return flag ? list.slice(0, 10) : list;
  }

  getOrgsWithoutNamesIds(){
    if(!this.props.data) return [];
    return this.props.data.map(pluckImm('id')).flatten().filter(id => !this.state.orgNames[id]).toJS();
  }

  render(){
    if(!this.props.data) return null;
    const {showAll} = this.state;
    return <table className="table table-stripped trable-hover frequent-supplier-bidder-table">
      <thead>
      <tr>
        <th>{this.t('tables:frequentTenderers:supplier')} #1</th>
        <th>{this.t('tables:frequentTenderers:supplier')} #2</th>
        <th>{this.t('tables:frequentTenderers:nrITB')}</th>
        <th>{this.t('tables:frequentTenderers:supplier1wins')}</th>
        <th>{this.t('tables:frequentTenderers:supplier2wins')}</th>
      </tr>
      </thead>
      <tbody>
      {this.maybeSlice(!showAll, this.props.data).map(this.row.bind(this))}
      {!showAll && this.props.data.count() > 10 && <tr>
        <td colSpan="5">
          <button className="btn btn-info btn-danger btn-block" onClick={_ => this.setState({showAll: true})}>
            {this.t('tables:showAll')}
          </button>
        </td>
      </tr>}
      </tbody>
    </table>
  }
}

FrequentTenderers.getName = t => t('tables:frequentTenderers:title');
FrequentTenderers.endpoint = 'frequentTenderers';

export default FrequentTenderers;