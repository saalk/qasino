/* eslint-disable no-shadow */
import { QuizzesService, AnswersService } from 'src/common/api.service';
import { FETCH_QUIZ, FETCH_ANSWERS } from '../types/actions.type';
import { SET_QUIZ, SET_ANSWERS } from '../types/mutations.type';

export const state = {
  quiz: {},
  answers: [],
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
  [FETCH_ANSWERS](context, quizid) {
    return AnswersService.get(quizid)
      .then(({ data }) => {
        context.commit(SET_ANSWERS, data.answers);
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
  [SET_ANSWERS](state, answers) {
    state.answers = answers;
  },
};

export default {
  state,
  actions,
  mutations,
};
