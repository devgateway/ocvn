import translatable from "../../translatable";
import Component from "../../pure-render-component";
import {fetchJson} from "../../tools";
import URI from "urijs";
import {fromJS, shallowCopy} from "immutable";

class TypeAhead extends translatable(Component){
  constructor(props){
    super(props);
    this.state = {
      query: "",
      options: fromJS([]),
      orgNames: {}
    };
  }

  updateQuery(query){
    this.setState({query});
    if(query.length >= this.constructor.MIN_QUERY_LENGTH) {
      fetchJson(new URI(this.constructor.endpoint).addSearch('text', query).toString())
          .then(data => this.setState({options: fromJS(data)}));
    } else {
      this.setState({options: fromJS([])})
    }
  }

  /* Marks an option as selected */
  select(option){
    let id = option.get('id');
    let name = option.get('name');
    let orgNames = shallowCopy(this.state.orgNames);
    orgNames[id] = name;
    this.setState({orgNames});
    this.props.onToggle(id);
  }

  renderOption({id, name, checked, cb}){
    return <div className="checkbox" key={id}>
      <label>
        <input
            type="checkbox"
            checked={checked}
            onChange={cb}
        /> {name}
      </label>
    </div>
  }

  render(){
    let {query, options, orgNames} = this.state;
    let {selected, onToggle} = this.props;
    let haveQuery = query.length >= this.constructor.MIN_QUERY_LENGTH;
    return (
        <section className="field type-ahead">
          <header>{this.constructor.getName(this.t.bind(this))} ({selected.count()})</header>
          <section className="options">
            {selected.map(id => this.renderOption({
              id,
              name: orgNames[id],
              checked: true,
              cb: () => onToggle(id)
            }))}

            <input
                type="text"
                className="input-sm form-control search"
                placeholder={this.t('filters:typeAhead:inputPlaceholder')}
                value={query}
                onChange={e => this.updateQuery(e.target.value)}
            />

            {haveQuery && <div className="result-count">{this.t_n('filters:typeAhead:result:sg', 'filters:typeAhead:result:pl', options.count())}</div>}

            {options.filter(option => !selected.has(option.get('id'))).map(option => this.renderOption({
              id: option.get('id'),
              name: option.get('name'),
              checked: false,
              cb: this.select.bind(this, option)
            }))}
          </section>
        </section>
    )
  }
}

TypeAhead.MIN_QUERY_LENGTH = 3;

export default TypeAhead;
