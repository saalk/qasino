<template>
  <div class="quiz-page">
    <div class="banner">
      <div class="container">
        <h1>{{ quiz.title }}</h1>
        <RwvQuizMeta :quiz="quiz" :actions="true"></RwvQuizMeta>
      </div>
    </div>
    <div class="container page">
      <div class="row quiz-content">
        <div class="col-xs-12">
          <div v-html="parseMarkdown(quiz.body)"></div>
          <ul class="tag-list">
            <li v-for="(tag, index) of quiz.tagList" :key="tag + index">
              <RwvTag
                :name="tag"
                className="tag-default tag-pill tag-outline"
              ></RwvTag>
            </li>
          </ul>
        </div>
      </div>
      <hr />
      <div class="quiz-actions">
        <RwvQuizMeta :quiz="quiz" :actions="true"></RwvQuizMeta>
      </div>
      <div class="row">
        <div class="col-xs-12 col-md-8 offset-md-2">
          <RwvTestEditor
            v-if="isAuthenticated"
            :id="id"
            :userImage="currentUser.image"
          >
          </RwvTestEditor>
          <p v-else>
            <router-link :to="{ name: 'login' }">Sign in</router-link>
            or
            <router-link :to="{ name: 'register' }">sign up</router-link>
            to add tests on this quiz.
          </p>
          <RwvTest
            v-for="(test, index) in tests"
            :id="id"
            :test="test"
            :key="index"
          >
          </RwvTest>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import marked from 'marked';
import store from 'src/store';
import RwvQuizMeta from 'src/components/real/QuizMeta';
import RwvTest from 'src/components/real/Test';
import RwvTestEditor from 'src/components/real/TestEditor';
import RwvTag from 'src/components/real/VTag';
import { FETCH_QUIZ, FETCH_TESTS } from 'src/store/types/actions.type';

export default {
  name: 'rwv-quiz',
  props: {
    id: {
      type: String,
      required: true,
    },
  },
  components: {
    RwvQuizMeta,
    RwvTest,
    RwvTestEditor,
    RwvTag,
  },
  beforeRouteEnter(to, from, next) {
    Promise.all([
      store.dispatch(FETCH_QUIZ, to.params.id),
      store.dispatch(FETCH_TESTS, to.params.id),
    ]).then(() => {
      next();
    });
  },
  computed: {
    ...mapGetters(['quiz', 'currentUser', 'tests', 'isAuthenticated']),
  },
  methods: {
    parseMarkdown(content) {
      return marked(content);
    },
  },
};
</script>
