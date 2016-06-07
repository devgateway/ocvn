import Plot from "../plot";
import {pluck} from "../../tools";
import translatable from "../translatable";

export default class FundingByBidType extends translatable(Plot){
  getData(){
    return [{
      x: this.props.data.map(pluck('_id')),
      y: this.props.data.map(pluck('totalTenderAmount')),
      type: 'bar'
    }];
  }

  getLayout(){
    return {
      xaxis: {
        title: this.__("Category"),
        type: "category",
        titlefont: {
          color: "#cc3c3b"
        }
      },
      yaxis: {
        title: this.__("Amount"),
        titlefont: {
          color: "#cc3c3b"
        }
      }
    }
  }
}