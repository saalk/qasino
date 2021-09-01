// import Vue from 'vue';
import {
  createRouter, createMemoryHistory, createWebHistory, createWebHashHistory,
} from 'vue-router';
import { CHECK_AUTH } from 'src/store/types/actions.type';
import store from 'src/store';
import routes from './routes';

// needed ?
// Vue.use(VueRouter);

/*
 * If not building with SSR mode, you can
 * directly export the Router instantiation;
 *
 * The function below can be async too; either use
 * async/await or return a Promise which resolves
 * with the Router instance.
 */
export default function indexjs(/* { store, ssrContext } */) {
  const createHistory = process.env.SERVER
    ? createMemoryHistory
    : process.env.VUE_ROUTER_MODE === 'history' ? createWebHistory : createWebHashHistory;

  const Router = createRouter({
    scrollBehavior: () => ({ left: 0, top: 0 }),
    routes,

    // Leave this as is and make changes in quasar.conf.js instead!
    // quasar.conf.js -> build -> vueRouterMode
    // quasar.conf.js -> build -> publicPath
    history: createHistory(process.env.MODE === 'ssr' ? undefined : process.env.VUE_ROUTER_BASE),
  });

  // Ensure we checked auth before each page load, check auth uses token!!
  // https://next.router.vuejs.org/guide/advanced/navigation-guards.html#optional-third-argument-next
  Router.beforeEach((to, from, next) => {
    Promise.all([store.dispatch(CHECK_AUTH)]).then(next());
  });

  return Router;
}
