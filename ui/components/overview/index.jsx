import Component from "../pure-render-component";
import OverviewChart from "./overview-chart";
import TendersTable from "./tenders-table";
import AwardsTable from "./awards-table";
import Comparison from "../comparison";
import translatable from "../translatable";

export default class Overview extends translatable(Component){
  render(){
    var {width, state, translations} = this.props;
    var {compare, overview, topTenders, topAwards} = state;
    return (
        <div className="col-sm-12 content">
          {compare ?
              <Comparison
                  translations={translations}
                  width={width}
                  state={overview}
                  Component={OverviewChart}
                  title={this.__("Overview chart")}
              />
          :
              <OverviewChart
                  translations={translations}
                  width={width}
                  data={overview}
                  title={this.__("Overview chart")}
              />
          }
          <TendersTable data={topTenders} translations={translations}/>
          <AwardsTable data={topAwards} translations={translations}/>
        </div>
    );
  }
}