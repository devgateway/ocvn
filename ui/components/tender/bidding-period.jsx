import React from "react";
import Plot from "../plot";
import {pluck} from "../../tools";

export default class BiddingPeriod extends Plot{
  getTitle() {
    return "Bidding period"
  }

  getData(){
    var {data, years} = this.props;
    if(!data) return [];
    var filteredData = data.filter(({year}) => years.get(year, false));
    var filteredYears = filteredData.map(pluck('year'));
    return [{
      x: filteredData.map(pluck('tender')),
      y: filteredYears,
      name: "Tender",
      type: "bar",
      orientation: 'h'
    }, {
      x: filteredData.map(pluck('award')),
      y: filteredYears,
      name: "Award",
      type: "bar",
      orientation: 'h'
    }];
  }

  getLayout(){
    return {
      barmode: "stack",
      xaxis: {
        title: "Days",
        titlefont: {
          color: "#cc3c3b"
        }
      },
      yaxis: {
        title: "Years",
        type: "category",
        titlefont: {
          color: "#cc3c3b"
        }
      }
    }
  }
}
