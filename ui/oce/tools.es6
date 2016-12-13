import URI from "urijs";
/**
 * Returns a function that will invoke `func` property on its argument
 * @param {Function} func
 * @returns {Function}
 */
export var callFunc = funcName => obj => obj[funcName]();

export var pluck = fieldName => obj => obj[fieldName];

export var pluckImm = fieldName => imm => imm.get(fieldName);

export var fetchJson = url => fetch(url, {credentials: 'same-origin'}).then(callFunc('json'))

export function debounce(cb, delay = 200){
  var timeout = null;
  return function(){
    if(null !== timeout) clearTimeout(timeout);
    timeout = setTimeout(cb, delay);
  }
}

export var toK = number => number >= 1000 ? Math.round(number / 1000) + "K" : number;

export var identity = _ => _;

export let response2obj = (field, arr) => arr.reduce((obj, elem) => {
  obj[elem._id] = elem[field];
  return obj;
}, {});

var shallowCompArr = (a, b) => a.every((el, index) => el == b[index]);

export var cacheFn = fn => {
  var lastArgs, lastResult;
  return (...args) => {
    if(!lastArgs || !shallowCompArr(lastArgs, args)){
      lastArgs = args;
      lastResult = fn(...args);
    }
    return lastResult;
  }
};

export let max = (a, b) => a > b ? a : b;

//takes and URI object and makes a POST request to it's base url and query as payload
export const send = url => fetch(url.clone().query(""), {
  method: 'POST',
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded'
  },
  credentials: 'same-origin',
  body: url.query()
});

export let download = ({ep, filters, years, t}) => {
  const url = new URI(`/api/ocds/${ep}`)
      .addSearch(filters.toJS())
      .addSearch('year', years.toArray())
      //this sin shall be atoned for in the future
      .addSearch('language', localStorage.oceLocale);
  return send(url).then(response => {
    let {userAgent} = navigator;
    let isSafari = -1 < userAgent.indexOf("Safari") && -1 == userAgent.indexOf("Chrom");//excludes both Chrome and Chromium
    const isIE = navigator.appName == 'Microsoft Internet Explorer' || !!(navigator.userAgent.match(/Trident/)
        || navigator.userAgent.match(/rv 11/)) || $.browser.msie == 1;
    if (isSafari || isIE) {
      location.href = url;
      return response;
    }
    let [_, filename] = response.headers.get('Content-Disposition').split("filename=");
    response.blob().then(blob => {
      var link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = filename;
      document.body.appendChild(link);
      link.click();
    });
    return response;
  }).catch(() => {
    alert(t('export:error'));
  });
};

export const shallowCopy = original => {
  let copy = {};
  Object.keys(original).forEach(key => copy[key] = original[key]);
  return copy;
};

export const arrReplace = (a, b, [head, ...tail]) => "undefined" == typeof head ?
    tail :
    [a == head ? b : head].concat(arrReplace(a, b, tail));