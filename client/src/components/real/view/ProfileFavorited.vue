<template>
  <div class="profile-page">
    <RwvQuizList :favorited="favorited" :items-per-page="5">
    </RwvQuizList>
  </div>
</template>

<script>
import RwvQuizList from 'src/components/real/QuizList';

export default {
  name: 'RwvProfileFavorited',
  components: {
    RwvQuizList,
  },
  computed: {
    favorited() {
      return this.$route.params.username;
    },
  },
};
</script>
