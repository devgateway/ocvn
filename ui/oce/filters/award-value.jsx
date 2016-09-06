import Range from "./inputs/range";
import {fetchJson} from "../tools";

class AwardValue extends Range{
    componentDidMount(){
        fetchJson('/api/awardValueInterval').then(([{minAwardValue: min, maxAwardValue: max}]) => this.setState({min, max}))
        
    }
    getTitle(){
        return this.__('Award value');
    }
}

export default AwardValue;
