import cn from "classnames";
import {Set, Map} from "immutable";

class Filter extends React.Component{
  render(){
    const {title, open, onClick} = this.props;
    return(
      <div onClick={onClick} className={cn('filter', {open})}>
      {title}
      <i className="glyphicon glyphicon-menu-down"></i>
      {open && <div className="dropdown" onClick={e => e.stopPropagation()}>
  {this.props.children}
      </div>}
      </div>
    )
  }
}

import TypeAhead from "../../filters/inputs/type-ahead";

class Organizations extends TypeAhead{
  static getName(t){
    return 'Organizations';
  }
}

Organizations.endpoint = '/api/ocds/organization/all';

import MultipleSelect from "../../filters/inputs/multiple-select";

class ProcurementMethod extends MultipleSelect{
  getTitle(t){
    return 'Procurement method';
  }

  getId(option){
    return option.get('_id');
  }

  getLabel(option){
    return option.get('_id');
  }

  transform(data){
    return data.filter(({_id}) => !!_id);
  }
}

ProcurementMethod.ENDPOINT = 'ocds/procurementMethod/all';

import TenderPrice from "../../filters/tender-price";
import AwardValue from "../../filters/award-value";

class Filters extends React.Component{
  render(){
    const {box, requestNewFilterBox, onUpdate, state} = this.props;
    const setBox = newBox => e => {
      e.stopPropagation();
      if(box == newBox){
        requestNewFilterBox('');
      } else {
        requestNewFilterBox(newBox);
      }
    };

    const filters = [{
      title: "Organizations",
      slug:  "organizations",
      Component: Organizations
    },{
      title: "Procurement method",
      slug:  "procurementMethod",
      Component: ProcurementMethod
    }];
    /* ,{
       title: "Value Amount",
       slug:  "valueAmount"
       },{
       title: "Date",
       slug:  "date"
       },{
       title: "Location",
       slug:  "location"
       }];*/
    const minTenderPrice = state.get('minTenderPrice');
    const maxTenderPrice = state.get('maxTenderPrice');
    const minAwardValue = state.get('minAwardValue');
    const maxAwardValue = state.get('maxAwardValue');
    return (
      <div className="row filters-bar">
      <div className="col-lg-3 col-md-2 col-sm-1 title text-right">
      Filter your data
      </div>
      <div className="col-lg-7 col-md-9 col-sm-10">
	    {filters.map(({title, slug, Component}, index) => {
        const selected = state.get(slug, Set());
        const toggle = id => onUpdate(state.set(slug, selected.has(id) ?
                                                selected.delete(id) :
                                                selected.add(id)));
        return (
  	      <Filter
    	    	  title={title}
	        	  open={box == slug}
		          onClick={setBox(slug)}
  	          key={index}
    	    >
            <Component
                selected={selected}
                translations={Map()}
                onToggle={toggle}
            />
          </Filter>
        )
      })}
      <Filter
      title="Value amount"
      open={box == 'valueAmount'}
      onClick={setBox('valueAmount')}
      key={'valueAmount'}
      >
      <TenderPrice
          translations={Map()}
          minValue={minTenderPrice}
          maxValue={maxTenderPrice}
          onUpdate={({min, max}) => {
              minTenderPrice != min && onUpdate(state.set("minTenderValue", min));
              maxTenderPrice != max && onUpdate(state.set("maxTenderValue", max));
            }}
      />
      <AwardValue
          translations={Map()}
          minValue={minAwardValue}
          maxValue={maxAwardValue}
          onUpdate={({min, max}) => {
              minAwardValue != min && onUpdate(state.set("minAwardValue", min));
              maxAwardValue != max && onUpdate(state.set("maxAwardValue", max));
            }}
      />
          </Filter>
      </div>
      <div className="col-lg-2 col-md-1 col-sm-1 download">
        <button className="btn btn-success">
          <i className="glyphicon glyphicon-download-alt"></i>
        </button>
      </div>
      </div>
    )
  }
}

export default Filters;
