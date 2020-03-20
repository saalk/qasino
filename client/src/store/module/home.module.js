/* eslint-disable no-shadow */
import { TagsService, QuizzesService } from 'src/common/api.service';
import { FETCH_QUIZS, FETCH_TAGS } from '../types/actions.type';
import {
  FETCH_START,
  FETCH_END,
  SET_TAGS,
  UPDATE_QUIZ_IN_LIST,
} from '../types/mutations.type';

const state = {
  tags: [],
  quizzes: [],
  isLoading: true,
  // quizzesCount: 0
  quizzesCount: 2,
};

const getters = {
  quizzesCount(state) {
    return state.quizzesCount;
  },
  quizzes(state) {
    return state.quizzes;
  },
  isLoading(state) {
    return state.isLoading;
  },
  tags(state) {
    return state.tags;
  },
};

const actions = {
  [FETCH_QUIZS]({ commit }, params) {
    commit(FETCH_START);
    return QuizzesService.query(params.type, params.filters)
      .then(({ data }) => {
        commit(FETCH_END, data);
      })
      .catch((error) => {
        throw new Error(error);
      });
  },
  [FETCH_TAGS]({ commit }) {
    return TagsService.get()
      .then(({ data }) => {
        commit(SET_TAGS, data.tags);
      })
      .catch((error) => {
        throw new Error(error);
      });
  },
};

/* eslint no-param-reassign: ["error", { "props": false }] */
const mutations = {
  [FETCH_START](state) {
    state.isLoading = true;
  },
  // [FETCH_END](state, { quizzes, quizzesCount }) {
  [FETCH_END](state, { quizzes, quizzesCount = 2 }) {
    state.quizzes = quizzes;
    state.quizzesCount = quizzesCount;
    state.isLoading = false;
  },
  [SET_TAGS](state, tags) {
    state.tags = tags;
  },
  [UPDATE_QUIZ_IN_LIST](state, data) {
    state.quizzes = state.quizzes.map((quiz) => {
      if (quiz.id !== data.id) {
        return quiz;
      }
      // We could just return data, but it seems dangerous to
      // mix the results of different api calls, so we
      // protect ourselves by copying the information.
      quiz.favorited = data.favorited;
      quiz.favoritesCount = data.favoritesCount;
      return quiz;
    });
  },
};

export default {
  state,
  getters,
  actions,
  mutations,
};
