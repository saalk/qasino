<template>
  <q-page class="quiz">
    <header class="site__header island">
      <div class="wrap">
        <span id="animationSandbox" style="display: block;" class="">
          <h1 class="site__title mega">{{ title }}</h1>
        </span>
        <span class="beta subhead"
          >{{ description }} - {{ questions.length }} questions</span
        >
      </div>
    </header>
    <main class="site__content island" role="content">
      <div class="wrap">
        <div v-if="introStage">
          <form>
            <!-- <select class="input input--dropdown js--animations">
              <optgroup label="Creditcards">
                <option value="flip">cc-basic</option>
                <option value="flip">cc-expert</option>
              </optgroup>
              <optgroup label="Debitcards">
                <option value="flip">db-basic</option>
                <option value="flip">db-expert</option>
              </optgroup>
            </select> -->
            <button class="butt js--triggerAnimation" @click="startQuiz()">
              Start quiz
            </button>
          </form>
        </div>
        <hr />
        <br />
        <div>
          <div v-if="questionStage">
            <strong class="beta subhead">Question {{ currentQuestion }}:</strong>
            <br />
            <!-- form start -->
            <!-- <q-form class="question" @submit="onSubmit" @reset="onReset"> -->
              <div class="question">
                {{ questions[currentQuestion - 1].text }}
              </div>
              <div v-if="questions[currentQuestion - 1].type === 'tf'">
              <br />
                <q-radio v-model="picked" val="t" label="True" />
                <q-radio v-model="picked" val="f" label="False" />
                <!-- <input
                  type="radio"
                  name="currentQuestion1"
                  id="trueAnswer"
                  v-model="picked"
                  selected value="t"
                />
                <br />
                <label class="answer" for="trueAnswer">True</label>
                <input
                  type="radio"
                  name="currentQuestion1"
                  id="falseAnswer"
                  v-model="picked"
                  value="f"
                />
                <br />
                <label class="answer" for="falseAnswer">False</label>
                <br /> -->
              </div>
              <div v-if="questions[currentQuestion - 1].type === 'mc'">
                <br />
                <q-option-group
                  :type="radio"
                  :options="questions[currentQuestion - 1].answers"
                  v-model="picked"
                />
                <!-- <div
                  v-for="(mcanswer, index) in questions[currentQuestion - 1]
                    .answers"
                  :key="index"
                >
                  <input
                    type="radio"
                    :id="'answer' + index"
                    name="currentQuestion"
                    v-model="picked"
                    :value="mcanswer"
                  />
                  <label class="answer" :for="'answer' + index">{{ mcanswer }}</label>
                  <br />
                </div> -->
                <br />
              </div>
              <br />
              <button
                type="button"
                v-if="currentQuestion !== 1"
                class="butt js--triggerAnimation"
                @click="previousQuestion()"
              >
                Previous
              </button>
              <button
                type="submit"
                v-if="currentQuestion != questions.length"
                class="butt js--triggerAnimation"
                @click="processAnswer(picked)"
              >
                Next
              </button>
              <button
                type="submit"
                v-if="currentQuestion === questions.length"
                class="butt js--triggerAnimation"
                @click="processAnswer(picked)"
              >
                Finish
              </button>
            <!-- </q-form> -->
            <!-- form end -->
          </div>
          <div v-if="scoresStage">
            Your score is {{ correct }}/{{ questions.length }} correct answers
            [ {{ perc }}% ]
          </div>
        </div>
      </div>
    </main>
  </q-page>
</template>

<script type="text/javascript">
const quizDataFile = `{
  "title": "Creditcards quiz",
  "description": "Test your basic creditcards knowledge",
  "subject": "Creditcards",
  "scope": "Basic knowledge",
  "questions": [
    {
      "text": "Is SIA the creditcard processor for ING Creditcards?",
      "type": "tf",
      "answer": "t"
    },
    {
      "text": "What is the best featured ING creditcard?",
      "type": "mc",
      "answers": [
        {"label": "ING Studentcard", "value": "student"},
        {"label": "ING Creditcard", "value": "creditcard"},
        {"label": "ING Platinumcard", "value": "platinum"}
      ],
      "answer": "platinum"
    },
    {
      "text": "What is the monthly repayment for revolving creditcards?",
      "type": "mc",
      "answers": [
        {"label": "5 percent", "value": "5"},
        {"label": "10 percent", "value": "10"},
        {"label": "15 percent", "value": "15"}
      ],
      "answer": "5"
    }
  ]}
`;

export default {
  data() {
    return {
      quizData: [],
      // use https://api.myjson.com/bins/d7kcc
      introStage: true,
      questionStage: false,
      scoresStage: false,

      title: '<empty>',
      description: '<empty>',
      subject: '<empty>',
      scope: '<empty>',
      questions: [],

      currentQuestion: 1,
      answers: [],
      picked: '',
      correct: 0,
      perc: null,
    };
  },
  mounted() {
    this.loadData();
  },
  methods: {
    loadData() {
      this.quizData = JSON.parse(quizDataFile);
      this.title = this.quizData.title;
      this.description = this.quizData.description;
      this.subject = this.quizData.subject;
      this.scope = this.quizData.scope;
      this.questions = this.quizData.questions;
      this.introStage = true;
    },
    startQuiz() {
      this.introStage = false;
      this.questionStage = true;
      this.scoresStage = false;

      this.currentQuestion = 1;
      this.answers = [];
      this.picked = '';
      this.correct = 0;
      this.perc = null;
    },
    previousQuestion() {
      if (this.currentQuestion >= 0) {
        this.currentQuestion -= 1;
      }
    },
    processAnswer(p) {
      // console.log('answer event ftw', e);
      this.answers[this.currentQuestion - 1] = p;
      if (this.currentQuestion === this.questions.length) {
        this.handleScores();
      } else {
        this.currentQuestion += 1;
      }
    },
    handleScores() {
      this.questions.forEach((a, index) => {
        if (this.answers[index] === a.answer) this.correct += 1;
      });
      this.perc = ((this.correct / this.questions.length) * 100).toFixed(0);
      // eslint-disable-next-line no-console
      // console.log(`${this.correct} ${this.perc}`);
      this.questionStage = false;
      this.scoresStage = true;
      this.introStage = true;
    },
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
  padding: 1.5rem;
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
