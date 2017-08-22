import PropTypes from 'prop-types';
import { getRoute, navigate, onNavigation } from './router';

class OCESwitcher extends React.Component{
  constructor(...args){
    super(...args);
    const view = getRoute()[0] || Object.keys(this.constructor.views)[0];

    this.state = {
      view,
    };

    onNavigation(([view]) => this.setState({view}));
  }

  render() {
    const { translations, styling } = this.props;
    const CurrentView = this.constructor.views[this.state.view];
    return (
      <CurrentView
        onSwitch={navigate}
        translations={translations}
        styling={styling}
      />
    );
  }
}

OCESwitcher.propTypes = {
  translations: PropTypes.object.isRequired,
  styling: PropTypes.object.isRequired,
};

OCESwitcher.views = {};

export default OCESwitcher;
