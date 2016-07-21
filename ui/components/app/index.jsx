import React from "react";
import Component from "../pure-render-component";
import {tabs} from "../../flux/stores/global-state";
import Overview from "../overview";
import Planning from "../planning";
import Competitiveness from "../competitiveness";
import Efficiency from "../efficiency";
import EProcurement from "../e-procurement";
import NavigationLink from "./navigation-link";
import cn from "classnames";
import {toImmutable} from "nuclear-js";
import Filters from "./filters";
import ComparisonCriteria from "./filters/compare";
import translatable from "../translatable";
require('./style.less');

export default class App extends translatable(Component){
  componentDidMount(){
    // this.props.actions.changeContentWidth(document.querySelector('.years-bar').offsetWidth);
  }

  render(){
    let {state, actions, translations} = this.props;
    let width = state.getIn(['globalState', 'contentWidth']);
    let navigationLink = (text, marker, tab) =>
        <NavigationLink text={text} actions={actions} tab={tab} marker={marker} active={state.getIn(['globalState', 'tab']) == tab}/>
    let globalState = state.get('globalState');
    return <div className="container-fluid">
      <header className="branding row">
        <div className="col-sm-offset-1 col-sm-4">
          <h1>
            {this.__('e-Procurement')}
            <small>{this.__('Toolkit')}</small>
          </h1>
        </div>
        <div className="col-sm-6 menu">
          <Filters {...this.props}/>
          <ComparisonCriteria {...this.props}/>
          <div className="filters">
            <img className="top-nav-icon" src="assets/icons/export.svg"/> Export <i className="glyphicon glyphicon-menu-down"></i>
          </div>
        </div>
        <div className="col-sm-2 language-switcher">
          <img className="flag" src="assets/flags/us.png" alt="US flag" onClick={e => actions.setLocale("en")}/>
          <img className="flag" src="assets/flags/vn.png" alt="Vietnam flag" onClick={e => actions.setLocale("vn")}/>
        </div>
      </header>
      <aside className="col-xs-4 col-md-3 col-lg-2">
        <div className="row">
          <div role="navigation">
          <a className="col-sm-12">
          <span className="circle">
            <img className="nav-icon" src="assets/icons/overview.svg"/>
          </span>
          Overview
          </a>
          <a className="col-sm-12">
          <span className="circle">
            <img className="nav-icon" src="assets/icons/planning.svg"/>
          </span>
          Planning
          </a>
          <a className="col-sm-12">
          <span className="circle">
            <img className="nav-icon" src="assets/icons/competitive.svg"/>
          </span>
          Competitiveness
          </a>
          <a className="col-sm-12">
          <span className="circle">
            <img className="nav-icon" src="assets/icons/efficiency.svg"/>
          </span>
          Efficiency
          </a>
          <a className="col-sm-12">
          <span className="circle">
            <img className="nav-icon" src="assets/icons/eprocurement.svg"/>
          </span>
          E-procurement
          </a>

          </div>
          <section className="col-sm-12 description">
            <h3><strong>{this.__("Toolkit description")}</strong></h3>
            <p>
              <small>
                {this.__("The Procurement M&E Prototype is an interactive platform for analyzing, monitoring, and evaluating information on procurement in Vietnam. All data in the dashboard are collected from the Vietnam Government eProcurement system (eGP).")}
              </small>
            </p>
          </section>
        </div>
      </aside>
      <div className="col-xs-offset-4 col-md-offset-3 col-lg-offset-2 col-xs-8 col-md-9 col-lg-10">
        <div className="row">
             {function(tab, props){
               let {state, actions} = props;
               switch(tab){
                 case tabs.OVERVIEW:
                   return <Overview
                       translations={translations}
                       actions={actions}
                       state={state.get('overview')}
                       width={width}
                   />;
                 case tabs.PLANNING: return (
                     <Planning
                         translations={translations}
                         width={globalState.get('contentWidth')}
                         years={globalState.getIn(['filters', 'years'])}
                         locations={globalState.getIn(['data', 'locations'])}
                     />
                 );
                 case tabs.COMPETITIVENESS: return (
                     <Competitiveness
                         translations={translations}
                         actions={actions}
                         state={state.get('competitiveness')}
                         width={width}
                     />
                 );
                 case tabs.EFFICIENCY: return (
                     <Efficiency
                         translations={translations}
                         actions={actions}
                         state={state.get('efficiency')}
                         width={width}
                     />
                 );
                 case tabs.E_PROCUREMENT: return (
                     <EProcurement
                         translations={translations}
                         actions={actions}
                         state={state.get('eProcurement')}
                         width={width}
                     />
                 );
               }
             }(globalState.get('tab'), this.props)}
               <div className="col-sm-12 thick-red-line"></div>
        </div>
      </div>
      <div className="col-xs-offset-4 col-md-offset-3 col-lg-offset-2 col-xs-8 col-md-9 col-lg-10 years-bar" role="navigation">
           {globalState.hasIn(['filters', 'years']) && globalState.getIn(['filters', 'years']).map((selected, year) => (
               <a
                   key={year}
                   href="javascript:void(0);"
                   className={cn({active: true === selected})}
                   onClick={e => actions.toggleYear(year, !selected)}
               >
                 <i className="glyphicon glyphicon-ok-circle"></i> {year}
               </a>
           )).toArray()}
      </div>
      <footer className="col-sm-12 main-footer">&nbsp;</footer>
    </div>;

    return (
      <div className="container-fluid">



      </div>
    )
  }
}
