import Plot from "../plot";
import {pluck} from "../../tools";

export default class OverviewPlot extends Plot{
  getTitle() {
    return "Overview chart"
  }

  getData(){
    var {data} = this.props;
    return null == data ? [] : Object.keys(data).map(key => {
      return {
        x: data[key].map(pluck('_id')),
        y: data[key].map(pluck('count')),
        type: 'scatter',
        name: key
      }
    });
  }

  getLayout(){
    return {
      xaxis: {
        title: "Years",
        type: "category",
        titlefont: {
          color: "#cc3c3b"
        }
      },
      yaxis: {
        title: "Count",
        titlefont: {
          color: "#cc3c3b"
        }
      }
    }
  }
}