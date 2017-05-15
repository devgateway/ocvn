import Component from "../../pure-render-component";
import style from "./style.less";
import cn from "classnames";
import MultipleSelect from "./multiple-select";
import TypeAhead from "./type-ahead";
import translatable from "../../translatable";

let icon = slug => <i className={`glyphicon glyphicon-${slug}`}></i>

class Filters extends translatable(Component){
  render(){
    let {actions, state, translations} = this.props;
    let globalState = state.get('globalState');
    let open = 'filters' == globalState.get('menuBox');
    let filters = globalState.get('filters');
    return <div
        onClick={e => actions.setMenuBox(open ? "" : "filters")}
        className={cn("filters", {open})}
    >
      {<img className="top-nav-icon" src="assets/icons/filter.svg"/>} {this.__('Filter the data')} {icon('menu-down')}
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
  }
}

Filters.propTypes = {
  translations: React.PropTypes.object.isRequired
};

export default Filters;
