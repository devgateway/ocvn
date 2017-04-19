import cn from "classnames";
import {Set, Map} from "immutable";
import Organizations from "./organizations";
import ProcurementMethodBox from "./procurement-method";
import ValueAmount from "./value-amount";
import DateBox from "./date";
import {fetchJson, range, pluck} from "../../tools";

class Filters extends React.Component{
  constructor(...args){
    super(...args);
		const months = range(1, 12);
    this.state = {
			allMonths: months,
			allYears: [],
      state: Map({
				months: Set(months),
				years: Set()
			})
    }
  }

	componentDidMount(){
		fetchJson('/api/tendersAwardsYears').then(data => {
			const years = data.map(pluck('_id'));
			const {state} = this.state;
			this.setState({
				allYears: years,
				state: state.set('years', Set(years))
			}, () => this.props.onUpdate(this.state.state));
    });
	}

  render(){
    const {onUpdate, translations, currentBoxIndex, requestNewBox} = this.props;
    const {state, allYears, allMonths} = this.state;
    const {BOXES} = this.constructor;
    return (
      <div className="row filters-bar" onClick={e => e.stopPropagation()}>
        <div className="col-lg-1 col-md-1 col-sm-1">
        </div>
        <div className="col-lg-9 col-md-9 col-sm-9">
        <div className="title">Filter your data</div>
          {BOXES.map((Box, index) => {
             return (
               <Box
                   key={index}
                   open={currentBoxIndex === index}
                   onClick={e => requestNewBox(currentBoxIndex === index ? null : index)}
                   state={state}
                   onUpdate={(slug, newState) => this.setState({state: state.set(slug, newState)})}
                   translations={translations}
                   onApply={e => {requestNewBox(null); onUpdate(state)}}
									 allYears={allYears}
									 allMonths={allMonths}
               />
             )
           })}
        </div>
        <div className="col-lg-1 col-md-1 col-sm-1 download">
          <button className="btn btn-success">
            <i className="glyphicon glyphicon-download-alt"></i>
          </button>
        </div>
        <div className="col-lg-1 col-md-1 col-sm-1"></div>
      </div>
    )
  }
}

Filters.BOXES = [
  Organizations,
  ProcurementMethodBox,
  ValueAmount,
	DateBox
];

export default Filters;
