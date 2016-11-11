import FrontendYearFilterableChart from "../../oce/visualizations/charts/frontend-filterable";
import {pluckImm} from "../../oce/tools";

class PercentEProcurement extends FrontendYearFilterableChart{
    getData(){
        let data = super.getData();
        if(!data) return [];
        return [{
            x: data.map(pluckImm('year')).toArray(),
            y: data.map(pluckImm('percentEgp')).toArray(),
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
                title: this.t('charts:percentEProcurement:xAxisTitle'),
                type: 'category'
            },
            yaxis: {
                title: this.t('charts:percentEProcurement:yAxisTitle'),
                hoverformat: '.2f'
            }
        }
    }
}


PercentEProcurement.endpoint = 'percentTendersUsingEgp';
PercentEProcurement.excelEP = 'percentTendersUsingEgpExcelChart';
PercentEProcurement.getName = t => t('charts:percentEProcurement:title');
PercentEProcurement.getMaxField = imm => imm.get('percentEgp', 0);

export default PercentEProcurement;