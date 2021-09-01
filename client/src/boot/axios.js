// src/boot/axios.js
import { boot } from 'quasar/wrappers';
import axios from 'axios';
import { API_QUIZ_URL, API_ANSWER_URL, API_USER_URL } from 'src/common/api/config';
import { ID_TOKEN_KEY } from 'src/common/api/api.key';

const apiUser = axios.create(
  {
    baseURL: API_USER_URL,
    timeout: 5000,
    headers: { 'X-Custom-Header': ID_TOKEN_KEY },
  },
);
const apiQuiz = axios.create(
  {
    baseURL: API_QUIZ_URL,
    timeout: 5000,
    headers: { 'X-Custom-Header': ID_TOKEN_KEY },
  },
);
const apiAnswer = axios.create(
  {
    baseURL: API_ANSWER_URL,
    timeout: 5000,
    headers: { 'X-Custom-Header': ID_TOKEN_KEY },
  },
);

export default boot(({ app }) => {
  // for use inside Vue files (Options API) through this.$axios and this.$api

  app.config.globalProperties.$axios = axios;
  // ^ ^ ^ this will allow you to use this.$axios (for Vue Options API form)
  //       so you won't necessarily have to import axios in each vue file

  app.config.globalProperties.$apiUser = apiUser;
  app.config.globalProperties.$apiQuiz = apiQuiz;
  app.config.globalProperties.$apiAnswer = apiAnswer;
  // ^ ^ ^ this will allow you to use this.$apiUser (for Vue Options API form)
  //       so you can easily perform requests against your app's API
});

export {
  axios, apiUser, apiQuiz, apiAnswer,
};
