import React from "react";
import Component from "../pure-render-component";
import {tabs} from "../../flux/stores/global-state";
import Overview from "../overview";
import Planning from "../planning";
import Tender from "../tender";
import NavigationLink from "./navigation-link";
import {years} from "../../tools";
import cn from "classnames";
import {toImmutable} from "nuclear-js";
import Filters from "./filters";
require('./style.less');

export default class App extends React.Component{
  componentDidMount(){
    this.props.actions.changeContentWidth(document.querySelector('.years-bar').offsetWidth);
  }

  render(){
    var {state, actions} = this.props;
    var navigationLink = (text, marker, tab) =>
        <NavigationLink text={text} actions={actions} tab={tab} marker={marker} active={state.getIn(['globalState', 'tab']) == tab}/>
    var globalState = state.get('globalState');
    return (
      <div className="container-fluid">
        <aside className="col-xs-4 col-md-3 col-lg-2">
          <div className="row">
            <section className="col-sm-12 branding">
              <h1>
                E-procurement
                <small>Toolkit</small>
              </h1>
            </section>
            <div role="navigation">
              {navigationLink("Overview", 'search', tabs.OVERVIEW)}
              {navigationLink("Planning", 'map-marker', tabs.PLANNING)}
              {navigationLink("Tender", 'time', tabs.TENDER_AWARD)}
            </div>
            <section className="col-sm-12 description">
              <p><strong>Toolkit description</strong></p>
              <p>
                <small>
                  The Procurement M&E Prototype is an interactive platform for analyzing, monitoring, and evaluating
                  information on procurement in Vietnam. All data in the dashboard are collected from the Vietnam
                  Government eProcurement system (eGP).
                </small>
              </p>
            </section>
            <Filters {...this.props}/>
          </div>
        </aside>
        <div className="col-xs-offset-4 col-md-offset-3 col-lg-offset-2 col-xs-8 col-md-9 col-lg-10 years-bar" role="navigation">
          {globalState.get('selectedYears').map((selected, year) => (
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
              switch(tab){
                case tabs.OVERVIEW: return <Overview {...props}/>;
                case tabs.PLANNING: return (
                    <Planning
                        width={globalState.get('contentWidth')}
                        years={globalState.get('selectedYears')}
                        locations={globalState.getIn(['data', 'locations'])}
                    />
                );
                default: return <Tender {...props}/>
              }
            }(globalState.get('tab'), this.props)}
            <div className="col-sm-12 thick-red-line"></div>
          </div>
        </div>
      </div>
    )
  }
}