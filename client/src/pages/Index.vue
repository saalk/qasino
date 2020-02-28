<template>
  <q-page class="flex flex-center">
    <div id="quiz">
      <div v-if="introStage">
        <h1>Welcome to the Quiz: {{title}}</h1>
        <p>Answer a couple of questions on Creditcards to test your knowledge</p>

        <button @click="startQuiz">START!</button>
      </div>

      <div v-if="questionStage">
        <question
          :question="questions[currentQuestion]"
          v-on:answer="handleAnswer"
          :question-number="currentQuestion+1"
        ></question>
      </div>

      <div
        v-if="resultsStage"
      >You got {{correct}} right out of {{questions.length}}
      questions. Your percentage is {{perc}}%.</div>
    </div>
  </q-page>
</template>

<script>
import Vue from 'Vue';

const quizData = '~assets/QuizData.json';

// what does declaring a const with 'app' do?
// set app.title to something
const app = new Vue({
  // DOM element for Vue to mount on
  // - # is the id to mount data on without #
  // - . is the first class to mount on
  // - $mount('#foo') delayed mount
  el: '#quiz',
  data() {
    return {
      introStage: true,
      questionStage: false,
      resultsStage: false,
      title: '',
      questions: [],
      currentQuestion: 0,
      answers: [],
      correct: 0,
      perc: null,
    };
  },
  created() {
    fetch(quizData)
      .then((res) => res.json())
      .then((res) => {
        this.title = res.title;
        this.questions = res.questions;
        this.introStage = true;
      });
  },
  methods: {
    startQuiz() {
      this.introStage = false;
      this.questionStage = true;
      // console.log(`test${JSON.stringify(this.questions[this.currentQuestion])}`);
    },
    handleAnswer(e) {
      // console.log('answer event ftw', e);
      this.answers[this.currentQuestion] = e.answer;
      if ((this.currentQuestion + 1) === this.questions.length) {
        this.handleResults();
        this.questionStage = false;
        this.resultsStage = true;
      } else {
        this.currentQuestion += 1;
      }
    },
    handleResults() {
      // console.log('handle results');
      this.questions.forEach((a, index) => {
        if (this.answers[index] === a.answer) this.correct += 1;
      });
      this.perc = ((this.correct / this.questions.length) * 100).toFixed(2);
      // console.log(`${this.correct} ${this.perc}`);
    },
  },
});

export default {
  name: 'PageIndex',
  app,
};
</script>
