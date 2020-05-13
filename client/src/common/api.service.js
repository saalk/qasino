import Vue from 'vue';
import axios from 'axios';
import VueAxios from 'vue-axios';
import JwtService from 'src/common/api/jwt.service';
import { API_QUIZ_URL, API_ANSWER_URL, API_USER_URL } from 'src/common/api/config';

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

// Endpoints ANWER-DB
// =================
export const ApiAnswerService = {
  init() {
    Vue.use(VueAxios, axios);
    // Vue.axios.defaults.baseURL = API_ANSWER_URL;
  },

  setHeader() {
    Vue.axios.defaults.headers.common.Authorization = `Token ${JwtService.getToken()}`;
  },

  query(resource, params) {
    Vue.axios.defaults.baseURL = API_ANSWER_URL;
    return Vue.axios.get(resource, params).catch((error) => {
      throw new Error(`ApiAnswerService ${error}`);
    });
  },

  get(resource, id = '') {
    Vue.axios.defaults.baseURL = API_ANSWER_URL;
    if (id !== '') {
      // return Vue.axios.get(`${resource}/${id}`).catch(error => {
      return Vue.axios.get(`${resource}`).catch((error) => {
        throw new Error(`ApiAnswerService ${error}`);
      });
    }
    return Vue.axios.get(`${resource}`).catch((error) => {
      throw new Error(`ApiAnswerService ${error}`);
    });
  },

  post(resource, params) {
    Vue.axios.defaults.baseURL = API_ANSWER_URL;
    return Vue.axios.post(`${resource}`, params);
  },

  update(resource, id, params) {
    Vue.axios.defaults.baseURL = API_ANSWER_URL;
    return Vue.axios.put(`${resource}/${id}`, params);
  },

  put(resource, params) {
    Vue.axios.defaults.baseURL = API_ANSWER_URL;
    return Vue.axios.put(`${resource}`, params);
  },

  delete(resource) {
    Vue.axios.defaults.baseURL = API_ANSWER_URL;
    return Vue.axios.delete(resource).catch((error) => {
      throw new Error(`ApiAnswerService ${error}`);
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
// // provide tag, author or favorited query parameter to filter scores
// GET /api/quizzes -> added /api/favorite
// GET /api/quizzes/:id -> replaced by /api/quiz
// POST /api/quizzes -> replaced by /api/quiz
// PUT /api/quizzes/:id -> replaced by /api/quiz
// DELETE /api/quizzes/:id -> replaced by /api/quiz
export const QuizzesService = {
  query(type, params) {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    // return ApiQuizService.query(`quizzes${type === 'follow' ? '/follow' : ''}`, {
    return ApiQuizService.query(`quiz${type === 'following' ? '' : ''}`, {
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
    return ApiQuizService.post('quiz', { quiz: params });
  },
  update(id, params) {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    return ApiQuizService.update('quiz', id, { quiz: params });
  },
  destroy(id) {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    if (id !== null) {
      // hack due to static my-json-server
      return ApiQuizService.delete(`quiz/${id}`);
    }
    return ApiQuizService.delete(`quiz/${id}`);
  },
};
// # assessment management
// POST /api/quizzes/:id/scores replaced by -> /api/scores
// GET /api/quizzes/:id/scores
// DELETE /api/quizzes/:id/scores/:id
export const ScoresService = {
  get(quizid, userid) {
    Vue.axios.defaults.baseURL = API_ANSWER_URL;
    if (quizid === null || userid === null) {
      throw new Error('scoresService.get() quiz + user id required to fetch scores');
    }
    // return ApiQuizService.get("quizzes", `${id}/scores`);
    return ApiAnswerService.get('score');
  },

  post(id, payload) {
    // return ApiQuizService.post(`quizzes/${id}/scores`, {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    return ApiAnswerService.post('answer', {
      score: { body: payload },
    });
  },

  destroy(id, scoreId) {
    Vue.axios.defaults.baseURL = API_QUIZ_URL;
    return ApiAnswerService.delete(`score/${scoreId}`);
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
  ScoresService,
  FavoriteService,
};
