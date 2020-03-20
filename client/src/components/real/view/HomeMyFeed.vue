<template>
  <div class="home-my-feed"><RwvQuizList type="feed" /></div>
</template>

<script>
import RwvQuizList from 'src/components/real/QuizList';

export default {
  name: 'rwv-home-my-feed',
  components: {
    RwvQuizList,
  },
};
</script>
