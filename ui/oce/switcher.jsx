import URI from "urijs";

class OCESwitcher extends React.Component{
  constructor(...args){
    super(...args);
		const uri = new URI(location);
		const view = uri.hasQuery('corruption-risk-dashboard') ?
								 'corruptionRiskDashboard' :
								 Object.keys(this.constructor.views)[0];

    this.state={
      view
    }
  }

  render(){
    const {translations, styling} = this.props;
    const CurrentView = this.constructor.views[this.state.view];
    return <CurrentView
               onSwitch={view => this.setState({view})}
               translations={translations}
               styling={styling}
           />;
  }
}

OCESwitcher.views = {};

export default OCESwitcher;
