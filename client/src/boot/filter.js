// import Vue from 'vue';
import { createApp } from 'vue';
import App from 'src/App.vue';
// import DateFilter from './filter/date.filter';
// import ErrorFilter from './filter/error.filter';
// import LengthFilter from './filter/length.filter';
// import CapitalizeFilter from './filter/capitalize.filter';

import format from 'date-fns/format';

const app = createApp(App);

app.config.globalProperties.$filters = {
  currencyEUR(value) {
    return `€${value}`;
  },
  capitalizeFilter(s) {
    if (typeof s !== 'string') return '';
    return s.charAt(0).toUpperCase() + s.slice(1);
  },
  date(date) {
    format(new Date(date), 'MMMM D, YYYY');
  },
  error(errorValue) {
    return `${errorValue[0]}`;
  },
  length(value, size) {
    if (!value) return '';
    value = value.toString();
    if (value.length <= size) {
      return value;
    }
    return `${value.substr(0, size)}...`;
  },
};
// Vue.filter('date', DateFilter);
// Vue.filter('error', ErrorFilter);
// Vue.filter('trimto', LengthFilter);
// Vue.filter('capitalize', CapitalizeFilter);
