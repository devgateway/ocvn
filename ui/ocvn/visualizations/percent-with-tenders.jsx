import FrontendYearFilterableChart from "../../oce/visualizations/charts/frontend-filterable";
import {pluckImm} from "../../oce/tools";

class PercentWithTenders extends FrontendYearFilterableChart{
  static getName(t){return t('charts:percentWithTenders:title')}

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
        title: this.t('charts:percentWithTenders:xAxisTitle'),
        type: 'category'
      },
      yaxis: {
        title: this.t('charts:percentWithTenders:yAxisTitle'),
        hoverformat: '.2f'
      }
    }
  }
}


PercentWithTenders.endpoint = 'percentTendersWithLinkedProcurementPlan';
PercentWithTenders.excelEP = 'tendersWithLinkedProcurementPlanExcelChart';
PercentWithTenders.getMaxField = imm => imm.get('percentTenders', 0);

export default PercentWithTenders;