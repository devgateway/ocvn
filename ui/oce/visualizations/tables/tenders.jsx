import Table from "./index";

class Tenders extends Table{
  row(entry){
    let bidNo = entry.getIn(['planning', 'bidNo']);
    let tender = entry.get('tender');
    let tenderPeriod = tender.get('tenderPeriod');
    let value = tender.get('value');
    return <tr key={bidNo}>
      <td>{bidNo}</td>
      <td>{new Date(tenderPeriod.get('startDate')).toLocaleDateString(undefined, Table.DATE_FORMAT)}</td>
      <td>{new Date(tenderPeriod.get('endDate')).toLocaleDateString(undefined, Table.DATE_FORMAT)}</td>
      <td className="procuring-entity-title">{tender.getIn(['procuringEntity', 'name'])}</td>
      <td>{value.get('amount')} {value.get('currency')}</td>
    </tr>
  }

  render(){
    if(!this.props.data) return null;
    return (
      <table className="table table-striped table-hover tenders-table">
        <thead>
        <tr>
          <th>{this.t('tables:top10tenders:number')}</th>
          <th>{this.t('tables:top10tenders:startDate')}</th>
          <th>{this.t('tables:top10tenders:endDate')}</th>
          <th>{this.t('tables:top10tenders:procuringEntity')}</th>
          <th>{this.t('tables:top10tenders:estimatedValue')}</th>
        </tr>
        </thead>
        <tbody>
          {this.props.data.map(this.row)}
        </tbody>
      </table>
    )
  }
}

Tenders.getName = t => t('tables:top10tenders:title');
Tenders.endpoint = 'topTenLargestTenders';

export default Tenders;