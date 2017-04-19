import FilterBox from "./box";

import MultipleSelect from "../../filters/inputs/multiple-select";

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
