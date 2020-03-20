<template>
  <q-page class="quiz">
    <header class="site__header island">
      <QuizHeader v-bind:headerProps='getQuizMetaData()'/>
    </header>
    <hr>
    <main class="site__content island" role="content">
      <br />
      <QuizQuestions
        v-if="quizProgress.subString(0,1)=='q'"
        v-bind:questionsProps='getQuizQuestions()'/>
      <QuizResult
        v-if="quizProgress.subString(0,1)=='r'"
        v-bind:resultProps='getQuizResult()'/>
    </main>
  </q-page>
</template>

<script type="text/javascript">
import { mapState, mapGetter, mapActions } from 'vuex';

import QuizHeader from 'src/pages/quiz/QuizHeader';
import QuizQuestions from 'src/pages/quiz/QuizQuestions';
import QuizResult from 'src/pages/quiz/QuizResult';

// const quizDataFile = `
// {
// "quiz":
// {
// "title": "x quiz",
// "description": "Test your basic product x knowledge",
// "subject": "product x",
// "scope": "Basic knowledge",
// }
// ,
// "questions": [
// {
// "text": "Is product x hosted by a third party outside ING?",
// "category": "true-false",
// "answer": "t",
// "explanation": "Product x is being administrated at a third party located in y"
// },
// {
// "text": "Which picture of avalable options for product x has the beste features?",
// "category": "images",
// "images": [
//   {
//   "src": "https://a.png",
//   "alt": "description of a when not loaded",
//   "value": "a"
//   },
//   {
//   "src": "https://a.png",
//   "alt": "description of b when not loaded",
//   "value": "b"
//   },
//   {
//   "src": "https://a.png",
//   "alt": "description of c when not loaded",
//   "value": "c"
//   }
// ],
// "answer": "c",
// "explanation": "Variant c has in total z features, more then any of the other variants"
// },
// {
// "text": "Which product varaint of product x has the beste features?",
// "category": "multi-choice",
// "choices": [
//   {
//   "label": "Variant a",
//   "value": "a"
//   },
//   {
//   "label": "Variant b",
//   "value": "b"
//   },
//   {
//   "label": "Variant c",
//   "value": "c"
//   }
// ],
// "answer": "c",
// "explanation": "Variant c has in total z features more then the other variants"
// },
// {
// "text": "Decribe the slogan to attract customers for product x,
// you need to mention at least 2 key elements",
// "category": "text-essay",
// "answers": ["easy", "easily", "cheap", "low priced", "robust"],
// "atLeastNeeded": "2",
// "explanation": "Product x, easy and rubust when used and amongst the cheapest on the market"
// },
// {
// "text": "Product x is ___ and ___ when used and amongst the cheapest on the market",
// "category": "fill-the-blanks",
// "answersOrdered": ["easy", "robust"],
// "explanation": "Product x, easy and rubust when used and amongst the cheapest on the market"
// },
// {
// "text": "What is the minimum age to apply for porduct x?",
// "category": "numerical",
// "answer": "18",
// "explanation": "You should be at least 18 to apply but for a partner 16 is the minimum"
// },
// {
// "text": "What are the key elements of porduct x?",
// "category": "multi-answer",
// "choices": [
//   {
//   "label": "Easy to use",
//   "value": "easy"
//   },
//   {
//   "label": "Expensive when compared",
//   "value": "expensive"
//   },
//   {
//   "label": "5 star ratings",
//   "value": "robust"
//   }
// ],
// "answers": ["easy", "robust"],
// "explanation": "Product x, easy and rubust when used and amongst the cheapest on the market"
// }
// ]
// }
// `;

export default {
  name: 'QuizLayout',
  quizResult: {
    quizProgress: 'intro',
    currentQuestion: '0',
    startDateTime: '',
    endDateTime: '',
    totalPauzeTime: '',
    answers: [],
    correct: '0',
    hintsTaken: '0',
  },
  picked: '',
  components: {
    QuizHeader,
    QuizQuestions,
    QuizResult,
  },
  computed: {
    ...mapState(['quiz', 'quizResult']),
    ...mapGetter(['getQuizMetaData', 'getQuizSettings',
      'getQuizQuestions', 'getQuizResult']),
  },
  methods: {
    ...mapActions(['modifyQuizMetaData', 'modifyQuizSettings',
      'modifyQuizQuestions', 'modifyQuizResult']),

    modifyQuizMetaData(quizMetaData) {
      this.modifyQuizMetaDatat(quizMetaData);
    },
    modifyQuizSettings(quizSettings) {
      this.modifyQuizSettings(quizSettings);
    },
    modifyQuizQuestions(quizQuestions) {
      this.modifyQuizQuestions(quizQuestions);
    },
    modifyQuizResult(quizResult) {
      this.modifyQuizResult(quizResult);
    },
  },
  startQuiz() {
    this.currentQuestion += 1;
    this.progressQuizState();
    this.currentQuestion = 1;
    this.choices = [];
    this.picked = '';
    this.correct = 0;
    this.perc = null;
  },
  stopQuiz() {
    this.quizProgress = 'intro';
    this.introStage = true;
    this.questionStage = false;
    this.resultsStage = false;
  },
  previousQuestion() {
    if (this.currentQuestion > 1) {
      this.currentQuestion -= 1;
    } else {
      this.quizProgress = 'question-first';
    }
  },
  processAnswer(p) {
    // console.log('answer event ftw', e);
    this.choices[this.currentQuestion - 1] = p;
    this.progressQuizState();
    if (this.quizProgress === 'question-middle') {
      //
    }
    if (this.currentQuestion === this.questions.length) {
      this.progressQuizState();
      this.handleResults();
    } else {
      this.quizProgress = 'question-middle';
      this.currentQuestion += 1;
    }
  },
  progressQuizState() {
    if (this.questions.length === 1) {
      this.quizProgress = 'question-only';
    } else {
      switch (this.currentQuestion) {
        case 0:
          this.quizProgress = 'intro';
          break;
        case 1:
          this.quizProgress = 'question-first';
          break;
        case this.questions.length - 1:
          this.quizProgress = 'question-last';
          break;
        case this.questions.length:
          this.quizProgress = 'results';
          break;
        default:
          this.quizProgress = 'question-middle';
          break;
      }
    }
  },
  handleResults() {
    this.questions.forEach((a, index) => {
      if (this.choices[index] === a.answer) this.correct += 1;
    });
    this.perc = ((this.correct / this.questions.length) * 100).toFixed(0);
    // eslint-disable-next-line no-console
    // console.log(`${this.correct} ${this.perc}`);
    this.questionStage = false;
    this.resultsStage = true;
    this.introStage = true;
  },
};
</script>

