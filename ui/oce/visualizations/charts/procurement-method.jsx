import FrontendYearFilterableChart from "./frontend-filterable";
import {pluckImm, max} from "../../tools";
import {Map, Set} from "immutable";
import Comparison from "../../comparison";

class ProcurementMethod extends FrontendYearFilterableChart{
  static toCats(data){
    if(!data) return Map();
    return data
        .groupBy(pluckImm(this.PROCUREMENT_METHOD_FIELD))
        .map((bidTypes, _id) => Map({
          _id,
          totalTenderAmount: bidTypes.map(pluckImm('totalTenderAmount')).reduce((a, b) => a + b, 0)
        }));
  }

  getCats(data){
    let cats = this.props.cats || Map();
    return cats.merge(this.constructor.toCats(data));
  }

  getData(){
    let raw = super.getData();
    if(!raw) return [];
    let data = this.getCats(raw);

    return [{
      x: data.map(imm => imm.get('_id') || this.__('Unspecified')).toArray(),
      y: data.map(pluckImm('totalTenderAmount')).toArray(),
      type: 'bar',
      marker: {
        color: this.props.styling.charts.traceColors[0]
      }
    }];
  }

  getLayout(){
    return {
      xaxis: {
        title: this.__("Method"),
        type: "category"
      },
      yaxis: {
        title: this.__("Amount (in VND)")
      }
    }
  }
}

ProcurementMethod.endpoint = 'tenderPriceByProcurementMethodYear';
ProcurementMethod.getName = __ => __('Procurement method');
ProcurementMethod.UPDATABLE_FIELDS = ['data', 'years', 'cats'];
ProcurementMethod.PROCUREMENT_METHOD_FIELD = 'procurementMethod';

class ProcurementMethodComparison extends Comparison{
  render(){
    let {compareBy, comparisonData, comparisonCriteriaValues, filters, requestNewComparisonData, years, translations,
      styling} = this.props;
    if(!comparisonCriteriaValues.length) return null;
    let Component = this.getComponent();
    let decoratedFilters = this.constructor.decorateFilters(filters, compareBy, comparisonCriteriaValues);
    let rangeProp, cats;

    if(comparisonData.count() == comparisonCriteriaValues.length + 1){
      let uniformData = comparisonData.map(comparisonDatum =>
          Component.toCats(Component.filterDataByYears(comparisonDatum, years))
      ).reduce(
          (a, b) => a.mergeWith(
              (a, b) => a.get('totalTenderAmount') > b.get('totalTenderAmount') ? a : b
              , b)
      );

      let maxValue = uniformData.map(pluckImm('totalTenderAmount')).reduce(max, 0);

      rangeProp = {
        yAxisRange: [0, maxValue]
      };

      cats = uniformData.map(uniformDatum => uniformDatum.set('totalTenderAmount', undefined))
    } else {
      cats = Map();
      rangeProp = {};
    }

    return this.wrap(decoratedFilters.map((comparisonFilters, index) => {
      let ref = `visualization${index}`;
      return <div className="col-md-6" key={index}>
        <Component
            filters={comparisonFilters}
            requestNewData={(_, data) => requestNewComparisonData([index], data)}
            data={comparisonData.get(index)}
            years={years}
            title={this.getTitle(index)}
            cats={cats}
            translations={translations}
            styling={styling}
            {...rangeProp}
        />
        <div className="chart-toolbar"
             onClick={e => this.refs[ref].querySelector(".modebar-btn:first-child").click()}
        >
          <div className="btn btn-default">
            <img src="assets/icons/camera.svg"/>
          </div>
        </div>
      </div>
    }));
  }
}


ProcurementMethod.compareWith = ProcurementMethodComparison;

export default ProcurementMethod;