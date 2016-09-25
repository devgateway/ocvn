import ProcurementMethod from "../../oce/visualizations/charts/procurement-method";

class BidSelectionMethod extends ProcurementMethod{}

BidSelectionMethod.endpoint = 'tenderPriceByAllBidSelectionMethods';
BidSelectionMethod.getName = __ => __('Bid selection method');
ProcurementMethod.PROCUREMENT_METHOD_FIELD = 'procurementMethodDetails';
ProcurementMethod.excelEP = 'bidSelectionExcelChart';

export default BidSelectionMethod;
