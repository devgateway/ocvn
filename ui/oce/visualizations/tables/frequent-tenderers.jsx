import Table from "./index";

class FrequentTenderers extends Table{
  row(entry, index){
    return <tr key={index}>
      <td>{entry.getIn(['id', 'tendererId1'])}</td>
      <td>{entry.getIn(['id', 'tendererId2'])}</td>
      <td>{entry.get('value')}</td>
    </tr>
  }

  render(){
    if(!this.props.data) return null;
    return <table className="table table-stripped trable-hover frequent-supplier-bidder-table">
      <thead>
      <tr>
        <th>{this.t('tables:frequentTenderers:supplier')} #1</th>
        <th>{this.t('tables:frequentTenderers:supplier')} #2</th>
        <th>{this.t('tables:frequentTenderers:nrITB')}</th>
      </tr>
      </thead>
      <tbody>
      {this.props.data.map(this.row)}
      </tbody>
    </table>
  }
}

FrequentTenderers.getName = t => t('tables:frequentTenderers:title');
FrequentTenderers.endpoint = 'frequentTenderers';

export default FrequentTenderers;