// for web
   import Vue from 'vue'
// for mobile and native scripts
   // import Vue from 'nativescript-vue'
import './plugins/vuetify'
import App from './App'
import { store } from './store'
import router from './router'
import axios from 'axios'
import 'material-design-icons-iconfont/dist/material-design-icons.css'

Vue.config.ignoredElements = [/^ion-/]
Vue.config.productionTip = false
axios.defaults.baseURL = 'http://www.omdbapi.com/?apikey=b76b385c&page=1&type=movie&Content-Type=application/json'

import VueLogger from 'vuejs-logger';

const options = {
  isEnabled: true,
  logLevel : 'debug',
  stringifyArguments : false,
  showLogLevel : true,
  showMethodName : false,
  separator: '|',
  showConsoleColors: true
};

Vue.use(VueLogger, options);

new Vue({
  render: h => h(App),
  // render: h => h('frame', [h(App)]),
  store,
  router
   }).$mount('#app')
 // }).$start()

/* eslint-disable no-new + older:
new Vue({
  el: '#app',
  router,  // <-- add this line
  template: '<App/>',
  components: { App }
})
*/

