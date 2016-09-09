import Tab from "../index";
import {Set} from "immutable";
import TenderLocations from "../../visualizations/map/tender-locations";
import cn from "classnames";
import style from "./style.less";

class LocationTab extends Tab{
  static getName(__){
    return __("Location")
  }

  static computeYears(data){
    if(!data) return Set();
    return this.LAYERS.reduce((years, visualization, index) =>
            visualization.computeYears ?
                years.union(visualization.computeYears(data.get(index))) :
                years
        , Set())
  }

  constructor(props){
    super(props);
    this.state = {
      currentLayer: 0,
      dropdownOpen: false,
      switcherPos: {
        top: 0,
        left: 0
      }
    }
  }

  maybeGetSwitcher(){
    let {LAYERS} = this.constructor;
    let {switcherPos} = this.state;
    if(this.constructor.LAYERS.length > 1){
      let {currentLayer, dropdownOpen} = this.state;
      return <div className="layer-switcher" style={switcherPos}>
        <div className={cn("dropdown", {open: dropdownOpen})} onClick={e => this.setState({dropdownOpen: !dropdownOpen})}>
          <button className="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1">
            {LAYERS[currentLayer].getLayerName(this.__.bind(this))} <span className="caret"></span>
          </button>
          <ul className="dropdown-menu">
            {LAYERS.map((layer, index) => <li key={index}>
                  <a href="javascript:void(0)" onClick={e => this.setState({currentLayer: index})}>
                    {LAYERS[index].getLayerName(this.__.bind(this))}
                  </a>
                </li>
            )}
          </ul>
        </div>
      </div>
    }
  }

  componentDidMount(){
    super.componentDidMount();
    let zoom = document.querySelector('.leaflet-control-zoom');
    this.setState({
      switcherPos: {
        top: zoom.offsetTop,
        left: zoom.offsetLeft + zoom.offsetWidth + 10
      }
    })
  }

  render(){
    let {currentLayer} = this.state;
    let {data, requestNewData, translations, filters, years, styling} = this.props;
    let Map = this.constructor.LAYERS[currentLayer];
    return <div className="col-sm-12 content map-content">
      {this.maybeGetSwitcher()}
      <Map
          {...this.props}
          data={data.get(currentLayer)}
          requestNewData={(_, data) => requestNewData([currentLayer], data)}
          translations={translations}
          filters={filters}
          years={years}
          styling={styling}
      />
    </div>
  }
}

LocationTab.icon = "planning";
LocationTab.computeComparisonYears = null;
LocationTab.LAYERS = [TenderLocations];

export default LocationTab;
