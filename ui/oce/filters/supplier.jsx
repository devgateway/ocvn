import TypeAhead from "./inputs/type-ahead.jsx";

class Supplier extends TypeAhead{
    static getName(t){
        return t('filters:supplier:title');
    }
}

Supplier.endpoint = '/api/ocds/organization/supplier/all';

export default Supplier;
