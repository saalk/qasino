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
        v-if="quizProgress !== 'scores' & quizProgress !== 'intro'"
        :quiz="this.quiz"
        :quizProgress="this.quizProgress"
        :score="this.score"
        @previousQuestionParent="previousQuestion"
        @processAnswerParent="processAnswer"
        />
      <QuizScore
        v-else
        :quiz="this.quiz"
        :quizProgress="this.quizProgress"
        :score="this.score"
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
import QuizScore from 'src/components/quiz/play/QuizScore';
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
    QuizScore,
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
      score: {
        scoreId: 0,
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
      // (re)set the scores
      this.score.quizId = this.quiz.quizId;
      this.score.createdAt = '';
      // computed
      this.score.computed.answeredCount = 0;
      this.score.computed.correctCount = 0;
      this.score.computed.currentQuestion = 1;
      this.score.computed.currentIndex = 0;
      this.score.computed.currentPercentToPass = 0;
      this.score.computed.passed = false;
      // answer s
      this.score.answers = [];
      // user
      this.score.user.username = this.currentUser.username;
      this.progressQuizState();

      // TODO store start new score for quizId and username
    },
    stopQuiz() {
      this.score.computed.currentIndex = 0;
      this.calculateScores();
      this.quizProgress = 'scores';
    },
    previousQuestion() {
      if (this.score.computed.currentQuestion > 1) {
        this.score.computed.currentQuestion -= 1;
        this.score.computed.currentIndex -= 1;
        this.progressQuizState();
      }
    },
    processAnswer(a) {
      // console.log('answer event ftw', e);
      this.answer.questionId = this.quiz.questions[this.score.computed.currentIndex].questionId;
      this.answer.answer = a;
      this.answer.secondsToAnswer = '10';
      this.pushToAnswers(this.answer);
      // TODO store answer on existing quizId and username

      if (this.quizProgress === 'intro'
          || this.quizProgress === 'question-first'
          || this.quizProgress === 'question-middle') {
        this.score.computed.currentQuestion += 1;
        this.score.computed.currentIndex += 1;
        this.progressQuizState();
      }
      this.calculateScores();
    },
    pushToAnswers(obj) {
      if (this.score.answers.length === 0) {
        this.score.answers.push(obj);
      } else {
        const index = this.score.answers.findIndex((item) => item.questionId === obj.questionId);
        if (index > -1) {
          this.score.answers[index] = obj;
        } else {
          this.score.answers.push(obj);
        }
      }
    },
    progressQuizState() {
      if (this.quiz.questions.length === 1) {
        this.quizProgress = 'question-only';
      } else {
        switch (this.score.computed.currentQuestion) {
          case 0:
            this.quizProgress = 'intro';
            break;
          case this.quiz.questions.length:
            this.quizProgress = 'question-last';
            break;
          case 1:
            this.quizProgress = 'question-first';
            break;
          default:
            this.quizProgress = 'question-middle';
            break;
        }
      }
    },
    calculateScores() {
      this.score.computed.answeredCount = 0;
      this.score.computed.correctCount = 0;
      this.score.computed.currentPercentToPass = 0;

      if (this.score.answers !== null) {
        this.score.computed.answeredCount = this.score.answers.length;
        this.quiz.questions.forEach((a, index) => {
          if (this.score.answers[index] === a.answer) this.score.computed.correctCount += 1;
        });
        this.score.computed
          .currentPercentToPass = (
            (this.score.computed.correctCount / this.quiz.questions.length) * 100
          ).toFixed(0);
      }
      if (this.score.computed.currentPercentToPass < this.quiz.settings.minimumPercentToPass) {
        this.score.computed.passed = false;
      } else {
        this.score.computed.passed = true;
      }
      // TODO store score on existing quizId and username
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
