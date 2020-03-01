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
            <button class="butt js--triggerAnimation" @click="startQuiz">
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
              <div v-if="questions[currentQuestion - 1].type === 'truefale'">
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
              <div v-if="questions[currentQuestion - 1].type === 'multichoise'">
                <br />
                <q-option-group
                  :type="radio"
                  :options="questions[currentQuestion - 1].choices"
                  v-model="picked"
                />
                <!-- <div
                  v-for="(mcanswer, index) in questions[currentQuestion - 1]
                    .choices"
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
                @click="previousQuestion"
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
          <div v-if="resultsStage">
            Your result is {{ correct }}/{{ questions.length }} correct choices
            [ {{ perc }}% ]
          </div>
        </div>
      </div>
    </main>
  </q-page>
</template>

<script type="text/javascript">
const quizDataFile = `{
  "title": "x quiz",
  "description": "Test your basic product x knowledge",
  "subject": "product x",
  "scope": "Basic knowledge",
  "questions": [
    {
      "text": "Is product x hosted by a third party outside ING?",
      "category": "true-false",
      "answer": "t",
      "explanation": "Product x is being administrated at a third party located in y"
    },
    {
      "text": "Which picture of avalable options for product x has the beste features?",
      "category": "images",
      "images": [
        {"src": "https://a.png", "alt": "description of a when not loaded", value": "a"},
        {"src": "https://a.png", "alt": "description of b when not loaded", value": "b"},
        {"src": "https://a.png", "alt": "description of c when not loaded", value": "c"},
      ],
      "answer": "c",
      "explanation": "Variant c has in total z features, more then any of the other variants"
    },
    {
      "text": "Which product varaint of product x has the beste features?",
      "category": "matching",
      "choices": [
        {"label": "Variant a", "value": "a"},
        {"label": "Variant b", "value": "b"},
        {"label": "Variant c", "value": "c"}
      ],
      "answer": "c",
      "explanation": "Variant c has in total z features more then the other variants"
    },
    {
      "text": "Decribe the slogan to attract customers for product x, you need to mention at least 2 key elements",
      "category": "text-essay",
      "answers": [{"easy"},{"easily"},{"cheap"},{"low priced"},{"robust"}]
      "atLeastNeeded": "2",
      "explanation": "Product x, easy and rubust when used and amongst the cheapest on the market"
    },
    {
      "text": "Product x is ___ and ___ when used and amongst the cheapest on the market",
      "category": "fill-the-blanks",
      "answersOrdered": [{"easy"},{"robust"}]
      "explanation": "Product x, easy and rubust when used and amongst the cheapest on the market"
    },
    {
      "text": "What is the minimum age to apply for porduct x?",
      "category": "numerical",
      "answer": "18",
      "explanation": "You should be at least 18 to apply but for a partner 16 is the minimum"
    },
    {
      "text": "What are the key elements of porduct x?",
      "category": "multi-answer",
      "choices": [
        {"label": "Easy to use", "value": "easy"},
        {"label": "Expensive when compared", "value": "expensive"},
        {"label": "5 star ratings", "value": "robust"}
      ],
      "answers": [{"easy"},{"robust"}]
      "explanation": "Product x, easy and rubust when used and amongst the cheapest on the market"
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
      resultsStage: false,

      title: '<empty>',
      description: '<empty>',
      subject: '<empty>',
      scope: '<empty>',
      questions: [],

      currentQuestion: 1,
      choices: [],
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
      this.resultsStage = false;

      this.currentQuestion = 1;
      this.choices = [];
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
      this.choices[this.currentQuestion - 1] = p;
      if (this.currentQuestion === this.questions.length) {
        this.handleResults();
      } else {
        this.currentQuestion += 1;
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
