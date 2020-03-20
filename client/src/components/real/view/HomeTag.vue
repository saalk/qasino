<template>
  <div class="home-tag"><RwvQuizList :tag="tag"></RwvQuizList></div>
</template>

<script>
import RwvQuizList from 'src/components/real/QuizList';

export default {
  name: 'RwvHomeTag',
  components: {
    RwvQuizList,
  },
  computed: {
    tag() {
      return this.$route.params.tag;
    },
  },
};
</script>
