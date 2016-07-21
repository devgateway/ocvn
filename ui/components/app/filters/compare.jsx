import Component from "../../pure-render-component";
import cn from "classnames";
import translatable from "../../translatable";

let icon = slug => <i className={`glyphicon glyphicon-${slug}`}></i>

export default class ComparisionCriteria extends translatable(Component){
  render(){
    let {actions, state} = this.props;
    let globalState = state.get('globalState');
    let open = 'compare' == globalState.get('menuBox');
    return <div
        onClick={e => actions.setMenuBox(open ? "" : "compare")}
        className={cn("filters compare", {open})}
    >
      {<img className="top-nav-icon" src="assets/icons/compare.svg"/>} {this.__('Compare')} {icon('menu-down')}
      <div className="box" onClick={e => e.stopPropagation()}>
        <div className="col-sm-6">
          <label>{this.__('Comparison criteria')}</label>
        </div>
        <div className="col-sm-6">
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
        </div>
      </div>
    </div>
    return (
        <section
            onClick={e => actions.setMenuBox(open ? "" : "compare")}
            className={cn("filters", {open})}
        >
          <div className="row">
            <div className="col-sm-10 text">

            </div>
            <div className="col-sm-1 end arrow">
              <i className="glyphicon glyphicon-menu-right"></i>
            </div>

          </div>
        </section>
    )
  }
}
