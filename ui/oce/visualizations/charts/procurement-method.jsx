import Chart from "./index";
import {pluckImm} from "../../tools";
import {Map, OrderedMap, Set} from "immutable";
import Comparison from "../../comparison";
import backendYearFilterable from "../../backend-year-filterable";

class ProcurementMethod extends backendYearFilterable(Chart){
  getData(){
    let data = super.getData();
    if(!data) return [];

    return [{
      x: data.map(imm => imm.get(this.constructor.PROCUREMENT_METHOD_FIELD) || this.__('Unspecified')).toArray(),
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

ProcurementMethod.endpoint = 'tenderPriceByProcurementMethod';
ProcurementMethod.getName = __ => __('Procurement method');
ProcurementMethod.UPDATABLE_FIELDS = ['data'];
ProcurementMethod.PROCUREMENT_METHOD_FIELD = 'procurementMethod';

class ProcurementMethodComparison extends Comparison{
  render(){
    let {compareBy, comparisonData, comparisonCriteriaValues, filters, requestNewComparisonData, years, translations,
        styling} = this.props;
    if(!comparisonCriteriaValues.length) return null;
    let Component = this.getComponent();
    let decoratedFilters = this.constructor.decorateFilters(filters, compareBy, comparisonCriteriaValues);
    let rangeProp, uniformData;

    if(comparisonData.count() == comparisonCriteriaValues.length + 1){
      let byCats = comparisonData.map(
          data => data.reduce(
              (cats, datum) => cats.set(datum.get(Component.PROCUREMENT_METHOD_FIELD), datum),
              Map()
          )
      );

      let cats = comparisonData.reduce(
          (cats, data) => data.reduce(
              (cats, datum) => {
                let cat = datum.get(Component.PROCUREMENT_METHOD_FIELD);
                return cats.set(cat, Map({
                  [Component.PROCUREMENT_METHOD_FIELD]: cat,
                  totalTenderAmount: 0
                }))
              },
              cats
          ),
          Map()
      );

      uniformData = byCats.map(
          data => cats.merge(data).toList()
      );

      let maxValue = uniformData.reduce(
          (max, data) => data.reduce(
              (max, datum) => Math.max(max, datum.get('totalTenderAmount'))
              , max
          )
          , 0
      );

      rangeProp = {
        yAxisRange: [0, maxValue]
      };
    } else {
      rangeProp = {};
      uniformData = comparisonData;
    }

    return this.wrap(decoratedFilters.map((comparisonFilters, index) => <div className="col-md-6" key={index}>
          <Component
              filters={comparisonFilters}
              requestNewData={(_, data) => requestNewComparisonData([index], data)}
              data={uniformData.get(index)}
              years={years}
              title={this.getTitle(index)}
              translations={translations}
              styling={styling}
              {...rangeProp}
          />
        </div>
    ));
  }
}


ProcurementMethod.compareWith = ProcurementMethodComparison;

export default ProcurementMethod;