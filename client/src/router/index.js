import Vue from 'vue';
import VueRouter from 'vue-router';
import { CHECK_AUTH } from 'src/store/types/actions.type';
import store from 'src/store';
import routes from './routes';

Vue.use(VueRouter);

/*
 * If not building with SSR mode, you can
 * directly export the Router instantiation;
 *
 * The function below can be async too; either use
 * async/await or return a Promise which resolves
 * with the Router instance.
 */

// export default function (/* { store, ssrContext } */) {
export default function () {
  const Router = new VueRouter({
    scrollBehavior: () => ({ x: 0, y: 0 }),
    routes,

    // Leave these as they are and change in quasar.conf.js instead!
    // quasar.conf.js -> build -> vueRouterMode
    // quasar.conf.js -> build -> publicPath
    mode: process.env.VUE_ROUTER_MODE,
    base: process.env.VUE_ROUTER_BASE,
  });

  // Ensure we checked auth before each page load, check auth uses token!!
  Router.beforeEach((to, from, next) => Promise.all([store.dispatch(CHECK_AUTH)]).then(next));

  return Router;
}
