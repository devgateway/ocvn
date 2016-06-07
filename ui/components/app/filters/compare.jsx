import Component from "../../pure-render-component";
import cn from "classnames";
import translatable from "../../translatable";

export default class ComparisionCriteria extends Component{
  render(){
    var {actions, state} = this.props;
    var globalState = state.get('globalState');
    var open = 'compare' == globalState.get('filtersBox');
    return (
        <section
            onClick={e => actions.setFiltersBox(open ? "" : "compare")}
            className={cn("col-sm-12 filters", {open: open})}
        >
          <div className="row">
            <div className="col-sm-10 text">
              {this.__('Compare')}
            </div>
            <div className="col-sm-1 end arrow">
              <i className="glyphicon glyphicon-menu-right"></i>
            </div>
            <div className="box" onClick={e => e.stopPropagation()}>
              <label>
                {this.__('Comparison criteria')}
                &nbsp;
                <select
                    className="form-control"
                    value={globalState.get('compareBy')}
                    onChange={e => actions.updateComparisonCriteria(e.target.value)}
                >
                  <option value="">{this.__('None')}</option>
                  <option value="bidTypeId">{this.__('Bid Type')}</option>
                  <option value="bidSelectionMethod">{this.__('Bid Selection Method')}</option>
                  <option value="procuringEntityId">{this.__('Procuring Entity')}</option>
                </select>
              </label>
            </div>
          </div>
        </section>
    )
  }
}