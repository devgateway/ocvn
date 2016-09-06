import Tab from "./index";
import TenderPrice from "../tender-price";

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
      </div>
  }
}

Amount.getName = __ => __('Amounts');

export default Amount;
