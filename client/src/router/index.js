// === DEFAULT / CUSTOM STYLE ===
// WARNING! always comment out ONE of the two require() calls below.
// 1. use next line to activate CUSTOM STYLE (./src/themes)
// require(`./themes/app.${__THEME}.styl`);
// 2. or, use next line to activate DEFAULT QUASAR STYLE
require(`quasar/dist/quasar.${__THEME}.css`)
// ==============================

import Vue from 'vue'
import VueRouter from 'vue-router'
import Quasar from 'quasar'
import Vuelidate from 'vuelidate'

import routes from './routes'
// import axios from 'configs/axios' -> now in boot/axios.js
// import store from './configs/store'; -> now in store/index.js
// import firebase from 'firebase'


import 'font-awesome/css/font-awesome.css';
import 'highlight/lib/vendor/highlight.js/styles/default.css';
import 'dragula/dist/dragula.css';

Vue.use(Vuelidate);
Vue.use(Quasar); // Install Quasar Framework
// Vue.use(axios);
Vue.use(VueRouter)

/*
 * If not building with SSR mode, you can
 * directly export the Router instantiation;
 *
 * The function below can be async too; either use
 * async/await or return a Promise which resolves
 * with the Router instance.
 */

// copied:
Quasar.start(() => {
  /* eslint-disable no-new */
  new Vue({
    el: '#q-app',
    router,
    store
  });
});

export default function (/* { store, ssrContext } */) {
  const Router = new VueRouter({
    scrollBehavior: () => ({ x: 0, y: 0 }),
    routes,

    // Leave these as they are and change in quasar.conf.js instead!
    // quasar.conf.js -> build -> vueRouterMode
    // quasar.conf.js -> build -> publicPath
    mode: process.env.VUE_ROUTER_MODE,
    base: process.env.VUE_ROUTER_BASE
  })

  return Router
}
