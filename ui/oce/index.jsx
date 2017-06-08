import cn from "classnames";
import {fromJS, Map, Set} from "immutable";
import {fetchJson, debounce, download, pluck, range} from "./tools";
import URI from "urijs";
import Filters from "./filters";
import OCEStyle from "./style.less";

const MENU_BOX_COMPARISON = "menu-box";
const MENU_BOX_FILTERS = 'filters';
const ROLE_ADMIN = 'ROLE_ADMIN';

class OCApp extends React.Component{
  constructor(props){
    super(props);
    this.tabs = [];
    this.state = {
      dashboardSwitcherOpen: false,
      exporting: false,
      locale: localStorage.oceLocale || "en_US",
      width: 0,
      currentTab: 0,
      menuBox: "",
      compareBy: "",
      comparisonCriteriaValues: [],
      selectedYears: Set(),
      selectedMonths: Set(range(1, 12)),
      filters: fromJS({}),
      data: fromJS({}),
      comparisonData: fromJS({}),
      bidTypes: fromJS({}),
      years: fromJS([]),
      user: {
        loggedIn: false,
        isAdmin: false
      }
    }
  }

  registerTab(tab){
    this.tabs.push(tab);
  }

  t(text){
    const {locale} = this.state;
    return this.constructor.TRANSLATIONS[locale][text];
  }

  updateComparisonCriteria(criteria){
    this.setState({
      menuBox: "",
      compareBy: criteria,
      comparisonCriteriaValues: [],
      comparisonData: fromJS({})
    });
    if(!criteria) return;
    fetchJson(new URI('/api/costEffectivenessTenderAmount').addSearch({
      groupByCategory: criteria,
      pageSize: 3
    })).then(data => this.setState({
      comparisonCriteriaValues: data.map(datum => datum[0] || datum._id)
    }));
  }

  fetchBidTypes(){
    fetchJson('/api/ocds/bidType/all').then(data =>
        this.setState({bidTypes: data.reduce((map, datum) => map.set(datum.id, datum.description), Map())})
    );
  }

  fetchYears(){
    fetchJson('/api/tendersAwardsYears').then(data => {
      const years = fromJS(data.map(pluck('_id')));
      this.setState({
        years,
        selectedYears: Set(years)
      })
    })
  }

  fetchUserInfo(){
    const noCacheUrl = new URI('/rest/userDashboards/getCurrentAuthenticatedUserDetails').addSearch('time', new Date());
    fetchJson(noCacheUrl).then(
        ({username, id, roles}) => this.setState({
          user: {
            loggedIn: true,
            isAdmin: roles.some(({authority}) => authority == ROLE_ADMIN),
            id
          }
        })
    ).catch(
        () => this.setState({
          user: {
            loggedIn: false
          }
        })
    )
  }

  componentDidMount(){
    this.fetchBidTypes();
    this.fetchYears();
    this.fetchUserInfo();

    this.setState({
      width: document.querySelector('.years-bar').offsetWidth - 30
    });

    window.addEventListener("resize", debounce(() => {
      this.setState({
        width: document.querySelector('.years-bar').offsetWidth - 30
      });
    }));
  }

  setMenuBox(e, slug){
    let {menuBox} = this.state;
    e.stopPropagation();
    this.setState({menuBox: menuBox == slug ? "" : slug})
  }

  filters(){
    let {menuBox, bidTypes, locale, user} = this.state;
    return <this.constructor.Filters
        onClick={e => this.setMenuBox(e, MENU_BOX_FILTERS)}
        onUpdate={filters => this.setState({filters, menuBox: ""})}
        open={menuBox == MENU_BOX_FILTERS}
        bidTypes={bidTypes}
        user={user}
        translations={this.constructor.TRANSLATIONS[locale]}
    />
  }

  comparison(){
    let {menuBox, compareBy} = this.state;
    return <div
        onClick={e => this.setMenuBox(e, MENU_BOX_COMPARISON)}
        className={cn("filters compare", {open: menuBox == MENU_BOX_COMPARISON})}
    >
      <img className="top-nav-icon" src="assets/icons/compare.svg" width="100%" height="100%"/>
      {this.t('header:comparison:title')}
      <i className="glyphicon glyphicon-menu-down"></i>
      <div className="box" onClick={e => e.stopPropagation()}>
        <div className="col-sm-6">
          <label>{this.t('header:comparison:criteria')}</label>
        </div>
        <div className="col-sm-6">
          <select
              className="form-control"
              value={compareBy}
              onChange={e => this.updateComparisonCriteria(e.target.value)}
          >
            {this.constructor.COMPARISON_TYPES.map(({value, label}, index) =>
              <option key={index} value={value}>{this.t(label)}</option>
            )}
          </select>
        </div>
      </div>
    </div>;
  }

  navigationLink({getName, icon}, index){
    return <a href="javascript:void(0);" key={index}
              className={cn("col-sm-12", {active: index == this.state.currentTab})}
              onClick={_ => this.setState({currentTab: index})}>
          <span className="circle">
            <img className="nav-icon" src={`assets/icons/${icon}.svg`}/>
            <i className={`glyphicon glyphicon-${icon}`}/>
          </span>
      &nbsp;
      {getName(this.t.bind(this))}
    </a>
  }

  navigation(){
    return this.tabs.map((tab, index) => this.navigationLink(tab, index));
  }

  updateData(path, data){
    this.setState({data: this.state.data.setIn(path, data)});
  }

  updateComparisonData(path, data){
    this.setState({comparisonData: this.state.comparisonData.setIn(path, data)})
  }

