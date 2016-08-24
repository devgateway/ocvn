import Amounts from "./amounts";
import Percents from "./percents";
import translatable from "../../../translatable";
import {Set} from "immutable";
import Comparison from "../../../comparison";
import ReactDOM from "react-dom";

class Cancelled extends translatable(React.Component){
  constructor(props){
    super(props);
    this.state = {
      percents: false
    }
  }

  render(){
    let {percents} = this.state;
    let Chart = percents ? Percents : Amounts;
    return <section>
      <h4 className="page-header">
        {percents ? this.__('Cancelled funding (%)') : this.__('Cancelled funding')}
        &nbsp;
        <button
            className="btn btn-default btn-sm"
            onClick={_ => this.setState({percents: !percents})}
            dangerouslySetInnerHTML={{__html: percents ? '&#8363;' : '%'}}
        />
        <img
            src="assets/icons/camera.svg"
            className="camera-icon"
            onClick={e => ReactDOM.findDOMNode(this).querySelector(".modebar-btn:first-child").click()}
        />
      </h4>
      <Chart {...this.props}/>
    </section>
  }

  static computeYears(data){
    if(!data) return Set();
    return Amounts.computeYears(data).union(Percents.computeYears(data));
  }
}

Cancelled.dontWrap = true;
Cancelled.comparable = true;
Cancelled.compareWith = class CancelledComparison extends Comparison{
  constructor(props){
    super(props);
    this.state = {
      percents: false
    }
  }

  getComponent(){
    return this.state.percents ? Percents : Amounts;
  }

  wrap(children){
    let {percents} = this.state;
    return <div>
      <h3 className="page-header">
        {percents ? this.__('Cancelled funding (%)') : this.__('Cancelled funding')}
        &nbsp;
        <button
            className="btn btn-default btn-sm"
            onClick={_ => this.setState({percents: !percents})}
            dangerouslySetInnerHTML={{__html: percents ? '&#8363;' : '%'}}
        />
      </h3>
      <div className="row">
        {children}
      </div>
    </div>
  }
};

export default Cancelled;