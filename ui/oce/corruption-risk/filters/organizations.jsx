import FilterBox from "./box";
import ProcuringEntity from "../../filters/procuring-entity";
import Supplier from "../../filters/supplier";

class Organizations extends FilterBox{
  getTitle(){
    return 'Organizations';
  }

  getBox(){
    return (
      <div className="box-content">
        {this.renderChild(ProcuringEntity, 'procuringEntityId')}
        {this.renderChild(Supplier, 'supplierId')}
      </div>
    )
  }
}

export default Organizations;
