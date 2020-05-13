/* eslint-disable no-tabs */
import Vue from 'vue';
import Vuex from 'vuex';

// import example from './module-example'
import home from './module/home.module';
import auth from './module/auth.module';
import quiz from './module/quiz.module';
import profile from './module/profile.module';

Vue.use(Vuex);

// https://garywoodfine.com/how-to-split-vuex-store-into-modules/
// const vuexStorage = new VuexPersist({
//   key: process.env.VUE_APP_STORAGE_KEY,
//   storage: localForage,
// });

export default new Vuex.Store({
  modules: {
    home,
    auth,
    quiz,
    profile,
  },
});

// export default function (/* { ssrContext } */) {
//   const Store = new Vuex.Store({
//     // data is kept in the state of the Vuex data store
//     // Every component in the application is listening to this same state
//     state: {
//       quiz: {},
//       quizMetaData: {
//         title: 'x quiz',
//         description: 'Test your basic product x knowledge',
//         subject: 'product x',
//         level: 'Basic knowledge',
//       },
//       quizSettings: {
//         maxSecondsPerQuestion: '5',
//         numberOfHints: '2',
//         allowExit: 'f',
//         allowGoBack: 'f',
//         randomizeQuestions: 't',
//         randomizeAnswers: 't',
//       },
//       // true-false
//       trueFalseQuestion: {
//         text: 'Is product x hosted by a third party outside ING?',
//         category: 'true-false',
//         answer: 't',
//         explanation: 'Product x is being administrated at a third party located in y',
//       },
//       quizScore: {
//         quizProgress: 'intro',
//         currentQuestion: '0',
//         startDateTime: '',
//         endDateTime: '',
//         totalPauzeTime: '',
//         answers: [],
//         correct: '0',
//         hintsTaken: '0',
//       },
//     },
//     getters: {
//       getQuizMetaData: (state) => state.quiz.metaDate,
//       getQuizSettings: (state) => state.quiz.setttings,
//       getQuizQuestions: (state) => state.quiz.questinos,

//       getQuizScore: (state) => state.quizScore,
//     },
//     mutations: {
//       modifyQuizMetaData(metaData) {
//         this.quiz.metaData = metaData;
//       },
//       modifyQuizSettings(settings) {
//         this.quiz.settings = settings;
//       },
//       modifyQuizQuestions(questions) {
//         this.quiz.questions = questions;
//       },
//       modifyQuizScore(quizScore) {
//         this.quizScore = quizScore;
//       },
//     },
//     actions: {
//       loadQuiz() {
//         this.QuizMetaData = {};
//         //
//       },
//       modifyQuizMetaData(context, quizMetaData) {
//         context.commit('modifyQuizMetaData', quizMetaData);
//       },
//       modifyQuizSettings(context, quizSettings) {
//         context.commit('modifyQuizSettings', quizSettings);
//       },
//       modifyQuizQuestions(context, quizQuestions) {
//         context.commit('modifyQuizQuestions', quizQuestions);
//       },

//       modifyQuizScore(context, quizScore) {
//         context.commit('modifyQuizScore', quizScore);
//       },
//     },
//   });

//   return Store;
// }
