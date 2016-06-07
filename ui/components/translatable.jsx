var translatable = Class => class Translatable extends Class{
  __(text){
    var translations = this.props.translations || {};
    return translations[text] || text;
  }

  __n(sg, pl, n){
    return n + " " + this.__(1 == n ? sg : pl);
  }
};

export default translatable;