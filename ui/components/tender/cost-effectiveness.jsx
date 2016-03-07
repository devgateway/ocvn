import Plot from "../plot";

export default class CostEffectiveness extends Plot{
  getTitle(){
    return  "Cost effectiveness";
  }

  getData(){
    var {years, data} = this.props;
    var filteredData = data.filter(({year}) => years.get(+year, false));
    var filteredYears = filteredData.map(datum => datum.year);

    var bidPrice = {
      x: filteredYears,
      y: filteredData.map(datum => datum.tender),
      name: 'Bid price',
      type: 'bar'
    };

    var diff = {
      x: filteredYears,
      y: filteredData.map(datum => datum.diff),
      name: 'Difference',
      type: 'bar'
    };

    return [bidPrice, diff];
  }

  getLayout(){
    return {
      barmode: "stack",
      xaxis:{
        type: "category"
      }
    }
  }
}