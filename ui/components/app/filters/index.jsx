import Component from "../../pure-render-component";
import style from "./style.less";
import cn from "classnames";
import MultipleSelect from "./multiple-select";
import TypeAhead from "./type-ahead";
import translatable from "../../translatable";

export default class Filters extends translatable(Component){
  render(){
    var {actions, state, translations} = this.props;
    var globalState = state.get('globalState');
    var open = 'filters' == globalState.get('filtersBox');
    var filters = globalState.get('filters');
    return (
        <section
            onClick={e => actions.setFiltersBox(open ? "" : "filters")}
            className={cn("col-sm-12 filters", {open: open})}
        >
          <div className="row">
            <div className="col-sm-10 text">
              {this.__('Filter the data')}
            </div>
            <div className="col-sm-1 end arrow">
              <i className="glyphicon glyphicon-menu-right"></i>
            </div>
            <div className="box" onClick={e => e.stopPropagation()}>
              <MultipleSelect
                  title={this.__("Bid type")}
                  slug="bidTypes"
                  state={filters.get('bidTypes')}
                  actions={actions}
              />
              <MultipleSelect
                  title={this.__("Bid selection method")}
                  slug="bidSelectionMethods"
                  state={filters.get('bidSelectionMethods')}
                  actions={actions}
              />
              <TypeAhead
                  slug="procuringEntities"
                  query={globalState.get('procuringEntityQuery')}
                  state={filters.get('procuringEntities')}
                  actions={actions}
                  translations={translations}
              />
            </div>
          </div>
        </section>
    )
  }
}