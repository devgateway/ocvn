import Component from "../pure-render-component";
import translatable from "../translatable";
import cn from "classnames";
import Organizations from "./tabs/organizations";
import TenderRules from "./tabs/tender-rules";
import Amounts from "./tabs/amounts";
import {Map} from "immutable";
import {send} from "../tools";
import URI from "urijs";

class Filters extends translatable(Component){
  constructor(props){
    super(props);
    this.state = {
      currentTab: 0,
      state: Map(),
      savingDashboard: false,
      dashboardName: ""
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
            {Tab.getName(this.t.bind(this))}
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

  onSaveClicked(){
    const {savingDashboard, dashboardName, state} = this.state;
    if(!savingDashboard){
      this.setState({savingDashboard: true});
    } else {
      const encoded = ""+JSON.stringify(state);
      send(new URI('/rest/userDashboards/saveDashboardForCurrentUser')
          .addSearch('name', dashboardName)
          .addSearch('formUrlEncodedBody', encoded)
      );
      this.setState({
        savingDashboard: false,
        dashboardName: ""
      })
    }
  }

  render(){
    let {onClick, onUpdate, open, user} = this.props;
    const {savingDashboard, dashboardName} = this.state;
    return <div className={cn('filters', {open})}  onClick={onClick}>
      <img className="top-nav-icon" src="assets/icons/filter.svg"/> {this.t('filters:title')} <i className="glyphicon glyphicon-menu-down"></i>
      <div className="box row" onClick={e => e.stopPropagation()}>
        <ul className="nav nav-pills nav-stacked col-xs-4">
          {this.listTabs()}
        </ul>
        <div className="col-xs-8 filter-tab-content">
          {this.content()}
        </div>
        <section className="buttons col-xs-offset-4 col-xs-8">
          <button className="btn btn-danger" onClick={e => onUpdate(this.state.state)}>
            {this.t('filters:apply')}
          </button>
          &nbsp;
          <button className="btn btn-default" onClick={e => this.reset()}>
            {this.t('filters:reset')}
          </button>
          &nbsp;
          {savingDashboard && user.isAdmin &&
            <input
                type="text"
                className="input-sm form-control dashboard-name"
                value={dashboardName}
                onChange={e => this.setState({dashboardName: e.target.value})}
            />
          }
          &nbsp;
          {user.loggedIn &&
            <button className="btn btn-default" onClick={e => this.onSaveClicked()}>
              {this.t('filters:dashboard:save')}
            </button>
          }
        </section>
      </div>
    </div>
  }
}

Filters.TABS = [Organizations, TenderRules, Amounts];

export default Filters;