<style scoped>
/*-----------------------------------*\
  $RESET
\*-----------------------------------*/

.quiz {
  overflow-x: hidden;
}

*,
:before,
:after {
  margin: 0;
  padding: 0;
  position: relative;
  box-sizing: border-box;
}

input,
select,
button,
textarea {
  -webkit-appearance: none;
  appearance: none;

  font: inherit;
  color: inherit;
}

/*-----------------------------------*\
  $OBJECTS
\*-----------------------------------*/

.butt,
.input {
  padding: 0.75rem;
  margin: 0.375rem;

  background-color: transparent;
  border-radius: 10px;
}

.butt:focus,
.input:focus {
  outline: none;
}

.butt {
  border: 2px solid #f35626;
  line-height: 1.375;
  padding-left: 1.5rem;
  padding-right: 1.5rem;

  font-weight: 700;
  /* button text */
  color: #ff6600;

  cursor: pointer;
  /* animation: hue 60s infinite linear; */
}

.butt--primary {
  background-color: #ff6600;
  color: #fff;
}

.input {
  border: 1px solid #c0c8c9;
  border-radius: 4px;
}

.input--dropdown {
  background-image: url("~assets/misc/ddown.svg"), none;
  background-repeat: no-repeat;
  background-size: 1.5rem 1rem;
  background-position: right center;
}

/*-----------------------------------*\
  $TYPOGRAPHY
\*-----------------------------------*/
h1,
.alpha {
  margin-bottom: 1.5rem;
  font-size: 2rem;
  font-weight: 100;
  line-height: 1;
  letter-spacing: -0.05em;
}
h2,
.beta {
  margin-bottom: 0.75rem;
  font-weight: 350;
  font-size: 1rem;
  line-height: 1;
}
@media (min-width: 650px) {
  .mega {
    font-size: 6rem;
    line-height: 1;
  }
}
.subhead,
.answer {
  color: #7b8993;
}
.question {
  color: #f35626;
  font-size: 1rem;
}
.promo {
  text-align: center;
}
p,
hr,
form {
  margin-bottom: 1.5rem;
}
hr {
  border: none;
  margin-top: -1px;
  height: 1px;
  background-color: #ff6600;
  background-image: linear-gradient(
    to right,
    #ffffff 0%,
    #c0c8c9 50%,
    #ffffff 100%
  );
}
a {
  color: inherit;
  text-decoration: underline;
  animation: hue 60s infinite linear;
}
a:hover {
  color: #f35626;
}
/*-----------------------------------*\
  $LAYOUT
\*-----------------------------------*/
.wrap {
  max-width: 38rem;
  margin: 0 auto;
}
.island {
  padding: 0.5rem;
}
.isle {
  padding: 0.75rem;
}
.spit {
  padding: 0.375rem;
}
/*-----------------------------------*\
  $BASE
\*-----------------------------------*/
.quiz {
  font: 100%/1.5 "Roboto", Verdana, sans-serif;
  color: #3d464d;
  background-color: #fff;
  -webkit-font-smoothing: antialiased;
  width: 100%;
  overflow: hidden-x;
  /* Centering in The Unknown */
  text-align: center;
}
@media (min-width: 650px) {
  .quiz {
    height: 100%;
    display: inline-block;
    vertical-align: middle;
    max-width: 48rem;
  }
  .quiz:before {
    content: "";
    display: inline-block;
    height: 100%;
    vertical-align: middle;
    margin-right: -0.25em;
  }
  body {
    display: inline-block;
    vertical-align: middle;
    max-width: 38rem;
  }
}
/*-----------------------------------*\
  $HEADER
\*-----------------------------------*/
.site__header {
  animation: bounceInUp 1s;
}
.site__title {
  color: #ff6600;
  background-image: linear-gradient(92deg, #ff6600 0%, #9c27b0 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  /* animation: hue 60s infinite linear; */
}
.site__content {
  animation: bounceInUp 1s;
  animation-delay: 0.1s;
}
.site__content form {
  animation: bounceInUp 1s;
  animation-delay: 0.1s;
}
/*-----------------------------------*\
  $ANIMATIONS
\*-----------------------------------*/
@keyframes hue {
  from {
    filter: hue-rotate(0deg);
  }
  to {
    filter: hue-rotate(-360deg);
  }
}
/*-----------------------------------*\
  $REDUCED MOTION BANNER
\*-----------------------------------*/
.motionless__banner {
  display: none;
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: 1rem;
  background: #333;
  color: #fff;
  border-top: 2px solid #ff5722;
}
.motionless__paragraph {
  margin-bottom: 0;
}
@media (print), (prefers-reduced-motion: reduce) {
  .motionless__banner {
    display: block;
  }
}
</style>
