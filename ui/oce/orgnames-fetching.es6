
import URI from "urijs";

const orgNamesFetching = Class => class extends Class{
  constructor(...args){
    super(...args);
    this.state = this.state || {};
    this.state.orgNames = {};
  }

  getOrgName(id){
    return this.state.orgNames[id] || id;
  }

  maybeFetchOrgNames(){
    if(!this.props.data) return;
    const idsWithoutNames = this.props.data.map(pluckImm('id')).flatten().filter(id => !this.state.orgNames[id]).toJS();
    if(!idsWithoutNames.length) return;
    send(new URI('/api/ocds/organization/ids').addSearch('id', idsWithoutNames))
        .then(callFunc('json'))
        .then(orgs => {
          let orgNames = shallowCopy(this.state.orgNames);
          orgs.forEach(({id, name}) => orgNames[id] = name);
          this.setState({orgNames})
        })
  }

  componentDidMount(){
    super.componentDidMount();
    this.maybeFetchOrgNames();
  }

  componentDidUpdate(...args){
    super.componentDidUpdate(...args);
    this.maybeFetchOrgNames();
  }
};

export default orgNamesFetching;