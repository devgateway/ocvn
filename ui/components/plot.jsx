import Component from "./pure-render-component";
import Plotly from "plotly.js/lib/core";
import translatable from "./translatable";

Plotly.register([
    require('plotly.js/lib/bar')
]);

class Plot extends translatable(Component){
  getDecoratedLayout(){
    var {pageHeaderTitle, title, xAxisRange, yAxisRange} = this.props;
    var layout = this.getLayout();
    if(!pageHeaderTitle) layout.title = title;
    if(xAxisRange) layout.xaxis.range = xAxisRange;
    if(yAxisRange) layout.yaxis.range = yAxisRange;
    return layout;
  }

  componentDidMount(){
    Plotly.newPlot(this.refs.chartContainer, this.getData(), this.getDecoratedLayout());
  }

  componentDidUpdate(prevProps){
    if(prevProps.width != this.props.width){
      Plotly.Plots.resize(this.refs.chartContainer);
    } else {
      //TODO: more efficient and non-redundant updates
      this.refs.chartContainer.data = this.getData();
      Plotly.redraw(this.refs.chartContainer);
    }
    if(['pageHeaderTitle', 'title', 'xAxisRange', 'yAxisRange'].some(prop => prevProps[prop] != this.props[prop])){
      Plotly.relayout(this.refs.chartContainer, this.getDecoratedLayout());
    }
  }

  render(){
    var {pageHeaderTitle, title} = this.props;
    return (
        <section className="chart-section-container">
          {pageHeaderTitle && <h4 className="page-header">{title}</h4>}
          <div ref="chartContainer"></div>
        </section>
    )
  }
}

Plot.defaultProps = {
  pageHeaderTitle: true
}

export default Plot;
