import Component from "../pure-render-component";
import OverviewChart from "./overview-chart";
import TendersTable from "./tenders-table";
import AwardsTable from "./awards-table";
import Comparison from "../comparison";
import translatable from "../translatable";

export default class Overview extends translatable(Component){
  render(){
    var {width, state} = this.props;
    var {compare, overview, topTenders, topAwards} = state;
    return (
        <div className="col-sm-12 content">
          {compare ?
              <Comparison
                  width={width}
                  state={overview}
                  Component={OverviewChart}
                  title={this.__("Overview chart")}
              />
          :
              <OverviewChart
                  width={width}
                  data={overview}
                  title={this.__("Overview chart")}
              />
          }
          <TendersTable data={topTenders}/>
          <AwardsTable data={topAwards}/>
        </div>
    );
  }
}