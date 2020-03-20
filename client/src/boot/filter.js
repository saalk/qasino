import Vue from 'vue';
import DateFilter from './filter/date.filter';
import ErrorFilter from './filter/error.filter';

Vue.config.productionTip = false;
Vue.filter('date', DateFilter);
Vue.filter('error', ErrorFilter);
