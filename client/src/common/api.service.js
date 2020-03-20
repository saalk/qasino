import Vue from 'vue';
import axios from 'axios';
import VueAxios from 'vue-axios';
import JwtService from 'src/common/api/jwt.service';
import { API_QUIZ_URL, API_USER_URL } from 'src/common/api/config';

export const ApiUserService = {
  init() {
    Vue.use(VueAxios, axios);
    // Vue.axios.defaults.baseURL = API_USER_URL;
  },

  setHeader() {
    Vue.axios.defaults.headers.common.Authorization = `Token ${JwtService.getToken()}`;
  },

  query(resource, params) {
    Vue.axios.defaults.baseURL = API_USER_URL;
    return Vue.axios.get(resource, params).catch((error) => {
      throw new Error(`[RWV] ApiUserService ${error}`);
    });
  },

  get(resource, id = '') {
    Vue.axios.defaults.baseURL = API_USER_URL;
    if (id !== '') {
      return Vue.axios.get(`${resource}/${id}`).catch((error) => {
        throw new Error(`[RWV] ApiUserService ${error}`);
      });
    }
    return Vue.axios.get(`${resource}`).catch((error) => {
      throw new Error(`[RWV] ApiUserService ${error}`);
    });
  },

  post(resource, params) {
    Vue.axios.defaults.baseURL = API_USER_URL;
    return Vue.axios.post(`${resource}`, params);
  },

  update(resource, id, params) {
    Vue.axios.defaults.baseURL = API_USER_URL;
    return Vue.axios.put(`${resource}/${id}`, params);
  },

  put(resource, params) {
    Vue.axios.defaults.baseURL = API_USER_URL;
    return Vue.axios.put(`${resource}`, params);
  },

  delete(resource) {
    Vue.axios.defaults.baseURL = API_USER_URL;
    return Vue.axios.delete(resource).catch((error) => {
      throw new Error(`[RWV] ApiUserService ${error}`);
    });
  },
};

export const ApiQuizService = {
  init() {
    Vue.use(VueAxios, axios);
    // Vue.axios.defaults.baseURL = API_QUIZ_URL;
  },

  setHeader() {
    Vue.axios.defaults.headers.common.Authorization = `Token ${JwtService.getToken()}`;
  },

  query(resource, params) {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    return Vue.axios.get(resource, params).catch((error) => {
      throw new Error(`[RWV] ApiQuizService ${error}`);
    });
  },

  get(resource, id = '') {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    if (id !== '') {
      // return Vue.axios.get(`${resource}/${id}`).catch(error => {
      return Vue.axios.get(`${resource}`).catch((error) => {
        throw new Error(`[RWV] ApiQuizService ${error}`);
      });
    }
    return Vue.axios.get(`${resource}`).catch((error) => {
      throw new Error(`[RWV] ApiQuizService ${error}`);
    });
  },

  post(resource, params) {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    return Vue.axios.post(`${resource}`, params);
  },

  update(resource, id, params) {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    return Vue.axios.put(`${resource}/${id}`, params);
  },

  put(resource, params) {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    return Vue.axios.put(`${resource}`, params);
  },

  delete(resource) {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    return Vue.axios.delete(resource).catch((error) => {
      throw new Error(`[RWV] ApiQuizService ${error}`);
    });
  },
};

export const TagsService = {
  get() {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    return ApiQuizService.get('tags');
  },
};

export const QuizzesService = {
  query(type, params) {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    return ApiQuizService.query(`quizzes${type === 'feed' ? '/feed' : ''}`, {
      params,
    });
  },
  get(id) {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    // return ApiQuizService.get("quizzes", id);
    return ApiQuizService.get('quiz', id);
  },
  create(params) {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    return ApiQuizService.post('quizzes', { quiz: params });
  },
  update(id, params) {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    return ApiQuizService.update('quizzes', id, { quiz: params });
  },
  destroy(id) {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    return ApiQuizService.delete(`quizzes/${id}`);
  },
};

export const TestsService = {
  get(id) {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    if (typeof id !== 'string') {
      throw new Error(
        '[RWV] TestsService.get() quiz id required to fetch tests',
      );
    }
    // return ApiQuizService.get("quizzes", `${id}/tests`);
    return ApiQuizService.get('tests');
  },

  post(id, payload) {
    // return ApiQuizService.post(`quizzes/${id}/tests`, {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    return ApiQuizService.post('tests', {
      test: { body: payload },
    });
  },

  destroy(id, testId) {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    return ApiQuizService.delete(`tests/${testId}`);
  },
};

export const FavoriteService = {
  add(id) {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    return ApiQuizService.post(`quizzes/${id}/favorite`);
  },
  remove(id) {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    return ApiQuizService.delete(`quizzes/${id}/favorite`);
  },
};

export default {
  ApiUserService,
  ApiQuizService,
  TagsService,
  QuizzesService,
  TestsService,
  FavoriteService,
};
