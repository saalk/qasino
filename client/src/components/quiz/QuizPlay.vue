<template>
  <q-page padding>
    <q-card class="no-padding no-margin q-pa-md items-start">
      <q-card-section>
      <!-- <header class="site__header island"> -->
      <QuizHeader
      :quiz="this.quiz"
      :quizProgress="this.quizProgress"
      @startQuizParent="startQuiz"
      @stopQuizParent="stopQuiz"
      />
      <!-- </header> -->
      </q-card-section>
    </q-card>
    <q-card class="no-padding no-margin q-pa-md items-start">
      <main class="site__content island" role="content">
      <br />
      <QuizQuestions
        v-if="quizProgress !== 'results' & quizProgress !== 'intro'"
        :quiz="this.quiz"
        :quizProgress="this.quizProgress"
        :result="this.result"
        @previousQuestionParent="previousQuestion"
        @processAnswerParent="processAnswer"
        />
      <QuizResult
        v-else
        :quiz="this.quiz"
        :quizProgress="this.quizProgress"
        :result="this.result"
        />
      </main>
    </q-card>
  </q-page>
</template>

<script>
import { mapGetters } from 'vuex';
// import marked from 'marked';
import store from 'src/store';
// import QuizMeta from 'src/components/quiz/view/QuizMeta';
// import Answer from 'src/components/answer/Answer';
// import AnswerEditor from 'src/components/answer/AnswerEditor';
// import Tag from 'src/components/main/VTag';
// import { FETCH_QUIZ, FETCH_ANSWERS } from 'src/store/types/actions.type';
import QuizHeader from 'src/components/quiz/play/QuizHeader';
import QuizQuestions from 'src/components/quiz/play/QuizQuestions';
import QuizResult from 'src/components/quiz/play/QuizResult';
import {
  FETCH_QUIZ,
  QUIZ_RESET_STATE,
} from 'src/store/types/actions.type';

export default {
  name: 'play-quiz',
  components: {
    // QuizMeta,
    // Answer,
    // AnswerEditor,
    // Tag,
    QuizHeader,
    QuizQuestions,
    QuizResult,
  },
  props: {
    previousQuiz: {
      type: Object,
      required: false,
    },
  },
  async beforeRouteEnter(to, from, next) {
    // SO: https://github.com/vuejs/vue-router/issues/1034
    // If we arrive directly to this url, we need to fetch the quiz
    await store.dispatch(QUIZ_RESET_STATE);
    if (to.params.quizId !== undefined) {
      await store.dispatch(
        FETCH_QUIZ,
        to.params.quizId,
        to.params.previousQuiz,
      );
    }
    return next();
  },
  data() {
    return {
      quizProgress: 'intro',
      result: {
        resultId: 0,
        quizId: 0,
        createdAt: '',
        updatedAt: '',
        hintsTaken: '0',
        // computed
        computed: {
          answeredCount: 0,
          correctCount: 0,
          currentQuestion: 0,
          currentIndex: 0,
          currentPercentToPass: 0,
          passed: false,
        },
        // answers
        answers: [],
        // user
        user: {
          username: '',
        },
      },
      // current answer
      answer: {
        questionId: '',
        answer: '',
        secondsToAnswer: '',
      },
      totalPauzeTime: '',
    };
  },
  computed: {
    ...mapGetters(['currentUser', 'isAuthenticated', 'quiz']),
  },
  methods: {
    startQuiz() {
      this.totalPauzeTime = '';
      // (re)set the results
      this.result.quizId = this.quiz.quizId;
      this.result.createdAt = '';
      // computed
      this.result.computed.answeredCount = 0;
      this.result.computed.correctCount = 0;
      this.result.computed.currentQuestion = 1;
      this.result.computed.currentIndex = 0;
      this.result.computed.currentPercentToPass = 0;
      this.result.computed.passed = false;
      // answer s
      this.result.answers = [];
      // user
      this.result.user.username = this.currentUser.username;
      this.progressQuizState();

      // TODO store start new result for quizId and username
    },
    stopQuiz() {
      this.result.computed.currentIndex = 0;
      this.calculateResults();
      this.quizProgress = 'results';
    },
    previousQuestion() {
      if (this.result.computed.currentQuestion > 1) {
        this.result.computed.currentQuestion -= 1;
        this.result.computed.currentIndex -= 1;
        this.progressQuizState();
      }
    },
    processAnswer(a) {
      // console.log('answer event ftw', e);
      this.answer.questionId = this.quiz.questions[this.result.computed.currentIndex].questionId;
      this.answer.answer = a;
      this.answer.secondsToAnswer = '10';
      this.pushToAnswers(this.answer);
      // TODO store answer on existing quizId and username

      if (this.quizProgress === 'intro'
          || this.quizProgress === 'question-middle') {
        this.result.computed.currentQuestion += 1;
        this.result.computed.currentIndex += 1;
        this.progressQuizState();
      }
      this.calculateResults();
    },
    pushToAnswers(obj) {
      if (this.result.answers.length === 0) {
        this.result.answers.push(obj);
      } else {
        const index = this.result.answers.findIndex((item) => item.questionId === obj.questionId);
        if (index > -1) {
          this.result.answers[index] = obj;
        } else {
          this.result.answers.push(obj);
        }
      }
    },
    progressQuizState() {
      const question = this.result.computed.currentQuestion;
      if (this.quiz.questions.length === 1) {
        this.quizProgress = 'question-only';
      } else {
        switch (question) {
          case 0:
            this.quizProgress = 'intro';
            break;
          case 1:
            this.quizProgress = 'question-first';
            break;
          case this.quiz.questions.length - 1:
            this.quizProgress = 'question-last';
            break;
          case this.quiz.questions.length:
            this.quizProgress = 'results';
            break;
          default:
            this.quizProgress = 'question-middle';
            break;
        }
      }
    },
    calculateResults() {
      this.result.computed.answeredCount = 0;
      this.result.computed.correctCount = 0;
      this.result.computed.currentPercentToPass = 0;

      if (this.result.answers !== null) {
        this.result.computed.answeredCount = this.result.answers.length;
        this.quiz.questions.forEach((a, index) => {
          if (this.result.answers[index] === a.answer) this.result.computed.correctCount += 1;
        });
        this.result.computed
          .currentPercentToPass = (
            (this.result.computed.correctCount / this.quiz.questions.length) * 100
          ).toFixed(0);
      }
      if (this.result.computed.currentPercentToPass < this.quiz.settings.minimumPercentToPass) {
        this.result.computed.passed = false;
      } else {
        this.result.computed.passed = true;
      }
      // TODO store result on existing quizId and username
    },
  },
};
</script>

<style scoped>
/*-----------------------------------*\
  $RESET
\*-----------------------------------*/

/* .quiz {
  overflow-x: hidden;
} */
</style>
