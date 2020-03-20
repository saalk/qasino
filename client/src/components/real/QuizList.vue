<template>
  <div>
    <div v-if="isLoading" class="quiz-preview">Loading quizzes...</div>
    <div v-else>
      <div v-if="quizzes.length === 0" class="quiz-preview">
        No quizzes are here... yet.
      </div>
      <RwvQuizPreview
        v-for="(quiz, index) in quizzes"
        :quiz="quiz"
        :key="quiz.title + index"
      />
      <VPagination :pages="pages" :currentPage.sync="currentPage" />
    </div>
  </div>
</template>

<script>
import { FETCH_QUIZS } from 'src/store/types/actions.type';
import { mapGetters } from 'vuex';
import RwvQuizPreview from './VQuizPreview';
import VPagination from './VPagination';

export default {
  name: 'RwvQuizList',
  components: {
    RwvQuizPreview,
    VPagination,
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
      this.$store.dispatch(FETCH_QUIZS, this.listConfig);
    },
    resetPagination() {
      this.listConfig.offset = 0;
      this.currentPage = 1;
    },
  },
};
</script>
