import FilterBox from "./box";
import MultipleSelect from "../../filters/inputs/multiple-select";
import {Set} from "immutable";

class ProcurementMethod extends MultipleSelect{
  getTitle(t){
    return 'Procurement method';
  }

  getId(option){
    return option.get('_id');
  }

  getLabel(option){
    return option.get('_id');
  }

  transform(data){
    return data.filter(({_id}) => !!_id);
  }
}

ProcurementMethod.ENDPOINT = 'ocds/procurementMethod/all';

class ProcurementMethodBox extends FilterBox{
  isActive(){
    const {appliedFilters} = this.props;
    return appliedFilters.get('procurementMethod', Set()).count() > 0;
  }

  reset(){
    const {onApply, state} = this.props;
    onApply(state.delete('procurementMethod'));
  }

  getTitle(){
    return 'Procurement Method';
  }

  getBox(){
    return (
      <div className="box-content">
        {this.renderChild(ProcurementMethod, 'procurementMethod')}
      </div>
    )
  }
}

export default ProcurementMethodBox;
