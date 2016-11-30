import Table from "./index";
import {pluckImm, send, callFunc, shallowCopy} from "../../tools";
import URI from "urijs";

class FrequentTenderers extends Table{
  constructor(...args){
    super(...args);
    this.state = {
      showAll: false,
      orgNames: {}
    }
  }

  getOrgName(id){
    return this.state.orgNames[id] || id;
  }

  row(entry, index){
    return <tr key={index}>
      <td>{this.getOrgName(entry.getIn(['id', 'tendererId1']))}</td>
      <td>{this.getOrgName(entry.getIn(['id', 'tendererId2']))}</td>
      <td>{entry.get('value')}</td>
    </tr>
  }

  maybeSlice(flag, list){
    return flag ? list.slice(0, 10) : list;
  }

  maybeFetchOrgNames(){
    if(!this.props.data) return;
    const idsWithoutNames = this.props.data.map(pluckImm('id')).flatten().filter(id => !this.state.orgNames[id]).toJS();
    if(!idsWithoutNames.length) return;
    send(new URI('/api/ocds/organization/ids').addSearch('id', idsWithoutNames))
        .then(callFunc('json'))
        .then(orgs => {
          let orgNames = shallowCopy(this.state.orgNames);
          orgs.forEach(({id, name}) => orgNames[id] = name);
          this.setState({orgNames})
        })
  }

  componentDidMount(){
    super.componentDidMount();
    this.maybeFetchOrgNames();
  }

  componentDidUpdate(...args){
    super.componentDidUpdate(...args);
    this.maybeFetchOrgNames();
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
      </tr>
      </thead>
      <tbody>
      {this.maybeSlice(!showAll, this.props.data).map(this.row.bind(this))}
      {!showAll && this.props.data.count() > 10 && <tr>
        <td colSpan="3">
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