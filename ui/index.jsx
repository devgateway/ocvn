import App from "./components/app";
import React from "react";
import ReactDOM from "react-dom";
import flux from "./flux";
import {debounce} from "./tools";
var TRANSLATIONS = {
  en: require('./languages/en_US.json'),
  vn: require('./languages/vn_VN.json')
};

flux.onUpdate(state =>
  ReactDOM.render(
    <App state={state} actions={flux.actions} translations={TRANSLATIONS[state.getIn(['globalState', 'locale'])]}/>,
    document.getElementById('dg-container')
  )
, true);

window.addEventListener("resize", debounce(function(){
  flux.actions.changeContentWidth(document.querySelector('.years-bar').offsetWidth)
}));

flux.actions.bootstrap();

if("ocvn.developmentgateway.org" == location.hostname){
  (function(b,o,i,l,e,r){b.GoogleAnalyticsObject=l;b[l]||(b[l]=
      function(){(b[l].q=b[l].q||[]).push(arguments)});b[l].l=+new Date;
    e=o.createElement(i);r=o.getElementsByTagName(i)[0];
    e.src='//www.google-analytics.com/analytics.js';
    r.parentNode.insertBefore(e,r)}(window,document,'script','ga'));
  ga('create','UA-78202947-1');ga('send','pageview');
}