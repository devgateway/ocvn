import Tab from "./index";
import TenderPrice from "../tender-price";
import AwardValue from "../award-value";

class Amount extends Tab{
    renderChild(slug, Component){
        let {state, onUpdate, translations} = this.props;
        return <Component
          translations={translations}
          minValue={state.get("min"+slug)}
          maxValue={state.get("max"+slug)}
          onUpdate={({min, max}) => {
              onUpdate("min"+slug, min);
              onUpdate("max"+slug, max);
          }}
        />
    }
    
    render(){
      return <div>
        {this.renderChild("TenderValue", TenderPrice)}
        {this.renderChild("AwardValue", AwardValue)}
      </div>
  }
}

Amount.getName = __ => __('Amounts');

export default Amount;
