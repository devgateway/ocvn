import Component from "../pure-render-component";
import {pluck} from "../../tools";
import translatable from "../translatable";
const DATE_FORMAT = {
  year: 'numeric',
  month: 'short',
  day: 'numeric'
};

export default class AwardsTable extends translatable(Component){
  render(){
    if(!this.props.data) return null;
    return (
        <section>
          <h4 className="page-header">Top 10 largest awards</h4>
          <table className="table table-striped table-hover awards-table">
            <thead>
              <tr>
                <th>{this.__('Number')}</th>
                <th>{this.__('Price')}</th>
                <th>{this.__('Supplier')}</th>
                <th>{this.__('Date')}</th>
              </tr>
            </thead>
            <tbody>
              {this.props.data.map(entry => (
                  <tr key={entry.planning.bidNo}>
                    <td>{entry.planning.bidNo}</td>
                    <td>{entry.awards.value.amount} {entry.awards.value.currency}</td>
                    <td className="supplier-name">
                      {entry.awards.suppliers.map(pluck('name')).join(', ')}
                    </td>
                    <td>{new Date(entry.awards.date).toLocaleDateString(undefined, DATE_FORMAT)}</td>
                  </tr>
              ))}
            </tbody>
          </table>
        </section>
    )
  }
}