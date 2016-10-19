import Tab from "./index";

class TenderRules extends Tab{
  render(){
    return <h2>tender rules</h2>
  }
}

TenderRules.getName = t => t('filters:tabs:tenderRules:title');

export default TenderRules;