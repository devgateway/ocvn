import Component from "./pure-render-component";

export default class Comparison extends Component{
  render(){
    var {state, Component, title, width, translations} = this.props;
    var {data, yAxisRange, xAxisRange, criteriaNames} = state;
    return (
        <div>
          <h3 className="page-header">{title}</h3>
          <div className="row">
            {data.map((datum, index) => (
                <div className="col-md-6">
                  <Component
                      key={index}
                      yAxisRange={yAxisRange}
                      xAxisRange={xAxisRange}
                      data={datum}
                      translations={translations}
                      pageHeaderTitle={false}
                      title={criteriaNames[index] || "Other"}
                      width={width}
                  />
                </div>
            ))}
          </div>
        </div>
    )
  }
}