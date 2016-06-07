import React from "react";
import Component from "../pure-render-component";
import {tabs} from "../../flux/stores/global-state";
import Overview from "../overview";
import Planning from "../planning";
import Tender from "../tender";
import NavigationLink from "./navigation-link";
import cn from "classnames";
import {toImmutable} from "nuclear-js";
import Filters from "./filters";
import ComparisonCriteria from "./filters/compare";
import translatable from ".././translatable";
require('./style.less');

export default class App extends translatable(Component){
  componentDidMount(){
    this.props.actions.changeContentWidth(document.querySelector('.years-bar').offsetWidth);
  }

  render(){
    var {state, actions, translations} = this.props;
    var width = state.getIn(['globalState', 'contentWidth']);
    var navigationLink = (text, marker, tab) =>
        <NavigationLink text={text} actions={actions} tab={tab} marker={marker} active={state.getIn(['globalState', 'tab']) == tab}/>
    var globalState = state.get('globalState');
    return (
      <div className="container-fluid">
        <aside className="col-xs-4 col-md-3 col-lg-2">
          <div className="row">
            <section className="col-sm-12 branding">
              <h1>
                {this.__('E-procurement')}
                <small>{this.__('Toolkit')}</small>
              </h1>
            </section>
            <div role="navigation">
              {navigationLink(this.__("Overview"), 'search', tabs.OVERVIEW)}
              {navigationLink(this.__("Planning"), 'map-marker', tabs.PLANNING)}
              {navigationLink(this.__("Tender"), 'time', tabs.TENDER_AWARD)}
            </div>
            <section className="col-sm-12 description">
              <p><strong>{this.__("Toolkit description")}</strong></p>
              <p>
                <small>
                  {this.__(`
                  The Procurement M&E Prototype is an interactive platform for analyzing, monitoring, and evaluating
                  information on procurement in Vietnam. All data in the dashboard are collected from the Vietnam
                  Government eProcurement system (eGP).
                  `)}
                </small>
              </p>
            </section>
            <Filters {...this.props}/>
            <ComparisonCriteria {...this.props}/>
          </div>
        </aside>
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
        <div className="col-xs-offset-4 col-md-offset-3 col-lg-offset-2 col-xs-8 col-md-9 col-lg-10">
          <div className="row">
            {function(tab, props){
              var {state, actions} = props;
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
                default: return (
                    <Tender
                        translations={translations}
                        actions={actions}
                        state={state.get('tender')}
                        width={width}
                    />
                )
              }
            }(globalState.get('tab'), this.props)}
            <div className="col-sm-12 thick-red-line"></div>
          </div>
        </div>
      </div>
    )
  }
}