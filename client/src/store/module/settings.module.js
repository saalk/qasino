/* eslint-disable no-shadow */
import { QuizzesService, TestsService } from 'src/common/api.service';
import { FETCH_QUIZ, FETCH_TESTS } from '../types/actions.type';
import { SET_QUIZ, SET_TESTS } from '../types/mutations.type';

export const state = {
  quiz: {},
  tests: [],
};

export const actions = {
  [FETCH_QUIZ](context, quizid) {
    return QuizzesService.get(quizid)
      .then(({ data }) => {
        context.commit(SET_QUIZ, data.quiz);
      })
      .catch((error) => {
        throw new Error(error);
      });
  },
  [FETCH_TESTS](context, quizid) {
    return TestsService.get(quizid)
      .then(({ data }) => {
        context.commit(SET_TESTS, data.tests);
      })
      .catch((error) => {
        throw new Error(error);
      });
  },
};

/* eslint no-param-reassign: ["error", { "props": false }] */
export const mutations = {
  [SET_QUIZ](state, quiz) {
    state.quiz = quiz;
  },
  [SET_TESTS](state, tests) {
    state.tests = tests;
  },
};

export default {
  state,
  actions,
  mutations,
};
