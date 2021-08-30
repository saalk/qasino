// src/boot/axios.js
import { boot } from 'quasar/wrappers';
import axios from 'axios';
import { API_QUIZ_URL, API_ANSWER_URL, API_USER_URL } from 'src/common/api/config';

const api_user = axios.create({ baseURL: API_USER_URL });
const api_quiz = axios.create({ baseURL: API_QUIZ_URL });
const api_answer = axios.create({ baseURL: API_ANSWER_URL });

export default boot(({ app }) => {
  // for use inside Vue files (Options API) through this.$axios and this.$api

  app.config.globalProperties.$axios = axios;
  // ^ ^ ^ this will allow you to use this.$axios (for Vue Options API form)
  //       so you won't necessarily have to import axios in each vue file

  app.config.globalProperties.$api_user = api_user;
  app.config.globalProperties.$api_quiz = api_quiz;
  app.config.globalProperties.$api_answer = api_answer;
  // ^ ^ ^ this will allow you to use this.$api_user (for Vue Options API form)
  //       so you can easily perform requests against your app's API
});

export {
  axios, api_user, api_quiz, api_answer,
};
