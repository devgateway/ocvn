import ProcurementMethod from "../../../oce/visualizations/charts/procurement-method";

class BidSelectionMethod extends ProcurementMethod{}

BidSelectionMethod.endpoint = 'tenderPriceByVnTypeYear';
BidSelectionMethod.getName = __ => __('Bid selection method');
ProcurementMethod.PROCUREMENT_METHOD_FIELD = 'procurementMethodDetails';

export default BidSelectionMethod;