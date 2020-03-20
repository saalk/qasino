<template>
  <!-- Used when user is also author -->
  <span v-if="canModify">
    <router-link class="btn btn-sm btn-outline-secondary" :to="editQuizLink">
      <i class="ion-edit"></i> <span>&nbsp;Edit Quiz</span>
    </router-link>
    <span>&nbsp;&nbsp;</span>
    <button class="btn btn-outline-danger btn-sm" @click="deleteQuiz">
      <i class="ion-trash-a"></i> <span>&nbsp;Delete Quiz</span>
    </button>
  </span>
  <!-- Used in QuizView when not author -->
  <span v-else>
    <button class="btn btn-sm btn-outline-secondary" @click="toggleFollow">
      <i class="ion-plus-round"></i> <span>&nbsp;</span>
      <span v-text="followUserLabel" />
    </button>
    <span>&nbsp;&nbsp;</span>
    <button
      class="btn btn-sm"
      @click="toggleFavorite"
      :class="toggleFavoriteButtonClasses"
    >
      <i class="ion-heart"></i> <span>&nbsp;</span>
      <span v-text="favoriteQuizLabel" /> <span>&nbsp;</span>
      <span class="counter" v-text="favoriteCounter" />
    </button>
  </span>
</template>

<script>
import { mapGetters } from 'vuex';
import {
  FAVORITE_ADD,
  FAVORITE_REMOVE,
  QUIZ_DELETE,
  FETCH_PROFILE_FOLLOW,
  FETCH_PROFILE_UNFOLLOW,
} from 'src/store/types/actions.type';

export default {
  name: 'RwvQuizActions',
  props: {
    quiz: { type: Object, required: true },
    canModify: { type: Boolean, required: true },
  },
  computed: {
    ...mapGetters(['profile', 'isAuthenticated']),
    editQuizLink() {
      return { name: 'quiz-edit', params: { id: this.quiz.id } };
    },
    toggleFavoriteButtonClasses() {
      return {
        'btn-primary': this.quiz.favorited,
        'btn-outline-primary': !this.quiz.favorited,
      };
    },
    followUserLabel() {
      return `${this.profile.following ? 'Unfollow' : 'Follow'} ${
        this.quiz.author.username
      }`;
    },
    favoriteQuizLabel() {
      return this.quiz.favorited ? 'Unfavorite Quiz' : 'Favorite Quiz';
    },
    favoriteCounter() {
      return `(${this.quiz.favoritesCount})`;
    },
  },
  methods: {
    toggleFavorite() {
      if (!this.isAuthenticated) {
        this.$router.push({ name: 'login' });
        return;
      }
      const action = this.quiz.favorited ? FAVORITE_REMOVE : FAVORITE_ADD;
      this.$store.dispatch(action, this.quiz.id);
    },
    toggleFollow() {
      if (!this.isAuthenticated) {
        this.$router.push({ name: 'login' });
        return;
      }
      const action = this.quiz.following
        ? FETCH_PROFILE_UNFOLLOW
        : FETCH_PROFILE_FOLLOW;
      this.$store.dispatch(action, {
        username: this.profile.username,
      });
    },
    async deleteQuiz() {
      try {
        await this.$store.dispatch(QUIZ_DELETE, this.quiz.id);
        this.$router.push('/');
      } catch (err) {
        // eslint-disable-next-line no-console
        console.error(err);
      }
    },
  },
};
</script>
