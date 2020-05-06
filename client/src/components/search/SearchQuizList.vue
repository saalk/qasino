<template>
  <q-page padding>
    <q-card class="no-padding no-margin q-pa-md items-start">
      <q-card-section>
        <MyCustomQueryBuilderGroup :presetQuery="this.listConfig.queryPreset">
        </MyCustomQueryBuilderGroup>
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
  name: 'SearchQuizList',
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
    bookmarks: {
      type: Boolean,
      required: false,
    },
    following: {
      type: Boolean,
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
      const queryPreset = {
        operatorIdentifier: 'AND',
        children: [],
      };
      const { type } = this;
      const filters = {
        offset: (this.currentPage - 1) * this.itemsPerPage,
        limit: this.itemsPerPage,
      };
      if (this.author) {
        filters.author = this.author;
        queryPreset.children.push({ identifier: 'author', value: this.author });
      } else {
        filters.author = '';
      }
      if (this.tag) {
        filters.tag = this.tag;
        queryPreset.children.push({ identifier: 'tag', value: this.tag });
      } else {
        filters.tag = '';
      }
      if (this.bookmarks) {
        filters.bookmarks = this.bookmarks;
        queryPreset.children.push({ identifier: 'bookmarks', value: this.bookmarks });
      } else {
        filters.bookmarks = false;
      }
      if (this.following) {
        filters.following = this.following;
        queryPreset.children.push({ identifier: 'following', value: this.following });
      } else {
        filters.following = false;
      }
      if (this.type) {
        filters.type = this.type;
      } else {
        filters.type = '';
      }
      return {
        type,
        filters,
        queryPreset,
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
