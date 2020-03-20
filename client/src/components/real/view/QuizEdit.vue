<template>
  <div class="editor-page">
    <div class="container page">
      <div class="row">
        <div class="col-md-10 offset-md-1 col-xs-12">
          <RwvListErrors :errors="errors" />
          <form @submit.prevent="onPublish(quiz.id)">
            <fieldset :disabled="inProgress">
              <fieldset class="form-group">
                <input
                  type="text"
                  class="form-control form-control-lg"
                  v-model="quiz.title"
                  placeholder="Quiz Title"
                />
              </fieldset>
              <fieldset class="form-group">
                <input
                  type="text"
                  class="form-control"
                  v-model="quiz.description"
                  placeholder="What's this quiz about?"
                />
              </fieldset>
              <fieldset class="form-group">
                <textarea
                  class="form-control"
                  rows="8"
                  v-model="quiz.body"
                  placeholder="Write your quiz (in markdown)"
                >
                </textarea>
              </fieldset>
              <fieldset class="form-group">
                <input
                  type="text"
                  class="form-control"
                  placeholder="Enter tags"
                  v-model="tagInput"
                  @keypress.enter.prevent="addTag(tagInput)"
                />
                <div class="tag-list">
                  <span
                    class="tag-default tag-pill"
                    v-for="(tag, index) of quiz.tagList"
                    :key="tag + index"
                  >
                    <i class="ion-close-round" @click="removeTag(tag)"> </i>
                    {{ tag }}
                  </span>
                </div>
              </fieldset>
            </fieldset>
            <button
              :disabled="inProgress"
              class="btn btn-lg pull-xs-right btn-primary"
              type="submit"
            >
              Publish Quiz
            </button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import store from 'src/store';
import RwvListErrors from 'src/components/real/ListErrors';
import {
  QUIZ_PUBLISH,
  QUIZ_EDIT,
  FETCH_QUIZ,
  QUIZ_EDIT_ADD_TAG,
  QUIZ_EDIT_REMOVE_TAG,
  QUIZ_RESET_STATE,
} from 'src/store/types/actions.type';

export default {
  name: 'RwvQuizEdit',
  components: { RwvListErrors },
  props: {
    previousQuiz: {
      type: Object,
      required: false,
    },
  },
  async beforeRouteUpdate(to, from, next) {
    // Reset state if user goes from /editor/:id to /editor
    // The component is not recreated so we use to hook to reset the state.
    await store.dispatch(QUIZ_RESET_STATE);
    return next();
  },
  async beforeRouteEnter(to, from, next) {
    // SO: https://github.com/vuejs/vue-router/issues/1034
    // If we arrive directly to this url, we need to fetch the quiz
    await store.dispatch(QUIZ_RESET_STATE);
    if (to.params.id !== undefined) {
      await store.dispatch(
        FETCH_QUIZ,
        to.params.id,
        to.params.previousQuiz,
      );
    }
    return next();
  },
  async beforeRouteLeave(to, from, next) {
    await store.dispatch(QUIZ_RESET_STATE);
    next();
  },
  data() {
    return {
      tagInput: null,
      inProgress: false,
      errors: {},
    };
  },
  computed: {
    ...mapGetters(['quiz']),
  },
  methods: {
    onPublish(id) {
      const action = id ? QUIZ_EDIT : QUIZ_PUBLISH;
      this.inProgress = true;
      this.$store
        .dispatch(action)
        .then(({ data }) => {
          this.inProgress = false;
          this.$router.push({
            name: 'quiz',
            params: { id: data.quiz.id },
          });
        })
        .catch(({ response }) => {
          this.inProgress = false;
          this.errors = response.data.errors;
        });
    },
    removeTag(tag) {
      this.$store.dispatch(QUIZ_EDIT_REMOVE_TAG, tag);
    },
    addTag(tag) {
      this.$store.dispatch(QUIZ_EDIT_ADD_TAG, tag);
      this.tagInput = null;
    },
  },
};
</script>
