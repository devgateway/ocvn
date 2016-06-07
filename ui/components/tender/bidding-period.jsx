import React from "react";
import Plot from "../plot";
import {pluck} from "../../tools";
import translatable from "../translatable";

export default class BiddingPeriod extends translatable(Plot){
  getData(){
    var {data} = this.props;
    var years = data.map(pluck('year'));
    return [{
      x: data.map(pluck('tender')),
      y: years,
      name: this.__("Tender"),
      type: "bar",
      orientation: 'h'
    }, {
      x: data.map(pluck('award')),
      y: years,
      name: this.__("Award"),
      type: "bar",
      orientation: 'h'
    }];
  }

  getLayout(){
    return {
      barmode: "stack",
      xaxis: {
        title: this.__("Days"),
        titlefont: {
          color: "#cc3c3b"
        }
      },
      yaxis: {
        title: this.__("Years"),
        type: "category",
        titlefont: {
          color: "#cc3c3b"
        }
      }
    }
  }
}
