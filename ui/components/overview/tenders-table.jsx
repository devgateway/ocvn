import Component from "../pure-render-component";
import translatable from '../translatable';
const DATE_FORMAT = {
  year: 'numeric',
  month: 'short',
  day: 'numeric'
};

export default class TendersTable extends translatable(Component){
  render(){
    if(!this.props.data) return null;
    return (
        <section>
          <h4 className="page-header">Top 10 largest tenders</h4>
          <table className="table table-striped table-hover tenders-table">
            <thead>
              <tr>
                <th>{this.__("Number")}</th>
                <th>{this.__("Start date")}</th>
                <th>{this.__("End date")}</th>
                <th>{this.__("Procuring entity")}</th>
                <th>{this.__("Estimated price")}</th>
              </tr>
            </thead>
            <tbody>
              {this.props.data.map(entry => (
                  <tr key={entry.planning.bidNo}>
                    <td>{entry.planning.bidNo}</td>
                    <td>{new Date(entry.tender.tenderPeriod.startDate).toLocaleDateString(undefined, DATE_FORMAT)}</td>
                    <td>{new Date(entry.tender.tenderPeriod.endDate).toLocaleDateString(undefined, DATE_FORMAT)}</td>
                    <td className="procuring-entity-title">{entry.tender.procuringEntity.name}</td>
                    <td>{entry.tender.value.amount} {entry.tender.value.currency}</td>
                  </tr>
              ))}
            </tbody>
          </table>
        </section>
    )
  }
}