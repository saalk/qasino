<template>
  <div class="quiz-preview">
    <RwvQuizMeta isPreview :quiz="quiz" />
    <router-link :to="quizLink" class="preview-link">
      <h1 v-text="quiz.title" />
      <p v-text="quiz.description" />
      <span>Read more...</span>
      <TagList :tags="quiz.tagList" />
    </router-link>
  </div>
</template>

<script>
import RwvQuizMeta from './QuizMeta';
import TagList from './TagList';

export default {
  name: 'RwvQuizPreview',
  components: {
    RwvQuizMeta,
    TagList,
  },
  props: {
    quiz: { type: Object, required: true },
  },
  computed: {
    quizLink() {
      return {
        name: 'quiz',
        params: {
          id: this.quiz.id,
        },
      };
    },
  },
};
</script>
