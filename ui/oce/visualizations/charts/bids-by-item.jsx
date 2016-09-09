import Chart from "./index";
import {Map, OrderedMap, Set} from "immutable";
import Comparison from "../../comparison";
import backendYearFilterable from "../../backend-year-filterable";

class BidsByItem extends backendYearFilterable(Chart){
  static getName(__){
    return __('Number of bids by item');
  }

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
      let name = datum.get('description');
      let totalTenders = datum.get('totalTenders');
      trace.x.push(name);
      trace.y.push(totalTenders);
      if(hoverFormatter) trace.text.push(hoverFormatter(totalTenders));
    }

    return [trace];
  }

  getLayout(){
    return {
      xaxis: {
        title: this.__("Item"),
        type: "category"
      },
      yaxis: {
        title: this.__("Count")
      }
    }
  }
}

BidsByItem.endpoint = 'numberOfTendersByItemClassification';
BidsByItem.excelEP = 'numberOfTendersByItemExcelChart';
BidsByItem.UPDATABLE_FIELDS = ['data'];

class BidsByItemComparison extends Comparison{
  render(){
    let {compareBy, comparisonData, comparisonCriteriaValues, filters, requestNewComparisonData, years, translations,
        styling} = this.props;
    if(!comparisonCriteriaValues.length) return null;
    let Component = this.getComponent();
    let decoratedFilters = this.constructor.decorateFilters(filters, compareBy, comparisonCriteriaValues);
    let rangeProp, uniformData;

    if(comparisonData.count() == comparisonCriteriaValues.length + 1){
      let byItems = comparisonData.map(
          data => data.reduce(
              (items, datum) => items.set(datum.get('description'), datum),
              Map()
          )
      );

      let items = comparisonData.reduce(
          (items, data) => data.reduce(
              (items, datum) => {
                let item = datum.get('description');
                return items.set(item, Map({
                  description: item,
                  totalTenders: 0
                }))
              },
              items
          ),
          Map()
      );

      uniformData = byItems.map(
          data => items.merge(data).toList()
      );

      let maxValue = uniformData.reduce(
          (max, data) => data.reduce(
              (max, datum) => Math.max(max, datum.get('totalTenders'))
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
      return <div className="col-md-6 comparison" key={index}>
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


BidsByItem.compareWith = BidsByItemComparison;

export default BidsByItem;