import translatable from "../../translatable";
import Component from "../../pure-render-component";
import {fromJS, Set} from "immutable";
import {fetchJson} from "../../tools";

class MultipleSelect extends translatable(Component){
  constructor(props){
    super(props);
    this.state = {
      options: fromJS([])
    }
  }

  getOptions(){
    return this.state.options;
  }

  getSelectedCount(){
    return this.getOptions().filter((option, key) => this.props.selected.has(this.getId(option, key))).count();
  }

  transform(datum){
    return datum;
  }

  componentDidMount(){
    let {ENDPOINT} = this.constructor;
    if(ENDPOINT) fetchJson(`/api/${ENDPOINT}`).then(data => this.setState({options: fromJS(this.transform(data))}));
  }

  selectAll(){
    this.props.onUpdateAll(
        Set(
            this.getOptions().map(this.getId).toArray()
        )
    );
  }

  selectNone(){
    this.props.onUpdateAll(Set());
  }

  render(){
    let options = this.getOptions();
    let {selected, onToggle} = this.props;
    let totalOptions = options.count();
    return (
        <section className="field">
          <header>
            {this.getTitle()} <span className="count">({this.getSelectedCount()}/{totalOptions})</span>
            <div className="pull-right">
              <a href="javascript:void(0)" onClick={e => this.selectAll()}>
                {this.t('filters:multipleSelect:selectAll')}
              </a>
              &nbsp;|&nbsp;
              <a href="javascript:void(0)" onClick={e => this.selectNone()}>
                {this.t('filters:multipleSelect:selectNone')}
              </a>
            </div>
          </header>
          <section className="options">
            {options.map((option, key) => (
                <div className="checkbox" key={this.getId(option, key)}>
                  <label>
                    <input
                        type="checkbox"
                        checked={selected.has(this.getId(option, key))}
                        onChange={e => onToggle(this.getId(option, key))}
                    /> {this.getLabel(option)}
                  </label>
                </div>
            )).toArray()}
          </section>
        </section>
    )
  }
}

export default MultipleSelect;