<template>
  <q-page padding>
    <q-card class="no-padding no-margin q-pa-md items-start">
      <q-card-section>
        <MyCustomQueryBuilderGroup></MyCustomQueryBuilderGroup>
      </q-card-section>
    </q-card>
    <q-card class="no-padding no-margin q-pa-md">
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
            <QuizPagination :pages="pages" :currentPage.sync="currentPage" />
          </div>
        </q-page>
    </q-card>
  </q-page>
</template>

<script>
import { mapGetters } from 'vuex';
import { FETCH_QUIZS } from 'src/store/types/actions.type';
import QuizPagination from 'src/components/quiz/view/QuizPagination';
import QuizPreview from 'src/components/quiz/view/QuizPreview';
import MyCustomQueryBuilderGroup from './MyCustomQueryBuilderGroup';

export default {
  name: 'QuizList',
  components: {
    QuizPreview,
    QuizPagination,
    MyCustomQueryBuilderGroup,
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
      query: Object,
    };
  },
  computed: {
    listConfig() {
      const queryPreset = `
      {
        "operatorIdentifier": "AND",
        "children": [{
          "identifier": "foo",
          "value": "bar"
      }]
      `;
      const { type } = this;
      const filters = {
        offset: (this.currentPage - 1) * this.itemsPerPage,
        limit: this.itemsPerPage,
      };
      if (this.author) {
        filters.author = this.author;
        queryPreset.replace('foo', 'author');
        // this.query.slice(0).queryPreset.replace('bar', this.author);
      } else {
        filters.author = '';
      }
      if (this.tag) {
        filters.tag = this.tag;
        queryPreset.replace('foo', 'tag');
        // this.query.slice(0).queryPreset.replace('bar', this.tag);
      } else {
        filters.tag = '';
      }
      if (this.favorited) {
        filters.favorited = this.favorited;
        queryPreset.replace('foo', 'favorited');
        // this.query.slice(0).queryPreset.replace('bar', this.favorited);
      } else {
        filters.favorited = '';
      }
      if (this.type) {
        filters.type = this.type;
        queryPreset.replace('foo', 'type');
        // this.query.slice(0).queryPreset.replace('bar', this.type);
      } else {
        filters.type = '';
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
      // - type (all, favorited or following)
      // - tag
      // - author (user or selection)
      this.$store.dispatch(FETCH_QUIZS, this.listConfig);
    },
    resetPagination() {
      this.listConfig.offset = 0;
      this.currentPage = 1;
    },
  },
};
</script>
