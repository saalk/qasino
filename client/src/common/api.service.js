// old: import Vue from 'vue';
// vue 3 new:
import { createApp } from 'vue';
import App from 'src/App.vue';

import axios from 'axios';
import VueAxios from 'vue-axios';
import JwtService from 'src/common/api/jwt.service';
import { API_QUIZ_URL, API_ANSWER_URL, API_USER_URL } from 'src/common/api/config';

// vue 3 new:
createApp(App).use(VueAxios, axios).mount('#app');

// todo: return GET this.$api_user + resource + params
// import { api_user } from 'boot/axios'

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
    // old:
    // Vue.use(VueAxios, axios);
    // this.$axios.defaults.baseURL = API_USER_URL;
  },

  setHeader() {
    this.$axios.defaults.headers.common.Authorization = `Token ${JwtService.getToken()}`;
  },

  query(resource, params) {
    this.$axios.defaults.baseURL = API_USER_URL;
    // todo: return GET this.$api_user + resource + params
    // return api_user.get(resource, params).catch((error) => {
    //   throw new Error(`ApiUserService ${error}`);
    // });
    return this.$axios.get(resource, params).catch((error) => {
      throw new Error(`ApiUserService ${error}`);
    });
  },

  get(resource, id = '') {
    this.$axios.defaults.baseURL = API_USER_URL;
    if (id !== '') {
      return this.$axios.get(`${resource}/${id}`).catch((error) => {
        throw new Error(`ApiUserService ${error}`);
      });
    }
    return this.$axios.get(`${resource}`).catch((error) => {
      throw new Error(`ApiUserService ${error}`);
    });
  },

  post(resource, params) {
    this.$axios.defaults.baseURL = API_USER_URL;
    return this.$axios.post(`${resource}`, params);
  },

  update(resource, id, params) {
    this.$axios.defaults.baseURL = API_USER_URL;
    return this.$axios.put(`${resource}/${id}`, params);
  },

  put(resource, params) {
    this.$axios.defaults.baseURL = API_USER_URL;
    return this.$axios.put(`${resource}`, params);
  },

  delete(resource) {
    this.$axios.defaults.baseURL = API_USER_URL;
    return this.$axios.delete(resource).catch((error) => {
      throw new Error(`ApiUserService ${error}`);
    });
  },
};

// Endpoints QUIZ-DB
// =================
export const ApiQuizService = {
  init() {
    // old:
    // Vue.use(VueAxios, axios);
    // this.$axios.defaults.baseURL = API_QUIZ_URL;
  },

  setHeader() {
    this.$axios.defaults.headers.common.Authorization = `Token ${JwtService.getToken()}`;
  },

  query(resource, params) {
    this.$axios.defaults.baseURL = API_QUIZ_URL;
    return this.$axios.get(resource, params).catch((error) => {
      throw new Error(`ApiQuizService ${error}`);
    });
  },

  get(resource, id = '') {
    this.$axios.defaults.baseURL = API_QUIZ_URL;
    if (id !== '') {
      return this.$axios.get(`${resource}/${id}`).catch((error) => {
      // return this.$axios.get(`${resource}`).catch((error) => {
        throw new Error(`ApiQuizService ${error}`);
      });
    }
    return this.$axios.get(`${resource}`).catch((error) => {
      throw new Error(`ApiQuizService ${error}`);
    });
  },

  post(resource, params) {
    this.$axios.defaults.baseURL = API_QUIZ_URL;
    return this.$axios.post(`${resource}`, params);
  },

  update(resource, id, params) {
    this.$axios.defaults.baseURL = API_QUIZ_URL;
    return this.$axios.put(`${resource}/${id}`, params);
  },

  put(resource, params) {
    this.$axios.defaults.baseURL = API_QUIZ_URL;
    return this.$axios.put(`${resource}`, params);
  },

  delete(resource) {
    this.$axios.defaults.baseURL = API_QUIZ_URL;
    return this.$axios.delete(resource).catch((error) => {
      throw new Error(`ApiQuizService ${error}`);
    });
  },
};

