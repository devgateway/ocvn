import style from "./style.less";
import cn from "classnames";
import URI from "urijs";
import {fetchJson, debounce, cacheFn, range, pluck, callFunc} from "../tools";
import OverviewPage from "./overview-page";
import CorruptionTypePage from "./corruption-type";
import {Map, Set} from "immutable";
import IndividualIndicatorPage from "./individual-indicator";
import Filters from "./filters";
import TotalFlags from "./total-flags";
import LandingPopup from "./landing-popup";
import {LOGIN_URL} from "./constants";

const ROLE_ADMIN = 'ROLE_ADMIN';

const CORRUPTION_TYPES = {
  FRAUD: "Fraud",
  RIGGING: "Process rigging",
  COLLUSION: "Collusion"
};

class CorruptionRiskDashboard extends React.Component{
  constructor(...args){
    super(...args);
    this.state={
      dashboardSwitcherOpen: false,
      user: {
        loggedIn: false,
        isAdmin: false
      },
      page: 'overview',
      indicatorTypesMapping: {},
      currentFiltersState: Map(),
      appliedFilters: Map(),
      filterBoxIndex: null,
      allMonths: range(1, 12),
      allYears: [],
      width: 0,
      data: Map(),
      showLandingPopup: !localStorage.alreadyVisited
    };
    localStorage.alreadyVisited = true;

    this.destructFilters = cacheFn(filters => {
      return {
        filters: filters.delete('years').delete('months'),
        years: filters.get('years', Set()),
        months: filters.get('months', Set())
      }
    });
  }

  fetchUserInfo(){
    const noCacheUrl = new URI('/isAuthenticated').addSearch('time', Date.now());
    fetchJson(noCacheUrl).then(({authenticated, disabledApiSecurity}) => {
      this.setState({
        user: {
          loggedIn: authenticated,
        },
        showLandingPopup: !authenticated || disabledApiSecurity,
        disabledApiSecurity
      })
    });
  }

  fetchIndicatorTypesMapping(){
    fetchJson('/api/indicatorTypesMapping').then(data => this.setState({indicatorTypesMapping: data}));
  }

  fetchYears(){
    fetchJson('/api/tendersAwardsYears').then(data => {
      const years = data.map(pluck('_id'));
      const {allMonths, currentFiltersState, appliedFilters} = this.state;
      this.setState({
        currentFiltersState: currentFiltersState
          .set('years', Set(years))
          .set('months', Set(allMonths)),
        appliedFilters: appliedFilters
          .set('years', Set(years))
          .set('months', Set(allMonths)),
        allYears: years
      });
    });
  }

  componentDidMount(){
    !this.state.showLandingPopup && this.fetchUserInfo();
    this.fetchIndicatorTypesMapping();
    this.fetchYears();

    this.setState({
      width: document.querySelector('.content').offsetWidth - 30
    });

    window.addEventListener("resize", debounce(() => {
      this.setState({
        width: document.querySelector('.content').offsetWidth - 30
      });
    }));
  }

  toggleDashboardSwitcher(e){
    e.stopPropagation();
    const {dashboardSwitcherOpen} = this.state;
    this.setState({dashboardSwitcherOpen: !dashboardSwitcherOpen});
  }

  loginBox(){
    if(this.state.user.loggedIn){
      return (
        <a href="/preLogout?referrer=/ui/index.html?corruption-risk-dashboard">
          <button className="btn btn-success">Logout</button>
        </a>
      )
    }
    return <a href={LOGIN_URL}>
          <button className="btn btn-success">Login</button>
    </a>
  }

  getPage(){
    const {translations, styling} = this.props;
    const {page, appliedFilters, indicatorTypesMapping, width} = this.state;

    const {filters, years, months} = this.destructFilters(appliedFilters);
    const monthly = years.count() == 1;

    if(page == 'overview'){
      return <OverviewPage
                 filters={filters}
                 translations={translations}
                 years={years}
                 monthly={monthly}
                 months={months}
                 indicatorTypesMapping={indicatorTypesMapping}
                 styling={styling}
                 width={width}
             />;
    } else if(page == 'corruption-type') {
      const {corruptionType} = this.state;

      const indicators =
        Object.keys(indicatorTypesMapping).filter(key => indicatorTypesMapping[key].types.indexOf(corruptionType) > -1);

      return <CorruptionTypePage
                 indicators={indicators}
                 onGotoIndicator={individualIndicator => this.setState({page: 'individual-indicator', individualIndicator})}
                 filters={filters}
                 translations={translations}
                 corruptionType={corruptionType}
                 years={years}
                 monthly={monthly}
                 months={months}
                 width={width}
             />;
    } else if(page == 'individual-indicator'){
      const {individualIndicator} = this.state;
      return (
        <IndividualIndicatorPage
            indicator={individualIndicator}
            filters={filters}
            translations={translations}
            years={years}
            monthly={monthly}
            months={months}
            width={width}
        />
      )
    }
  }

