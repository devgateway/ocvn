let backendFilterable = Class => class extends Class{
  buildUrl(ep){
    return super.buildUrl(ep).addSearch('year', this.props.years.toArray());
  }

  componentDidUpdate(prevProps){
    if(this.props.years != prevProps.years){
      this.fetch()
    } else super.componentDidUpdate(prevProps);
  }
};

export default backendFilterable;