import MultipleSelect from "../../oce/filters/inputs/multiple-select";

class ContractMethod extends MultipleSelect{
  getTitle(){
    return this.__('Contract method');
  }

  getId(option){
    return option.get('id');
  }

  getLabel(option){
    return option.get('details');
  }
}

ContractMethod.ENDPOINT = 'ocds/contrMethod/all';

export default ContractMethod;