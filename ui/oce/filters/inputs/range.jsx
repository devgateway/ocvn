import translatable from "../../translatable";
import Component from "../../pure-render-component";
import InputRange from "react-input-range";
import IRstyles from "react-input-range/dist/react-input-range.css";

const BILLION = 1000000000;
const MILLION = 1000000;
const THOUSAND = 1000;

let labelFormatter =  number => {
    if(typeof number == "undefined") return number;
    if(number >= BILLION) return (number/BILLION).toFixed(2) + "B";
    if(number >= MILLION) return (number/MILLION).toFixed(2) + "M";
    if(number >= THOUSAND) return (number/THOUSAND).toFixed(2) + "K";
    return number.toFixed(2);
}

class MultipleSelect extends translatable(Component){
  render(){
    if(!this.state) return null;
    let {onUpdate, minValue, maxValue} = this.props;
    let {min, max} = this.state;
    return (
        <section className="field">
          <header>
            {this.getTitle()}
          </header>
          <section className="options range">
              <InputRange
                  minValue={min}
                  maxValue={max}
                  value={{min: minValue || min, max: maxValue || max}}
                  onChange={(_, newVal) => onUpdate(newVal)}
                  formatLabel={labelFormatter}
              />
          </section>
        </section>
    )
  }
}

export default MultipleSelect;
