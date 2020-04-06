import Vue from 'vue';
import axios from 'axios';
import VueAxios from 'vue-axios';
import JwtService from 'src/common/api/jwt.service';
import { API_QUIZ_URL, API_USER_URL } from 'src/common/api/config';

// Endpoints USER-DB
// =================

// # user management POST just returns what you send ...
// POST /api/users/login replaced by -> GET /api/login
// POST /api/users

// GET /api/user
// PUT /api/user

// # follow management
// GET /api/profiles/:username

// follow / unfollow author of quizzes
// POST /api/profiles/:username/follow
// DELETE  /api/profiles/:username/follow
//
// will return multiple quizzes created by followed users as a feed
// GET /api/quizzes/follow

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
      throw new Error(`ApiUserService ${error}`);
    });
  },

  get(resource, id = '') {
    Vue.axios.defaults.baseURL = API_USER_URL;
    if (id !== '') {
      return Vue.axios.get(`${resource}/${id}`).catch((error) => {
        throw new Error(`ApiUserService ${error}`);
      });
    }
    return Vue.axios.get(`${resource}`).catch((error) => {
      throw new Error(`ApiUserService ${error}`);
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
      throw new Error(`ApiUserService ${error}`);
    });
  },
};

// Endpoints QUIZ-DB
// =================
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
      throw new Error(`ApiQuizService ${error}`);
    });
  },

  get(resource, id = '') {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    if (id !== '') {
      // return Vue.axios.get(`${resource}/${id}`).catch(error => {
      return Vue.axios.get(`${resource}`).catch((error) => {
        throw new Error(`ApiQuizService ${error}`);
      });
    }
    return Vue.axios.get(`${resource}`).catch((error) => {
      throw new Error(`ApiQuizService ${error}`);
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
      throw new Error(`ApiQuizService ${error}`);
    });
  },
};


// # just a tag list
// GET /api/tags
export const TagsService = {
  get() {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    return ApiQuizService.get('tags');
  },
};

// # quizzes management
// // provide tag, author or favorited query parameter to filter results
// GET /api/quizzes -> added /api/favorite
// GET /api/quizzes/:id -> replaced by /api/quiz
// POST /api/quizzes -> replaced by /api/quiz
// PUT /api/quizzes/:id -> replaced by /api/quiz
// DELETE /api/quizzes/:id -> replaced by /api/quiz
export const QuizzesService = {
  query(type, params) {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    // return ApiQuizService.query(`quizzes${type === 'follow' ? '/follow' : ''}`, {
    return ApiQuizService.query(`quizzes${type === 'following' ? '' : ''}`, {
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
    if (id !== null) {
      // hack due to static my-json-server
      return ApiQuizService.delete(`quiz/${id}`);
    }
    return ApiQuizService.delete(`quizzes/${id}`);
  },
};
// # assessment management
// POST /api/quizzes/:id/answers replaced by -> /api/answers
// GET /api/quizzes/:id/answers
// DELETE /api/quizzes/:id/answers/:id
export const AnswersService = {
  get(id) {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    if (typeof id !== 'string') {
      throw new Error('AnswersService.get() quiz id required to fetch answers');
    }
    // return ApiQuizService.get("quizzes", `${id}/answers`);
    return ApiQuizService.get('answers');
  },

  post(id, payload) {
    // return ApiQuizService.post(`quizzes/${id}/answers`, {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    return ApiQuizService.post('answers', {
      answer: { body: payload },
    });
  },

  destroy(id, answerId) {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    return ApiQuizService.delete(`answers/${answerId}`);
  },
};

// # favorite management
// POST /api/quizzes/:id/favorite replaced by -> /api/favorite
// DELETE /api/quizzes/:id/favorite
export const FavoriteService = {
  // add(id) {
  add() {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    // return ApiQuizService.post(`quizzes/${id}/favorite`);
    return ApiQuizService.post('favorite');
  },
  // remove(id) {
  remove() {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    // return ApiQuizService.delete(`quizzes/${id}/favorite`);
    return ApiQuizService.delete('favorite');
  },
};

export default {
  ApiUserService,
  ApiQuizService,
  TagsService,
  QuizzesService,
  AnswersService,
  FavoriteService,
};
