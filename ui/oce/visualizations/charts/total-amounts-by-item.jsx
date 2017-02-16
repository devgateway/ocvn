import BidsByItem from "./bids-by-item";

class TotalAmountsByItem extends BidsByItem{
  static getName(t){return t('charts:amountsByItem:title')}
}

TotalAmountsByItem.excelEP = '';
TotalAmountsByItem.CAT_VALUE_FIELD = 'totalTenderAmount';

export default TotalAmountsByItem;