// Endpoints ANWER-DB
// =================
export const ApiAnswerService = {
  init() {
    // old:
    // Vue.use(VueAxios, axios);
    // this.$axios.defaults.baseURL = API_ANSWER_URL;
  },

  setHeader() {
    this.$axios.defaults.headers.common.Authorization = `Token ${JwtService.getToken()}`;
  },

  query(resource, params) {
    this.$axios.defaults.baseURL = API_ANSWER_URL;
    return this.$axios.get(resource, params).catch((error) => {
      throw new Error(`ApiAnswerService ${error}`);
    });
  },

  get(resource, id = '') {
    this.$axios.defaults.baseURL = API_ANSWER_URL;
    if (id !== '') {
      // return this.$axios.get(`${resource}/${id}`).catch(error => {
      return this.$axios.get(`${resource}`).catch((error) => {
        throw new Error(`ApiAnswerService ${error}`);
      });
    }
    return this.$axios.get(`${resource}`).catch((error) => {
      throw new Error(`ApiAnswerService ${error}`);
    });
  },

  post(resource, params) {
    this.$axios.defaults.baseURL = API_ANSWER_URL;
    return this.$axios.post(`${resource}`, params);
  },

  update(resource, id, params) {
    this.$axios.defaults.baseURL = API_ANSWER_URL;
    return this.$axios.put(`${resource}/${id}`, params);
  },

  put(resource, params) {
    this.$axios.defaults.baseURL = API_ANSWER_URL;
    return this.$axios.put(`${resource}`, params);
  },

  delete(resource) {
    this.$axios.defaults.baseURL = API_ANSWER_URL;
    return this.$axios.delete(resource).catch((error) => {
      throw new Error(`ApiAnswerService ${error}`);
    });
  },
};

// # just a tag list
// GET /api/tags
export const TagsService = {
  get() {
    this.$axios.defaults.baseURL = API_QUIZ_URL;
    return ApiQuizService.get('tags');
  },
};

// # quizzes management
// // provide tag, author or favorited query parameter to filter scores
// GET /api/quizzes
// GET /api/quizzes/:id
// POST /api/quizzes
// PUT /api/quizzes/:id
// DELETE /api/quizzes/:id
export const QuizzesService = {
  query(type, params) {
    this.$axios.defaults.baseURL = API_QUIZ_URL;
    // return ApiQuizService.query(`quizzes${type === 'follow' ? '/follow' : ''}`, {
    return ApiQuizService.query(`quizzes${type === 'following' ? '' : ''}`, {
      params,
    });
  },
  get(id) {
    this.$axios.defaults.baseURL = API_QUIZ_URL;
    // return ApiQuizService.get("quizzes", id);
    return ApiQuizService.get('quizzes', id);
  },
  create(params) {
    this.$axios.defaults.baseURL = API_QUIZ_URL;
    return ApiQuizService.post('quizzes', { quiz: params });
  },
  update(id, params) {
    this.$axios.defaults.baseURL = API_QUIZ_URL;
    return ApiQuizService.update('quizzes', id, { quiz: params });
  },
  destroy(id) {
    this.$axios.defaults.baseURL = API_QUIZ_URL;
    if (id !== null) {
      // hack due to static my-json-server
      return ApiQuizService.delete(`quizzes/${id}`);
    }
    return ApiQuizService.delete(`quizzes/${id}`);
  },
};
// # assessment management
// POST /api/quizzes/:id/scores replaced by -> /api/scores
// GET /api/quizzes/:id/scores
// DELETE /api/quizzes/:id/scores/:id
export const ScoresService = {
  get(quizid, userid) {
    this.$axios.defaults.baseURL = API_ANSWER_URL;
    if (quizid === null || userid === null) {
      throw new Error('scoresService.get() quiz + user id required to fetch scores');
    }
    // return ApiQuizService.get("quizzes", `${id}/scores`);
    return ApiAnswerService.get('scores');
  },

  post(id, payload) {
    // return ApiQuizService.post(`quizzes/${id}/scores`, {
    this.$axios.defaults.baseURL = API_QUIZ_URL;
    return ApiAnswerService.post('answers', {
      score: { body: payload },
    });
  },

  destroy(id, scoreId) {
    this.$axios.defaults.baseURL = API_QUIZ_URL;
    return ApiAnswerService.delete(`scores/${scoreId}`);
  },
};

// # favorite management
// POST /api/quizzes/:id/favorite replaced by -> /api/favorite
// DELETE /api/quizzes/:id/favorite
export const FavoriteService = {
  // add(id) {
  add() {
    this.$axios.defaults.baseURL = API_QUIZ_URL;
    return ApiQuizService.post('quizzes/favorite');
    // return ApiQuizService.post(`quizzes/${id}/favorite`);
    // return ApiQuizService.post('favorite');
  },
  // remove(id) {
  remove() {
    this.$axios.defaults.baseURL = API_QUIZ_URL;
    // return ApiQuizService.delete(`quizzes/${id}/favorite`);
    return ApiQuizService.delete('favorite');
  },
};

export default {
  ApiUserService,
  ApiQuizService,
  TagsService,
  QuizzesService,
  ScoresService,
  FavoriteService,
};
