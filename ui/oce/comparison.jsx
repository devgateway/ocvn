import PureRenderCompoent from "./pure-render-component";
import translatable from "./translatable";
import {max, cacheFn, download} from "./tools";
import {List, Set, Map} from "immutable";

let computeUniformYears = cacheFn((Component, comparisonData, years) =>
    comparisonData.reduce((res, data) =>
            res.union(Component.computeYears(data))
        , Set()).intersect(years).sort()
);

class Comparison extends translatable(PureRenderCompoent){
  getComponent(){
    return this.props.Component;
  }

  wrap(children){
    return <div>
      <h3 className="page-header">{this.getComponent().getName(this.__.bind(this))}</h3>
      <div className="row">
        {children}
      </div>
    </div>
  }

  getTitle(index){
    let {compareBy, bidTypes, comparisonCriteriaValues} = this.props;
    if("bidTypeId" == compareBy){
      return bidTypes.get(comparisonCriteriaValues[index], this.__('Other'))
    }
    return comparisonCriteriaValues[index] || this.__('Other');
  }

  render(){
    let {compareBy, comparisonData, comparisonCriteriaValues, filters, requestNewComparisonData, years, width
        , translations, styling} = this.props;
    if(!comparisonCriteriaValues.length) return null;
    let Component = this.getComponent();
    let decoratedFilters = this.constructor.decorateFilters(filters, compareBy, comparisonCriteriaValues);
    let rangeProp, uniformData;

    if(comparisonData.count() == comparisonCriteriaValues.length + 1){
      uniformData = this.constructor.computeUniformData(Component, comparisonData, years);

      let maxValue = uniformData.map(datum =>
          datum.map(Component.getMaxField).reduce(max, 0)
      ).reduce(max, 0);

      rangeProp = {
        [Component.horizontal ? 'xAxisRange' : 'yAxisRange']: [0, maxValue]
      }
    } else {
      uniformData = Map();
      rangeProp = {};
    }

    return this.wrap(decoratedFilters.map((comparisonFilters, index) => {
      let ref = `visualization${index}`;
      let downloadExcel = e => download({
        ep: Component.excelEP,
        filters: comparisonFilters,
        years,
        __: this.__.bind(this)
      });
      return <div className="col-md-6 comparison" key={index} ref={ref}>
        <Component
            filters={comparisonFilters}
            requestNewData={(_, data) => requestNewComparisonData([index], data)}
            data={uniformData.get(index)}
            years={years}
            title={this.getTitle(index)}
            width={width / 2}
            translations={translations}
            styling={styling}
            {...rangeProp}
        />
        <div className="chart-toolbar">
          {Component.excelEP && <div className="btn btn-default" onClick={downloadExcel}>
            <img src="assets/icons/export-black.svg" width="16" height="16"/>
          </div>}

          <div className="btn btn-default" onClick={e => this.refs[ref].querySelector(".modebar-btn:first-child").click()}>
            <img src="assets/icons/camera.svg"/>
          </div>
        </div>
      </div>
    }));
  }
}

Comparison.decorateFilters = cacheFn((filters, compareBy, comparisonCriteriaValues) =>
    List(comparisonCriteriaValues)
        .map(criteriaValue => filters.set(compareBy, criteriaValue))
        .push(filters.set(compareBy, comparisonCriteriaValues).set('invert', 'true')));

Comparison.computeUniformData = cacheFn((Component, comparisonData, years) =>
    comparisonData.map(uniformDatum =>
        uniformDatum.reduce((res, datum) => res.has(+datum.get('year')) ? res.set(+datum.get('year'), datum) : res,
            computeUniformYears(Component, comparisonData, years).reduce((map, year) => map.set(year, Component.getFillerDatum(year)), Map())
        ).toList()
    )
);

export default Comparison;