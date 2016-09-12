import BidPeriod from "../../oce/visualizations/charts/bid-period";
import {pluckImm, response2obj} from "../../oce/tools";

class OCVNBidPeriod extends BidPeriod{
    transform([tenders, awards, avg]){
        let transformed = super.transform([tenders, awards]);
        let avgHash = response2obj('avgTimeFromPlanToTenderPhase', avg);
        return transformed.map(datum => {
            datum.avg = avgHash[datum.year] || 0;
            return datum;
        })
    }

    getData(){
        let data = super.getRawData();
        if(!data) return [];
        return [{
            x: data.map(pluckImm('avg')).toArray(),
            y: data.map(pluckImm('year')).toArray(),
            name: this.__('Average time from plan to tender phase'),
            type: "bar",
            orientation: 'h',
            marker: {
                color: this.props.styling.charts.traceColors[2]
            }
        }].concat(super.getData());
    }
}

OCVNBidPeriod.endpoints = BidPeriod.endpoints.concat('avgTimeFromPlanToTenderPhase');
OCVNBidPeriod.excelEP = null;
OCVNBidPeriod.getFillerDatum = year => BidPeriod.getFillerDatum(year).set('avg', 0);
OCVNBidPeriod.getMaxField = imm => BidPeriod.getMaxField(imm) + imm.get('avg');

export default OCVNBidPeriod;
