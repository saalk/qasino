import Vue from 'vue';
import axios from 'axios';
import { Loading } from 'quasar';

// Loading
const loadFunction = config => {
  Loading.show();
  return config;
};
const finishFunction = response => {
  Loading.hide();
  return response;
};
const errorFunction = error => {
  Loading.hide();
  return Promise.reject(error);
};

// axios create
const axiosInstanceJsonPlaceholder = axios.create({ baseURL: 'https://jsonplaceholder.typicode.com/' });
const axiosInstancePunk = axios.create({ baseURL: 'https://api.punkapi.com/v2/' });

axiosInstanceJsonPlaceholder.interceptors.request.use(loadFunction);
axiosInstancePunk.interceptors.request.use(loadFunction);

axiosInstanceJsonPlaceholder.interceptors.response.use(finishFunction, errorFunction);
axiosInstancePunk.interceptors.response.use(finishFunction, errorFunction);

const clients = {
  $http: {
    get() {
      return {
        jsonplaceholder: axiosInstanceJsonPlaceholder,
        punk: axiosInstancePunk,
      };
    },
  },
};

// export default (Vue) => {
//   // ^ ^ ^ this will allow you to use this.$axios
//   //       so you won't necessarily have to import axios in each vue file
//   Object.defineProperties(Vue.prototype, clients);
// };
Vue.prototype.$axios = axios;
Object.defineProperties(Vue.prototype, clients);
