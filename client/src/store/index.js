import Vue from 'vue';
import Vuex from 'vuex';
import VuexPersist from 'vuex-persist';
import localForage from 'localforage';

// import example from './module-example'

Vue.use(Vuex);

// https://garywoodfine.com/how-to-split-vuex-store-into-modules/
const vuexStorage = new VuexPersist({
  key: process.env.VUE_APP_STORAGE_KEY,
  storage: localForage,
});

/*
 * If not building with SSR mode, you can
 * directly export the Store instantiation;
 *
 * The function below can be async too; either use
 * async/await or return a Promise which resolves
 * with the Store instance.
 */

export default function (/* { ssrContext } */) {
  const Store = new Vuex.Store({
    modules: {
      // example
    },
    plugins: [vuexStorage.plugin],

    // enable strict mode (adds overhead!)
    // for dev mode only
    strict: process.env.DEV,
  });

  return Store;
}
