import TypeAhead from "./inputs/type-ahead.jsx";

class Supplier extends TypeAhead{
    static getName(__){
        return __("Supplier");
    }
}

Supplier.endpoint = '/api/ocds/organization/supplier/all';

export default Supplier;
