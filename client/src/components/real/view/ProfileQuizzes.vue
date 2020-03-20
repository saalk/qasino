<template>
  <div class="profile-page">
    <RwvQuizList :author="author" :items-per-page="5"></RwvQuizList>
  </div>
</template>

<script>
import RwvQuizList from 'src/components/real/QuizList';

export default {
  name: 'RwvProfileQuizzes',
  components: {
    RwvQuizList,
  },
  computed: {
    author() {
      return this.$route.params.username;
    },
  },
};
</script>
