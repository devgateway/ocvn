import Table from "./index";

class FrequentTenderers extends Table{
  constructor(...args){
    super(...args);
    this.state = {
      showAll: false
    }
  }

  row(entry, index){
    return <tr key={index}>
      <td>{entry.getIn(['id', 'tendererId1'])}</td>
      <td>{entry.getIn(['id', 'tendererId2'])}</td>
      <td>{entry.get('value')}</td>
    </tr>
  }

  maybeSlice(flag, list){
    return flag ? list.slice(0, 10) : list;
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
      {this.maybeSlice(!showAll, this.props.data).map(this.row)}
      {!showAll && <tr>
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