<template>
  <q-page padding>
    <transition v-if="isLoading"
      appear
      enter-active-class="animated fadeIn"
      leave-active-class="animated fadeOut"
    >
      <div id="animationSandbox" style="display: block;" class>
        <h1 key="text" class="site__title mega">Loading...</h1>
      </div>
    </transition>
    <div v-else>
      <transition v-if="quizzes.length === 0"
        appear
        enter-active-class="animated fadeIn"
        leave-active-class="animated fadeOut"
      >
        <div id="animationSandbox" style="display: block;" class>
          <h1 key="text" class="site__title mega">No quizzes available</h1>
        </div>
      </transition>
      <QuizPreview
        v-for="(quiz, index) in quizzes"
        :key="quiz.quizId + index"
        :quiz="quiz"
      />
      <QuizPagination :pages="pages" v-model:currentPage="currentPage" />
    </div>
  </q-page>
</template>

<script>
import { FETCH_QUIZS } from 'src/store/types/actions.type';
import { mapGetters } from 'vuex';
import QuizPreview from './view/QuizPreview';
import QuizPagination from './view/QuizPagination';

export default {
  name: 'QuizList',
  components: {
    QuizPreview,
    QuizPagination,
  },
  props: {
    type: {
      type: String,
      required: false,
      default: 'all',
    },
    author: {
      type: String,
      required: false,
    },
    tag: {
      type: String,
      required: false,
    },
    favorited: {
      type: String,
      required: false,
    },
    itemsPerPage: {
      type: Number,
      required: false,
      default: 10,
    },
  },
  data() {
    return {
      currentPage: 1,
    };
  },
  computed: {
    listConfig() {
      const { type } = this;
      const filters = {
        offset: (this.currentPage - 1) * this.itemsPerPage,
        limit: this.itemsPerPage,
      };
      if (this.author) {
        filters.author = this.author;
      }
      if (this.tag) {
        filters.tag = this.tag;
      }
      if (this.favorited) {
        filters.favorited = this.favorited;
      }
      return {
        type,
        filters,
      };
    },
    pages() {
      if (this.isLoading || this.quizzesCount <= this.itemsPerPage) {
        return [];
      }
      return [
        ...Array(Math.ceil(this.quizzesCount / this.itemsPerPage)).keys(),
      ].map((e) => e + 1);
    },
    ...mapGetters(['quizzesCount', 'isLoading', 'quizzes']),
  },
  watch: {
    currentPage(newValue) {
      this.listConfig.filters.offset = (newValue - 1) * this.itemsPerPage;
      this.fetchQuizzes();
    },
    type() {
      this.resetPagination();
      this.fetchQuizzes();
    },
    author() {
      this.resetPagination();
      this.fetchQuizzes();
    },
    tag() {
      this.resetPagination();
      this.fetchQuizzes();
    },
    favorited() {
      this.resetPagination();
      this.fetchQuizzes();
    },
  },
  mounted() {
    this.fetchQuizzes();
  },
  methods: {
    fetchQuizzes() {
      // fetch quizzes per
      // - pagination
      // - type (all, fav or following)
      // - tag
      // - author (users or selections)
      this.$store.dispatch(FETCH_QUIZS, this.listConfig);
    },
    resetPagination() {
      this.listConfig.offset = 0;
      this.currentPage = 1;
    },
  },
};
</script>