  render(){
    const {dashboardSwitcherOpen, corruptionType, page, filterBoxIndex, currentFiltersState, appliedFilters
      , data, indicatorTypesMapping, allYears, allMonths, showLandingPopup, disabledApiSecurity, user} = this.state;
    const {onSwitch, translations} = this.props;

    const {filters, years, months} = this.destructFilters(appliedFilters);
    const monthly = years.count() == 1;

    return (
      <div className="container-fluid dashboard-corruption-risk"
           onMouseDown={e => this.setState({dashboardSwitcherOpen: false, filterBoxIndex: null})}
      >
        {showLandingPopup &&
          <LandingPopup redirectToLogin={!disabledApiSecurity} requestClosing={e => this.setState({showLandingPopup: false})}/>}
        <header className="branding row">
          <div className="col-sm-1 logo-wrapper">
            <img src="assets/logo.png"/>
          </div>
          <div className="col-sm-8">
            <div className={cn('dash-switcher-wrapper', {open: dashboardSwitcherOpen})}>
              <h1 className="corruption-dash-title" onClick={this.toggleDashboardSwitcher.bind(this)}>
                Corruption Risk Dashboard
                <i className="glyphicon glyphicon-menu-down"></i>
              </h1>
              {dashboardSwitcherOpen &&
                <div className="dashboard-switcher">
                  <a href="javascript:void(0);" onClick={e => onSwitch('default')} onMouseDown={callFunc('stopPropagation')}>
                    Default dashboard
                  </a>
                </div>
              }
            </div>
          </div>
          <div className="col-sm-2 login-wrapper">
            {!disabledApiSecurity && this.loginBox()}
          </div>
          <div className="col-sm-1">
          </div>
        </header>
        <Filters
          onUpdate={currentFiltersState => this.setState({currentFiltersState})}
          onApply={filtersToApply => this.setState({filterBoxIndex: null, appliedFilters: filtersToApply, currentFiltersState: filtersToApply})}
          translations={translations}
          currentBoxIndex={filterBoxIndex}
          requestNewBox={index => this.setState({filterBoxIndex: index})}
          state={currentFiltersState}
          appliedFilters={appliedFilters}
          allYears={allYears}
          allMonths={allMonths}
        />
        <aside className="col-xs-4 col-md-4 col-lg-3" id="crd-sidebar">
          <div className="crd-description-text">
            <h4 className="crd-overview-link" onClick={e => this.setState({page: 'overview'})}>
              Corruption Risk Overview
              <i className="glyphicon glyphicon-info-sign"></i>
            </h4>
            <p className="small">
              The Corruption Risk Dashboard employs a red flagging approach to help users understand the potential presence of fraud, collusion or rigging in public contracting. While flags may indicate the presence of corruption, they may also be attributable to data quality issues, infringements of law or international good practice, or other issues.
            </p>
          </div>
          <section role="navigation" className="row">
            {Object.keys(CORRUPTION_TYPES).map(slug => {
               const name = CORRUPTION_TYPES[slug];
               const count = Object.keys(indicatorTypesMapping)
                 .filter(key => indicatorTypesMapping[key].types.indexOf(slug) > -1)
                 .length;

               return (
                 <a
                   href="javascript:void(0);"
                   onClick={e => this.setState({page: 'corruption-type', corruptionType: slug})}
                   className={cn({active: 'corruption-type' == page && slug == corruptionType})}
                   key={slug}
                 >
                   <img src={`assets/icons/${slug}.png`}/>
                   {name} <span className="count">({count})</span>
                 </a>
               )
            })}
          </section>
          <TotalFlags
            filters={filters}
            requestNewData={(path, newData) =>
              this.setState({data: this.state.data.setIn(['totalFlags'].concat(path), newData)})}
            translations={translations}
            data={data.get('totalFlags', Map())}
            years={years}
            months={months}
            monthly={monthly}
          />
        </aside>
        <div className="col-xs-offset-4 col-md-offset-4 col-lg-offset-3 col-xs-8 col-md-8 col-lg-9 content">
          {this.getPage()}
        </div>
      </div>
    )
  }
}

export default CorruptionRiskDashboard;
