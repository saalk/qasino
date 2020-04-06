<template>
  <div class="quiz-page">

    <div class="banner">
      <div class="container">
        <h1>{{ quiz.title }}</h1>
        <!-- <QuizMeta :quiz="quiz" :actions="true"></QuizMeta> -->
      </div>
    </div>

    <div class="container page">
      <div class="row quiz-content">
        <div class="col-xs-12">
          <div v-html="parseMarkdown(quiz.questions[0].text)"></div>

          <ul class="tag-list">
            <li v-for="(tag, index) of quiz.tagList" :key="tag + index">
              <!-- <Tag
                :name="tag"
                className="tag-default tag-pill tag-outline"
              ></Tag> -->
            </li>
          </ul>
        </div>
      </div>
      <hr />

      <div class="quiz-actions">
        <!-- <QuizMeta :quiz="quiz" :actions="true"></QuizMeta> -->
      </div>
      <div class="row">
        <div class="col-xs-12 col-md-8 offset-md-2">
          <AnswerEditor
            v-if="isAuthenticated"
            :answerId="answerId"
            :userImage="currentUser.image"
          >
          </AnswerEditor>

          <p v-else>
            <router-link :to="{ name: 'login' }">Sign in</router-link>
            or
            <router-link :to="{ name: 'register' }">sign up</router-link>
            to add answers on this quiz.
          </p>

          <Answer
            v-for="(answer, index) in answers"
            :answerId="answerId"
            :answer="answer"
            :key="index"
          >
          </Answer>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import marked from 'marked';
import store from 'src/store';
// import QuizMeta from 'src/components/quiz/view/QuizMeta';
import Answer from 'src/components/answer/Answer';
import AnswerEditor from 'src/components/answer/AnswerEditor';
// import Tag from 'src/components/main/VTag';
import { FETCH_QUIZ, FETCH_ANSWERS } from 'src/store/types/actions.type';

export default {
  name: 'play-quiz',
  props: {
    quizId: {
      type: String,
      required: true,
    },
  },
  components: {
    // QuizMeta,
    Answer,
    AnswerEditor,
    // Tag,
  },
  beforeRouteEnter(to, from, next) {
    Promise.all([
      store.dispatch(FETCH_QUIZ, to.params.quizId),
      store.dispatch(FETCH_ANSWERS, to.params.quizId),
    ]).then(() => {
      next();
    });
  },
  computed: {
    ...mapGetters(['quiz', 'currentUser', 'answers', 'isAuthenticated']),
  },
  methods: {
    parseMarkdown(content) {
      return marked(content);
    },
  },
};
</script>
