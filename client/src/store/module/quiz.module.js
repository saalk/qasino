/* eslint-disable no-restricted-syntax */
/* eslint-disable guard-for-in */
/* eslint-disable no-shadow */
import Vue from 'vue';
import {
  QuizzesService,
  ResultsService,
  FavoriteService,
} from 'src/common/api.service';
import {
  FETCH_QUIZ,
  FETCH_RESULT,
  ANSWER_CREATE,
  RESULT_DESTROY,
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
  SET_ANSWERS,
  TAG_ADD,
  TAG_REMOVE,
  UPDATE_QUIZ_IN_LIST,
} from '../types/mutations.type';

const initialState = {
  quiz: {
    meta: {
      title: '',
      description: '',
      subject: '',
      audiance: '',
      createdAt: `${(new Date()).getFullYear()}-${(new Date()).getMonth() + 1}-
      ${(new Date()).getDay()}T${(new Date()).getHours()}:${(new Date()).getMinutes()}:
      ${(new Date()).getSeconds()}`,
      updatedAt: '',
      tagList: [],
    },
    author: {},
    settings: {
      final: 't',
      maxSecondsPerQuestion: '5',
      numberOfHints: '2',
      allowExit: 't',
      allowGoBack: 't',
      randomizeQuestions: 'f',
      randomizeAnswers: 'f',
      minimumPercentToPass: '100',
    },
    questions: [],
  },
  result: {
    resultId: 1,
    quizId: 123,
    createdAt: `${(new Date()).getFullYear()}-${(new Date()).getMonth() + 1}-
    ${(new Date()).getDay()}T${(new Date()).getHours()}:${(new Date()).getMinutes()}:
    ${(new Date()).getSeconds()}`,
    updatedAt: '',
    hintsTaken: 0,
    answers: [],
    user: '',
  },
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
  async [FETCH_RESULT](context, quizid, userid) {
    const { data } = await ResultsService.get(quizid, userid);
    context.commit(SET_ANSWERS, data.answers);
    return data.answers;
  },
  async [ANSWER_CREATE](context, payload) {
    await ResultsService.post(payload.id, payload.answer);
    context.dispatch(SET_ANSWERS, payload.id);
  },
  async [RESULT_DESTROY](context, payload) {
    await ResultsService.destroy(payload.id, payload.answerId);
    context.dispatch(RESULT_DESTROY, payload.id);
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
  [SET_ANSWERS](state, answers) {
    state.answers = answers;
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
  answers(state) {
    return state.answers;
  },
};

export default {
  state,
  actions,
  mutations,
  getters,
};
