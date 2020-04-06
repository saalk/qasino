import Vue from 'vue';
import DateFilter from './filter/date.filter';
import ErrorFilter from './filter/error.filter';
import LengthFilter from './filter/length.filter';
import CapitalizeFilter from './filter/capitalize.filter';


Vue.config.productionTip = false;
Vue.filter('date', DateFilter);
Vue.filter('error', ErrorFilter);
Vue.filter('trimto', LengthFilter);
Vue.filter('capitalize', CapitalizeFilter);
