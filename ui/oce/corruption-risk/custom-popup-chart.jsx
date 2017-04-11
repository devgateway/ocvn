import Chart from "../visualizations/charts/index";
import ReactIgnore from "../react-ignore.jsx";

class CustomPopupChart extends Chart{
  constructor(...args){
    super(...args);
    this.state = {
      popup: {
        show: false,
        left: 0,
        top: 0
      }
    }
  }

  componentDidMount(){
    super.componentDidMount();
    const {chartContainer} = this.refs;
    chartContainer.on('plotly_hover', this.showPopup.bind(this));
    chartContainer.on('plotly_unhover', data => this.hidePopup());
  }

  showPopup(data){
    const year = data.points[0].x;
    const traceName = data.points[0].data.name;
    const POPUP_WIDTH = 300;
    const POPUP_HEIGHT = 150;
    const POPUP_ARROW_SIZE = 8;
    const hovertext = this.refs.chartContainer.querySelector('.hovertext');
    const {top: targetTop, left: targetLeft} = hovertext.getBoundingClientRect();
    const {top: parentTop, left: parentLeft} = this.refs.chartContainer.getBoundingClientRect();
    this.setState({
      popup: {
        show: true,
        top: targetTop-parentTop-POPUP_HEIGHT,
        left: targetLeft-parentLeft-POPUP_WIDTH/2 - POPUP_ARROW_SIZE/2,
        year,
        traceName
      }
    });
  }

  hidePopup(){
    this.setState({
      popup: {
        show: false
      }
    });
  }

  render(){
    const {loading, popup} = this.state;
    let hasNoData = !loading && this.hasNoData();
    return (
      <div className="chart-container">
    	  {hasNoData && <div className="message">{this.t('charts:general:noData')}</div>}
	      {loading && <div className="message">
  	      Loading...<br/>
    	    <img src="assets/loading-bubbles.svg" alt=""/>
	      </div>}

  	    {popup.show && this.getPopup()}

	      <ReactIgnore>
  	      <div ref="chartContainer"/>
	      </ReactIgnore>
      </div>
    )
  }
}

export default CustomPopupChart;
