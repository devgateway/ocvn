import Chart from "./index";
import {download} from "../../tools";
import {Map, OrderedMap, Set} from "immutable";
import Comparison from "../../comparison";
import backendYearFilterable from "../../backend-year-filterable";

class ProcurementMethod extends backendYearFilterable(Chart){
  getData(){
    let data = super.getData();
    if(!data) return [];
    let {traceColors, hoverFormatter} = this.props.styling.charts;
    let trace = {
      x: [],
      y: [],
      type: 'bar',
      marker: {
        color: traceColors[0]
      }
    };

    if(hoverFormatter){
      trace.text = [];
      trace.hoverinfo = "text";
    }

    for(let datum of data){
      let cat = datum.get(this.constructor.PROCUREMENT_METHOD_FIELD) || this.t('charts:procurementMethod:unspecified');
      let totalTenderAmount = datum.get('totalTenderAmount');
      trace.x.push(cat);
      trace.y.push(totalTenderAmount);
      if(hoverFormatter) trace.text.push(hoverFormatter(totalTenderAmount));
    }

    return [trace];
  }

  getLayout(){
    return {
      xaxis: {
        title: this.t('charts:procurementMethod:xAxisName'),
        type: "category"
      },
      yaxis: {
        title: this.t('charts:procurementMethod:yAxisName')
      }
    }
  }
}

ProcurementMethod.endpoint = 'tenderPriceByProcurementMethod';
ProcurementMethod.excelEP = 'procurementMethodExcelChart';
ProcurementMethod.getName = t => t('charts:procurementMethod:title');
ProcurementMethod.UPDATABLE_FIELDS = ['data'];
ProcurementMethod.PROCUREMENT_METHOD_FIELD = 'procurementMethod';

class ProcurementMethodComparison extends Comparison{
  render(){
    let {compareBy, comparisonData, comparisonCriteriaValues, filters, requestNewComparisonData, years, translations,
        styling, width} = this.props;
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

    return this.wrap(decoratedFilters.map((comparisonFilters, index) => {
      let ref = `visualization${index}`;
      let downloadExcel = e => download({
        ep: Component.excelEP,
        filters: comparisonFilters,
        years,
        t: this.t.bind(this)
      });
      return <div className="col-md-6 comparison" key={index}>
        <Component
            filters={comparisonFilters}
            margin={{b: 200}}
            requestNewData={(_, data) => requestNewComparisonData([index], data)}
            data={uniformData.get(index)}
            years={years}
            title={this.getTitle(index)}
            translations={translations}
            styling={styling}
            width={width / 2}
            {...rangeProp}
        />
        <div className="chart-toolbar">
          <div className="btn btn-default" onClick={downloadExcel}>
            <img src="assets/icons/export-black.svg" width="16" height="16"/>
          </div>

          <div className="btn btn-default" onClick={e => this.refs[ref].querySelector(".modebar-btn:first-child").click()}>
            <img src="assets/icons/camera.svg"/>
          </div>
        </div>
      </div>
    }));
  }
}


ProcurementMethod.compareWith = ProcurementMethodComparison;

export default ProcurementMethod;