/* eslint-disable no-restricted-syntax */
/* eslint-disable guard-for-in */
/* eslint-disable no-shadow */
import Vue from 'vue';
import {
  QuizzesService,
  TestsService,
  FavoriteService,
} from 'src/common/api.service';
import {
  FETCH_QUIZ,
  FETCH_TESTS,
  TEST_CREATE,
  TEST_DESTROY,
  FAVORITE_ADD,
  FAVORITE_REMOVE,
  QUIZ_PUBLISH,
  QUIZ_EDIT,
  QUIZ_EDIT_ADD_TAG,
  QUIZ_EDIT_REMOVE_TAG,
  QUIZ_DELETE,
  QUIZ_RESET_STATE,
} from '../types/actions.type';
import {
  RESET_STATE,
  SET_QUIZ,
  SET_TESTS,
  TAG_ADD,
  TAG_REMOVE,
  UPDATE_QUIZ_IN_LIST,
} from '../types/mutations.type';

const initialState = {
  quiz: {
    author: {},
    title: '',
    description: '',
    body: '',
    tagList: [],
  },
  tests: [],
};

export const state = { ...initialState };

export const actions = {
  async [FETCH_QUIZ](context, quizid, prevQuiz) {
    // avoid extronuous network call if quiz exists
    if (prevQuiz !== undefined) {
      return context.commit(SET_QUIZ, prevQuiz);
    }
    const { data } = await QuizzesService.get(quizid);
    context.commit(SET_QUIZ, data.quiz);
    return data;
  },
  async [FETCH_TESTS](context, quizid) {
    const { data } = await TestsService.get(quizid);
    context.commit(SET_TESTS, data.tests);
    return data.tests;
  },
  async [TEST_CREATE](context, payload) {
    await TestsService.post(payload.id, payload.test);
    context.dispatch(FETCH_TESTS, payload.id);
  },
  async [TEST_DESTROY](context, payload) {
    await TestsService.destroy(payload.id, payload.testId);
    context.dispatch(FETCH_TESTS, payload.id);
  },
  async [FAVORITE_ADD](context, id) {
    const { data } = await FavoriteService.add(id);
    context.commit(UPDATE_QUIZ_IN_LIST, data.quiz, { root: true });
    context.commit(SET_QUIZ, data.quiz);
  },
  async [FAVORITE_REMOVE](context, id) {
    const { data } = await FavoriteService.remove(id);
    // Update list as well. This allows us to favorite an quiz in the Home view.
    context.commit(UPDATE_QUIZ_IN_LIST, data.quiz, { root: true });
    context.commit(SET_QUIZ, data.quiz);
  },
  [QUIZ_PUBLISH]({ state }) {
    return QuizzesService.create(state.quiz);
  },
  [QUIZ_DELETE](context, id) {
    return QuizzesService.destroy(id);
  },
  [QUIZ_EDIT]({ state }) {
    return QuizzesService.update(state.quiz.id, state.quiz);
  },
  [QUIZ_EDIT_ADD_TAG](context, tag) {
    context.commit(TAG_ADD, tag);
  },
  [QUIZ_EDIT_REMOVE_TAG](context, tag) {
    context.commit(TAG_REMOVE, tag);
  },
  [QUIZ_RESET_STATE]({ commit }) {
    commit(RESET_STATE);
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
  [TAG_ADD](state, tag) {
    state.quiz.tagList = state.quiz.tagList.concat([tag]);
  },
  [TAG_REMOVE](state, tag) {
    state.quiz.tagList = state.quiz.tagList.filter((t) => t !== tag);
  },
  [RESET_STATE]() {
    for (const f in state) {
      Vue.set(state, f, initialState[f]);
    }
  },
};

const getters = {
  quiz(state) {
    return state.quiz;
  },
  tests(state) {
    return state.tests;
  },
};

export default {
  state,
  actions,
  mutations,
  getters,
};
