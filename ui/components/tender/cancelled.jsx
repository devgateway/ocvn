import React from "react";
import Plot from "../plot";
import {pluck} from "../../tools";

export default class BiddingPeriod extends Plot{
  getTitle() {
    return "Cancelled funding"
  }

  getData(){
    var {years, data} = this.props;
    if(!data) return [];
    var filteredData = data.filter(({_id}) => years.get(_id, false));
    return [{
      x: filteredData.map(pluck('_id')),
      y: filteredData.map(pluck('totalCancelledTendersAmount')),
      type: 'scatter',
      fill: 'tonexty'
    }];
  }

  getLayout(){
    return {
      xaxis: {
        title: "Years",
        type: 'category',
        titlefont: {
          color: "#cc3c3b"
        }
      },
      yaxis: {
        title: "Amount",
        titlefont: {
          color: "#cc3c3b"
        }
      }
    }
  }
}
