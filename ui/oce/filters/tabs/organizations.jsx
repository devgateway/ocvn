import Tab from "./index";
import ProcuringEntity from "../procuring-entity";
import Supplier from "../supplier.jsx";
import {Set} from "immutable";

class Organizations extends Tab{
  render(){
    let {state, onUpdate, translations} = this.props;
    let selectedProcuringEntitiesId = state.get('procuringEntityId', Set());
    return <div>
      {this.renderChild(ProcuringEntity, 'procuringEntityId')}
      {this.renderChild(Supplier, 'supplierId')}
    </div>
  }
}

Organizations.getName = __ => __('Organizations');

export default Organizations;
