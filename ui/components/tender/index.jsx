import React from "react";
import Component from "../pure-render-component";
import CostEffectiveness from "./cost-effectiveness";
import BidSelection from "./bid-selection";
import BiddingPeriod from "./bidding-period";
import Cancelled from "./cancelled";
import {toImmutable} from "nuclear-js";
import Comparison from "../comparison";
import {pluck} from "../../tools";

export default class Tender extends Component{
  render(){
    var {state, width} = this.props;
    var {compare, costEffectiveness, bidPeriod, bidType, cancelled} = state;
    return (
        <div className="col-sm-12 content">
          {compare ?
              <Comparison
                  width={width}
                  state={costEffectiveness}
                  Component={CostEffectiveness}
                  title="Cost effectiveness"
              />
          :
              <CostEffectiveness
                title="Cost effectiveness"
                data={costEffectiveness}
                width={width}
              />
          }

          {compare ?
              <Comparison
                  width={width}
                  state={bidPeriod}
                  Component={BiddingPeriod}
                  title="Bid period"
              />
              :
              <BiddingPeriod
                  title="Bid period"
                  data={bidPeriod}
                  width={width}
              />
          }

          {compare ?
              <Comparison
                  width={width}
                  state={bidType}
                  Component={BidSelection}
                  title="Bid Selection Method"
              />
              :
              <BidSelection
                  title="Bid Selection Method"
                  data={bidType}
                  width={width}
              />
          }

          {compare ?
              <Comparison
                  width={width}
                  state={cancelled}
                  Component={Cancelled}
                  title="Cancelled funding"
              />
              :
              <Cancelled
                  title="Cancelled funding"
                  data={cancelled}
                  width={width}
              />
          }
        </div>
    );
  }
}