  content(){
    let {filters, compareBy, comparisonCriteriaValues, currentTab, selectedYears, selectedMonths, bidTypes, width,
        locale} = this.state;
    let Tab = this.tabs[currentTab];
    return <Tab
        filters={filters}
        compareBy={compareBy}
        comparisonCriteriaValues={comparisonCriteriaValues}
        requestNewData={(path, data) => this.updateData([currentTab, ...path], data)}
        requestNewComparisonData={(path, data) => this.updateComparisonData([currentTab, ...path], data)}
        data={this.state.data.get(currentTab) || fromJS({})}
        comparisonData={this.state.comparisonData.get(currentTab) || fromJS({})}
        monthly={this.showMonths()}
        years={selectedYears}
        months={selectedMonths}
        bidTypes={bidTypes}
        width={width}
        translations={this.constructor.TRANSLATIONS[locale]}
        styling={this.constructor.STYLING}
    />;
  }

  yearsBar(){
    const {years, selectedYears} = this.state;
    const toggleYear = year => this.setState({
      selectedYears: selectedYears.has(+year) ?
          selectedYears.delete(+year) :
          selectedYears.add(+year)
    });
    const toggleOthersYears = year => this.setState({
      selectedYears: 1 == selectedYears.count() && selectedYears.has(year) ?
          Set(years) :
          Set([year])
    });
    return years.sort().map(year =>
        <a
            key={year}
            href="javascript:void(0);"
            className={cn({active: selectedYears.has(+year)})}
            onDoubleClick={e => toggleOthersYears(year)}
            onClick={e => e.shiftKey ? toggleOthersYears(year) : toggleYear(year)}
        >
          <i className="glyphicon glyphicon-ok-circle"></i> {year}
          <span className="ctrl-click-hint">
            {this.t('yearsBar:ctrlClickHint')}
          </span>
        </a>
    ).toArray();
  }

  showMonths(){
    const {years, selectedYears} = this.state;
    return selectedYears.intersect(years).count() == 1;
  }

  monthsBar(){
    const {selectedMonths} = this.state;
    return range(1, 12).map(month => <a
        key={month}
        href="javascript:void(0);"
        className={cn({active: selectedMonths.has(+month)})}
        onClick={_ => this.setState({
          selectedMonths: selectedMonths.has(+month) ?
              selectedMonths.delete(+month) :
              selectedMonths.add(+month)
        })}
    >
      <i className="glyphicon glyphicon-ok-circle"></i> {this.t(`general:months:${month}`)}
    </a>)
  }

  setLocale(locale){
    this.setState({locale});
    localStorage.oceLocale = locale;
  }

  loginBox(){
    if(this.state.user.loggedIn){
      return <a href="/preLogout?referrer=/ui/index.html">
        <i className="glyphicon glyphicon-user"/> {this.t("general:logout")}
      </a>
    }
    return <a href="/login?referrer=/ui/index.html">
      <i className="glyphicon glyphicon-user"/> {this.t("general:login")}
    </a>
  }

  languageSwitcher(){
    const {TRANSLATIONS} = this.constructor;
    if(Object.keys(TRANSLATIONS).length <= 1) return null;
    return Object.keys(TRANSLATIONS).map(locale =>
        <img className="icon"
             src={`assets/flags/${locale}.png`}
             alt={`${locale} flag`}
             onClick={e => this.setLocale(locale)}
             key={locale}
        />
    )
  }

  downloadExcel(){
    let {filters, selectedYears: years, selectedMonths: months} = this.state;
    let onDone = () => this.setState({exporting: false});
    this.setState({exporting: true});
    download({
      ep: 'excelExport',
      filters,
      years,
      months,
      t: this.t.bind(this)
    }).then(onDone).catch(onDone);

  }

  exportBtn(){
    if(this.state.exporting){
      return (
          <div className="filters">
            <div className="progress">
              <div className="progress-bar progress-bar-danger" role="progressbar" style={{width: "100%"}}>
                {this.t('export:exporting')}
              </div>
            </div>
          </div>
      )
    }
    return <div className="filters" onClick={e => this.downloadExcel()}>
      <img className="top-nav-icon" src="assets/icons/export.svg" width="100%" height="100%"/>
      {this.t('export:export')}
      <i className="glyphicon glyphicon-menu-down"></i>
    </div>
  }

  toggleDashboardSwitcher(e){
    e.stopPropagation();
    const {dashboardSwitcherOpen} = this.state;
    this.setState({dashboardSwitcherOpen: !dashboardSwitcherOpen});
  }

  dashboardSwitcher(){
    const {dashboardSwitcherOpen} = this.state;
    const {onSwitch} = this.props;
    return (
      <div className={cn('dash-switcher-wrapper', {open: dashboardSwitcherOpen})}>
        <h1 onClick={this.toggleDashboardSwitcher.bind(this)}>
          {this.t('general:title')}
          <i className="glyphicon glyphicon-menu-down"></i>
          <small>{this.t('general:subtitle')}</small>
        </h1>
        {dashboardSwitcherOpen &&
         <div className="dashboard-switcher">
           <a href="javascript:void(0);" onClick={e => onSwitch('corruptionRiskDashboard')}>
             Corruption Risk Dashboard
           </a>
         </div>
        }
      </div>
    )
  }
}

OCApp.TRANSLATIONS = {
  us: {}
};

OCApp.Filters = Filters;

OCApp.STYLING = {
  charts: {
    axisLabelColor: undefined,
    traceColors: []
  }
};

OCApp.COMPARISON_TYPES = [{
  value: '',
  label: 'header:comparison:criteria:none'
}, {
  value: 'bidTypeId',
  label: 'header:comparison:criteria:bidType'
}, {
  value: 'procuringEntityId',
  label: 'header:comparison:criteria:procuringEntity'
}];

export default OCApp;
