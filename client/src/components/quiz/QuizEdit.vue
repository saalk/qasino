<template>

  <q-page padding>
    <q-card class="no-padding no-margin q-pa-md items-start">
      <q-card-section>
      <!-- <header class="site__header island"> -->
      <QuizEditHeader
      :quiz="this.quiz"
      :quizProgress="this.quizProgress"
      @startQuizParent="startQuiz"
      @stopQuizParent="stopQuiz"
      />
      <!-- </header> -->
      </q-card-section>
    </q-card>
    <q-card v-if="this.errors !== null">
      <ListErrors :errors="errors" />
    </q-card>
    <q-card>
      class="no-padding no-margin q-pa-md items-start">
      <main class="site__content island" role="content">
      <br />
      <!-- <QuizEditQuestions
        :quiz="this.quiz"
        :quizProgress="this.quizProgress"
        :score="this.score"
        :picked="this.currentAnswer.answer"
        @previousQuestionParent="previousQuestion"
        @processAnswerParent="processAnswer"
        /> -->
      <QuizEditQuestions
        :quiz="this.quiz"
        :quizProgress="this.quizProgress"
        :score="this.score"
        @previousQuestionParent="previousQuestion"
        @processAnswerParent="processAnswer"
        />
      </main>
    </q-card>
      <q-card>
          <form @submit.prevent="onPublish(quiz.quizId)">
            <fieldset :disabled="inProgress">
              <fieldset class="form-group">
                <input
                  type="text"
                  class="form-control form-control-lg"
                  v-model="quiz.meta.title"
                  placeholder="Quiz Title"
                />
              </fieldset>
              <fieldset class="form-group">
                <input
                  type="text"
                  class="form-control"
                  v-model="quiz.meta.description"
                  placeholder="What's this quiz about?"
                />
              </fieldset>
              <fieldset class="form-group">
                <textarea
                  class="form-control"
                  rows="8"
                  v-model="quiz.questions[0].text"
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
                    v-for="(tag, index) of quiz.meta.tagList"
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
    </q-card>
  </q-page>
</template>

<script>
import { mapGetters } from 'vuex';
import store from 'src/store';
import ListErrors from 'src/components/quiz/view/ListErrors';
import QuizEditHeader from 'src/components/quiz/edit/QuizEditHeader';
import QuizEditQuestions from 'src/components/quiz/edit/QuizEditQuestions';
import {
  QUIZ_PUBLISH,
  QUIZ_EDIT,
  FETCH_QUIZ,
  QUIZ_EDIT_ADD_TAG,
  QUIZ_EDIT_REMOVE_TAG,
  QUIZ_RESET_STATE,
} from 'src/store/types/actions.type';

export default {
  name: 'QuizEdit',
  components: { ListErrors, QuizEditHeader, QuizEditQuestions },
  props: {
    previousQuiz: {
      type: Object,
      required: false,
    },
  },
  async beforeRouteUpdate(to, from, next) {
    // Reset state if user goes from /editor/:quizId to /editor
    // The component is not recreated so we use to hook to reset the state.
    await store.dispatch(QUIZ_RESET_STATE);
    return next();
  },
  async beforeRouteEnter(to, from, next) {
    // SO: https://github.com/vuejs/vue-router/issues/1034
    // If we arrive directly to this url, we need to fetch the quiz
    await store.dispatch(QUIZ_RESET_STATE);
    if (to.params.quizId !== undefined) {
      await store.dispatch(
        FETCH_QUIZ,
        to.params.quizId,
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
    onPublish(quizId) {
      const action = quizId ? QUIZ_EDIT : QUIZ_PUBLISH;
      this.inProgress = true;
      this.$store
        .dispatch(action)
        .then(({ data }) => {
          this.inProgress = false;
          this.$router.push({
            name: 'quiz-play',
            params: { quizId: data.quiz.quizId },
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
