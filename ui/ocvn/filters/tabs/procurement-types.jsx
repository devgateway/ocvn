import Tab from "../../../oce/filters/tabs";
import BidTypes from "../bid-types";
import {Set} from "immutable";
import BidSelectionMethod from "../bid-selection-method";

class ProcurementTypes extends Tab{
  render(){
    let {state, onUpdate, bidTypes} = this.props;
    let selectedBidTypesIds = state.get('bidTypeId', Set());
    let selectedBidSelectionMethods = state.get("bidSelectionMethod", Set());
    return <div>
      <BidTypes
          options={bidTypes}
          selected={selectedBidTypesIds}
          onToggle={id => onUpdate('bidTypeId', selectedBidTypesIds.has(id) ?
              selectedBidTypesIds.delete(id) :
              selectedBidTypesIds.add(id))
          }
      />

      <BidSelectionMethod
          selected={selectedBidSelectionMethods}
          onToggle={id => onUpdate('bidSelectionMethod', selectedBidSelectionMethods.has(id) ?
              selectedBidSelectionMethods.delete(id) :
              selectedBidSelectionMethods.add(id))
          }
      />
    </div>
  }
}

ProcurementTypes.getName = __ => __('Procurement types');

export default ProcurementTypes;