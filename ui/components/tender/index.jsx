import React from "react";
import Component from "../pure-render-component";
import CostEffectiveness from "./cost-effectiveness";
import BidSelection from "./bid-selection";
import BiddingPeriod from "./bidding-period";
import Cancelled from "./cancelled";
import {toImmutable} from "nuclear-js";
import Comparison from "../comparison";
import translatable from "../translatable";

export default class Tender extends translatable(Component){
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
                  title={this.__("Cost effectiveness")}
              />
          :
              <CostEffectiveness
                title={this.__("Cost effectiveness")}
                data={costEffectiveness}
                width={width}
              />
          }

          {compare ?
              <Comparison
                  width={width}
                  state={bidPeriod}
                  Component={BiddingPeriod}
                  title={this.__("Bid period")}
              />
              :
              <BiddingPeriod
                  title={this.__("Bid period")}
                  data={bidPeriod}
                  width={width}
              />
          }

          {compare ?
              <Comparison
                  width={width}
                  state={bidType}
                  Component={BidSelection}
                  title={this.__("Bid Selection Method")}
              />
              :
              <BidSelection
                  title={this.__("Bid Selection Method")}
                  data={bidType}
                  width={width}
              />
          }

          {compare ?
              <Comparison
                  width={width}
                  state={cancelled}
                  Component={Cancelled}
                  title={this.__("Cancelled funding")}
              />
              :
              <Cancelled
                  title={this.__("Cancelled funding")}
                  data={cancelled}
                  width={width}
              />
          }
        </div>
    );
  }
}