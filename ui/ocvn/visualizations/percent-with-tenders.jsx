import FrontendYearFilterableChart from "../../oce/visualizations/charts/frontend-filterable";
import {pluckImm} from "../../oce/tools";

class PercentEProcurement extends FrontendYearFilterableChart{
  static getName(__){
    return __('Percentage of plans with tender');
  }

  getData(){
    let data = super.getData();
    if(!data) return [];
    return [{
      x: data.map(pluckImm('year')).toArray(),
      y: data.map(pluckImm('percentTenders')).toArray(),
      type: 'scatter',
      fill: 'tonexty',
      marker: {
        color: this.props.styling.charts.traceColors[0]
      }
    }];
  }

  getLayout(){
    return {
      xaxis: {
        title: this.__("Year"),
        type: 'category'
      },
      yaxis: {
        title: this.__("Percent"),
        hoverformat: '.2f'
      }
    }
  }
}


PercentEProcurement.endpoint = 'percentTendersWithLinkedProcurementPlan';
PercentEProcurement.excelEP = 'tendersWithLinkedProcurementPlanExcelChart';
PercentEProcurement.getMaxField = imm => imm.get('percentTenders', 0);

export default PercentEProcurement;