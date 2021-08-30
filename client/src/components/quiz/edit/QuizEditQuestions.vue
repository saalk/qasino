<template>
  <div>
    <strong class="beta subhead">Question {{ this.score.computed.currentQuestion }}:</strong>
    <p class="question">{{ this.quiz.questions[this.score.computed.currentIndex].text }}</p>
    <!-- true-false -->
    <div v-if="this.quiz.questions[this.score.computed.currentIndex].category === 'trueFalse'">
      <!-- <q-radio v-model="picked" val="t" label="True" />
      <q-radio v-model="picked" val="f" label="False" /> -->
      <q-radio v-model="picked" val="t" label="True" />
      <q-radio v-model="picked" val="f" label="False" />
      <br />
      <br />
    </div>
    <!-- images -->
    <div v-if="this.quiz.questions[this.score.computed.currentIndex].category === 'images'">
      <q-option-group
        :options="this.quiz.questions[this.score.computed.currentIndex].images"
        v-model="picked"
      />
      <br />
    </div>
    <!-- multi-choice -->
    <div v-if="this.quiz.questions[this.score.computed.currentIndex].category === 'multiChoice'">
      <q-option-group
        :options="this.quiz.questions[this.score.computed.currentIndex].choices"
        v-model="picked"
      />
      <br />
    </div>
    <!-- text-essay -->
    <div v-if="this.quiz.questions[this.score.computed.currentIndex].category === 'textEssay'">
      <q-input v-bind="picked"
      />
      <br />
    </div>
    <!-- fill-the-blanks -->
    <div v-if="this.quiz.questions[this.score.computed.currentIndex].category === 'fillTheBlanks'">
      <q-input v-bind="picked"
      />
      <br />
    </div>
    <!-- numerical -->
    <div v-if="this.quiz.questions[this.score.computed.currentIndex].category === 'numerical'">
      <q-input v-model="picked"
      placeholder="type your answer"
      lazy-rules :rules="[ val => val && !isNaN(val) || 'only digits']"
      />
      <br />
    </div>
    <!-- multi-answer -->
    <div v-if="this.quiz.questions[this.score.computed.currentIndex].category === 'multiAnswer'">
      <q-option-group
        type="checkbox"
        :options="this.quiz.questions[this.score.computed.currentIndex].choices"
      />
      <br />
    </div>
    <q-btn v-if="this.score.computed.currentQuestion !== 1"
      class="actions text-h7 text-orange-9"
      outline no-caps color="white"
      label="Previous"
      @click="previousQuestionChild()">
    </q-btn>
    <q-btn v-if="this.score.computed.currentQuestion !== this.quiz.questions.length"
      class="actions text-h7 text-orange-9"
      outline no-caps color="white"
      label="Next"
      :disable="picked === ''"
      @click="processAnswerChild()">
    </q-btn>
    <q-btn v-else
      class="actions text-h7 text-orange-9"
      outline no-caps color="white"
      label="Finish"
      :disable="picked === ''"
      @click="processAnswerChild()">
    </q-btn>
  </div>
</template>

<script type="text/javascript">
export default {
  props: [
    'quiz',
    'quizProgress',
    'score',
    'picked',
    'previousQuestionParent',
    'processAnswerParent',
  ],
  // data() {
  //   return {
  //     picked: '',
  //   };
  // },
  // watch: {
  //   pickedPrevious() {
  //     this.picked = this.pickedPrevious;
  //   },
  // },
  methods: {
    previousQuestionChild() {
      this.picked = '';
      this.$emit('previousQuestionParent');
    },
    processAnswerChild() {
      this.$emit('processAnswerParent', this.picked);
      this.picked = '';
    },
  },
};
</script>

<style scoped>
/*-----------------------------------*\
  $RESET
\*-----------------------------------*/
.actions{
    /* no underlining */
    text-decoration: none;
    margin-right: 5px
}
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
