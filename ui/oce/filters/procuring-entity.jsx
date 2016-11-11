import TypeAhead from "./inputs/type-ahead";

class ProcuringEntity extends TypeAhead{
  static getName(t){
      return t('filters:procuringEntity:title');
  }
}

ProcuringEntity.endpoint = '/api/ocds/organization/procuringEntity/all';

export default ProcuringEntity
