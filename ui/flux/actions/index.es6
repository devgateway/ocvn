import dispatcher from "../dispatcher";
import constants from "./constants";
import {fetchJson, pluck, years, identity} from "../../tools";
import URI from "urijs";
import {toImmutable} from "nuclear-js";
import * as endpoints from "./endpoints";

let options2arr = options => Object.keys(options)
    .filter(key => options[key].selected)
    .map(identity);

let addFilters = (filters, url) => null === filters ? url :
    new URI(url).addSearch({
      bidTypeId: options2arr(filters.bidTypes.options),
      bidSelectionMethod: options2arr(filters.bidSelectionMethods.options),
      procuringEntityId: options2arr(filters.procuringEntities.options),
      year: Object.keys(filters.years).filter(year => filters.years[year])
    }).toString();

//Given [[1,2,3], ['a','b','c']] will produce [[1, 'a'], [2, 'b'], [3, 'c']]
let transpose = ([head, ...tail]) => head.map((el, index) => [el].concat(tail.map(pluck(index))));

let response2obj = (field, arr) => arr.reduce((obj, elem) => {
  obj[elem._id] = elem[field];
  return obj;
}, {});


let transformOverviewData = ([bidplansResponse, tendersResponse, awardsResponse]) => {
  let bidplans = response2obj('count', bidplansResponse);
  let tenders = response2obj('count', tendersResponse);
  let awards = response2obj('count', awardsResponse);
  return Object.keys(tenders).map(year => ({
    year: year,
    bidplan: bidplans[year],
    tender: tenders[year],
    award: awards[year]
  }));
};

let transformCostEffectivenessData = ([tenderResponse, awardResponse]) => {
  let tender = response2obj('totalTenderAmount', tenderResponse);
  let award = response2obj('totalAwardAmount', awardResponse);
  return Object.keys(tender).map(year => ({
    year: year,
    tender: tender[year],
    diff: tender[year] - award[year]
  }))
};

let transformBidPeriodData = ([tenders, awards]) => {
  let awardsHash = response2obj('averageAwardDays', awards);
  return tenders.map(tender => ({
      year: tender._id,
      tender: tender.averageTenderDays,
      award: awardsHash[tender._id] || 0
    })
  )
};

let transformCancelledData = raw => raw.map(({_id, totalCancelledTendersAmount}) => ({
  year: _id,
  count: totalCancelledTendersAmount
}));

