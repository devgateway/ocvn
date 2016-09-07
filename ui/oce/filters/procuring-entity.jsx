import TypeAhead from "./inputs/type-ahead";

class ProcuringEntity extends TypeAhead{
  static getName(__){
      return __('Procuring entity');
  }
}

ProcuringEntity.endpoint = '/api/ocds/organization/procuringEntity/all';

export default ProcuringEntity
