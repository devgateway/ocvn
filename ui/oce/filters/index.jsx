import Component from "../pure-render-component";
import translatable from "../translatable";
import cn from "classnames";
import Organizations from "./tabs/organizations";
import TenderRules from "./tabs/tender-rules";
import Amounts from "./tabs/amounts";
import {Map} from "immutable";

class Filters extends translatable(Component){
  constructor(props){
    super(props);
    this.state = {
      currentTab: 0,
      state: Map()
    }
  }

  listTabs(){
    let {currentTab} = this.state;
    return this.constructor.TABS.map((Tab, index) => <li
            key={index}
            role="presentation"
            className={cn({active: index == currentTab})}
            onClick={_ => this.setState({currentTab: index})}
        >
          <a href="javascript:void(0);">
            {Tab.getName(this.__.bind(this))}
          </a>
        </li>
    );
  }

  content(){
    let {currentTab, state} = this.state;
    let {bidTypes, translations} = this.props;
    let Component = this.constructor.TABS[currentTab];
    return <Component
        state={state}
        onUpdate={(key, update) => this.setState({state: this.state.state.set(key, update)})}
        bidTypes={bidTypes}
        translations={translations}
    />
  }

  reset(){
    this.setState({state: Map()});
    this.props.onUpdate(Map())
  }

  render(){
    let {onClick, onUpdate, open} = this.props;
    return <div className={cn('filters', {open})}  onClick={onClick}>
      <img className="top-nav-icon" src="assets/icons/filter.svg"/> {this.__('Filter the data')} <i className="glyphicon glyphicon-menu-down"></i>
      <div className="box row" onClick={e => e.stopPropagation()}>
        <ul className="nav nav-pills nav-stacked col-xs-4">
          {this.listTabs()}
        </ul>
        <div className="col-xs-8 filter-tab-content">
          {this.content()}
        </div>
        <section className="buttons col-xs-offset-4 col-xs-8">
          <button className="btn btn-danger" onClick={e => onUpdate(this.state.state)}>
            {this.__('Apply')}
          </button>
          &nbsp;
          <button className="btn btn-default" onClick={e => this.reset()}>
            {this.__('Reset')}
          </button>
        </section>
      </div>
    </div>
  }
}

Filters.TABS = [Organizations, TenderRules, Amounts];

export default Filters;