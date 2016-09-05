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

export let download = ({ep, filters, years, __}) => {
  let url = new URI(`/api/ocds/${ep}`).addSearch(filters.toJS()).addSearch('year', years.toArray());
  return fetch(url.clone().query(""), {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    body: url.query()
  }).then(response => {
    let {userAgent} = navigator;
    let isSafari =  -1 < userAgent.indexOf("Safari") && -1 == userAgent.indexOf("Chrom");//excludes both Chrome and Chromium
    if(isSafari){
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
    alert(__("An error occurred during export!"));
  });
}