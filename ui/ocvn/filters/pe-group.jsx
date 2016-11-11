import MultipleSelect from "../../oce/filters/inputs/multiple-select";

class PEGroup extends MultipleSelect{
  getTitle(){return this.t('filters:peGroup:title')}
}

PEGroup.ENDPOINT = 'ocds/orgGroup/all';

export default PEGroup;