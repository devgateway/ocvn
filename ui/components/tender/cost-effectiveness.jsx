import Plot from "../plot";
import {pluck} from "../../tools";
import translatable from "../translatable";

export default class CostEffectiveness extends translatable(Plot){
  getData(){
    var {data} = this.props;
    var years = data.map(pluck('year'));

    var bidPrice = {
      x: years,
      y: data.map(pluck('tender')),
      name: this.__('Bid price'),
      type: 'bar'
    };

    var diff = {
      x: years,
      y: data.map(pluck('diff')),
      name: this.__('Difference'),
      type: 'bar'
    };

    return [bidPrice, diff];
  }

  getLayout(){
    return {
      barmode: "stack",
      xaxis: {
        title: this.__("Years"),
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