export default {
  changeTab(slug){
    dispatcher.dispatch(constants.TAB_CHANGED, slug);
  },

  toggleYear(year, selected){
    dispatcher.dispatch(constants.YEAR_TOGGLED, {
      year: year,
      selected: selected
    });
  },

  changeContentWidth(newWidth){
    dispatcher.dispatch(constants.CONTENT_WIDTH_CHANGED, newWidth);
  },

  loadServerSideYearFilteredData(filters = null){
    let load = url => fetchJson(addFilters(filters, url));
    load('/api/topTenLargestTenders').then(data => dispatcher.dispatch(constants.TOP_TENDERS_DATA_UPDATED, data));
    load('/api/topTenLargestAwards').then(data => dispatcher.dispatch(constants.TOP_AWARDS_DATA_UPDATED, data));
  },

  loadData(filters = null){
    let load = url => fetchJson(addFilters(filters, url));
    this.loadServerSideYearFilteredData(filters);
    Promise.all([
      load(endpoints.COUNT_BID_PLANS_BY_YEAR),
      load(endpoints.COUNT_TENDERS_BY_YEAR),
      load(endpoints.COUNT_AWARDS_BY_YEAR)
    ])
        .then(transformOverviewData)
        .then(data => dispatcher.dispatch(constants.OVERVIEW_DATA_UPDATED, data));

    load(endpoints.PLANNED_FUNDING_BY_LOCATION).then(data => dispatcher.dispatch(constants.LOCATION_UPDATED, data));

    Promise.all([
      load(endpoints.COST_EFFECTIVENESS_TENDER_AMOUNT),
      load(endpoints.COST_EFFECTIVENESS_AWARD_AMOUNT)
    ])
        .then(transformCostEffectivenessData)
        .then(data => dispatcher.dispatch(constants.COST_EFFECTIVENESS_DATA_UPDATED, data));

    Promise.all([
      load(endpoints.AVERAGE_TENDER_PERIOD),
      load(endpoints.AVERAGE_AWARD_PERIOD)
    ]).then(transformBidPeriodData).then(data => dispatcher.dispatch(constants.BID_PERIOD_DATA_UPDATED, data));

    load(endpoints.TENDER_PRICE_BY_VN_TYPE_YEAR).then(data => dispatcher.dispatch(constants.BID_TYPE_DATA_UPDATED, data));

    load(endpoints.TOTAL_CANCELLED_TENDERS_BY_YEAR)
        .then(transformCancelledData)
        .then(data => dispatcher.dispatch(constants.CANCELLED_DATA_UPDATED, data));

    load('/api/averageNumberOfTenderers').then(data => dispatcher.dispatch(constants.AVERAGED_TENDERS_DATA_UPDATED, data));

    load('/api/percentTendersCancelled').then(data => dispatcher.dispatch(constants.CANCELLED_PERCENTS_DATA_UPDATED, data));

    load('/api/percentTendersUsingEBid').then(data => dispatcher.dispatch(constants.PERCENT_EBID_DATA_UPDATED, data));
  },

  bootstrap(){
    this.loadData();
    Promise.all([
      fetchJson('/api/ocds/bidType/all'),
      fetchJson('/api/ocds/bidSelectionMethod/all')
    ]).then(([bidTypes, bidSelectionMethods]) => dispatcher.dispatch(constants.FILTERS_DATA_UPDATED, {
      years: years().reduce((map, year) => map.set(year, true), toImmutable({})),
      bidTypes: {
        options: bidTypes.reduce((accum, bidType) => {
          let {id} = bidType;
          accum[id] = {
            id: id,
            description: bidType.description,
            selected: false
          };
          return accum;
        }, {})
      },
      bidSelectionMethods: {
        options: bidSelectionMethods
            .filter(method => !!method._id)
            .reduce((accum, {_id}) => {
              accum[_id] = {
                id: _id,
                description: _id,
                selected: false
              };
              return accum;
            }, {})
      },
      procuringEntities: {
        options: []
      }
    }));
  },

  loadComparisonData(criteria, filters){
    if(!criteria) return;
    let comparisonUrl = new URI(endpoints.COST_EFFECTIVENESS_TENDER_AMOUNT).addSearch({
      groupByCategory: criteria,
      pageSize: 3
    }).toString();
    fetchJson(addFilters(filters, comparisonUrl)).then(comparisonData => {
      dispatcher.dispatch(constants.COMPARISON_CRITERIA_NAMES_UPDATED, comparisonData);
      let addComparisonFilters = url => {
        let uri = new URI(addFilters(filters, url));
        let criteriaValues = comparisonData.map(pluck("bidTypeId" == criteria ? "0" : "_id"));
        return criteriaValues
            .map(criteriaValue => uri.clone().addSearch(criteria, criteriaValue).toString())
            .concat([
              uri.clone().addSearch(criteria, criteriaValues).addSearch('invert', 'true').toString()
            ]);
      };

      let load = url => Promise.all(addComparisonFilters(url).map(fetchJson));

      Promise.all([
          load(endpoints.COUNT_BID_PLANS_BY_YEAR),
          load(endpoints.COUNT_TENDERS_BY_YEAR),
          load(endpoints.COUNT_AWARDS_BY_YEAR)
      ])
          .then(data => transpose(data).map(transformOverviewData))
          .then(data => dispatcher.dispatch(constants.OVERVIEW_COMPARISON_DATA_UPDATED, data));

      Promise.all([
        load(endpoints.COST_EFFECTIVENESS_TENDER_AMOUNT),
        load(endpoints.COST_EFFECTIVENESS_AWARD_AMOUNT)
      ])
          .then(data => transpose(data).map(transformCostEffectivenessData))
          .then(data => dispatcher.dispatch(constants.COST_EFFECTIVENESS_COMPARISON_DATA_UPDATED, data));

      Promise.all([
        load(endpoints.AVERAGE_TENDER_PERIOD),
        load(endpoints.AVERAGE_AWARD_PERIOD)
      ])
          .then(data => transpose(data).map(transformBidPeriodData))
          .then(data => dispatcher.dispatch(constants.BID_PERIOD_COMPARISON_DATA_UPDATED, data));

      load(endpoints.TENDER_PRICE_BY_VN_TYPE_YEAR).then(data =>
          dispatcher.dispatch(constants.BID_TYPE_COMPARISON_DATA_UPDATED, data));

      load(endpoints.TOTAL_CANCELLED_TENDERS_BY_YEAR)
          .then(data => data.map(transformCancelledData))
          .then(data => dispatcher.dispatch(constants.CANCELLED_COMPARISON_DATA_UPDATED, data));
    });
  },

  setMenuBox(slug){
    dispatcher.dispatch(constants.MENU_BOX_CHANGED, slug);
  },

  toggleFilterOption(slug, option, selected){
    dispatcher.dispatch(constants.FILTER_OPTIONS_TOGGLED, {slug, option, selected});
  },

  updateProcuringEntityQuery(newQuery){
    dispatcher.dispatch(constants.PROCURING_ENTITY_QUERY_UPDATED, newQuery);
    if (newQuery.length >= 3) {
      fetchJson(new URI('/api/ocds/organization/procuringEntity/all').addSearch('text', newQuery).toString())
          .then(data => {
            dispatcher.dispatch(constants.PROCURING_ENTITIES_UPDATED, data.reduce((accum, procuringEntity) => {
              let {id} = procuringEntity;
              accum[id] = procuringEntity;
              return accum;
            }, {}))
          });
    }
  },

  updateComparisonCriteria(criteria){
    dispatcher.dispatch(constants.COMPARISON_CRITERIA_UPDATED, criteria);
  },
  
  setLocale(loc){
    localStorage.lang = loc;
    dispatcher.dispatch(constants.LOCALE_CHANGED, loc);
  },

  applyFilters(){
    dispatcher.dispatch(constants.FILTERS_APPLIED);
  },

  resetFilters(){
    dispatcher.dispatch(constants.FILTERS_RESET);
  },

  toggleCancelledPercents(bool){
    dispatcher.dispatch(constants.CANCELLED_TYPE_TOGGLED, bool);
  }
}