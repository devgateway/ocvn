import MultipleSelect from './inputs/multiple-select';

export default class ProcurementMethod extends MultipleSelect{
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
