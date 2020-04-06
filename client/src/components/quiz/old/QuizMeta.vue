<template>
  <div class="quiz-meta">
    <!-- <router-link
      :to="{ name: 'profile', params: { username: quiz.author.username } }"
    >
      <img :src="quiz.author.image" />
    </router-link> -->
    <div class="info">
      <span class="date">created: {{ quiz.createdAt | date }} by: </span>
      <router-link
        :to="{ name: 'profile', params: { username: quiz.author.username } }"
        class="author"
      >{{ quiz.author.username }}
      </router-link>
    <QuizActions
      v-if="buttons"
      :quiz="quiz"
      :canModify="isCurrentUser()"
    ></QuizActions>
    <!-- <button
      v-else
      @click="toggleFavorite"
      :class="{
        'btn-primary': quiz.favorited,
        'btn-outline-primary': !quiz.favorited
      }"
    >
      <i class="ion-heart"></i>
      <span class="counter"> {{ quiz.favoritesCount }} </span>
    </button> -->
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import { FAVORITE_ADD, FAVORITE_REMOVE } from 'src/store/types/actions.type';
import QuizActions from './QuizActions';

export default {
  name: 'QuizMeta',
  components: {
    QuizActions,
  },
  props: {
    quiz: {
      type: Object,
      required: true,
    },
    buttons: {
      type: Boolean,
      required: false,
      default: true,
    },
  },
  computed: {
    ...mapGetters(['currentUser', 'isAuthenticated']),
  },
  methods: {
    isCurrentUser() {
      if (this.currentUser.username && this.quiz.author.username) {
        return this.currentUser.username === this.quiz.author.username;
      }
      return false;
    },
    toggleFavorite() {
      if (!this.isAuthenticated) {
        this.$router.push({ name: 'login' });
        return;
      }
      const action = this.quiz.favorited ? FAVORITE_REMOVE : FAVORITE_ADD;
      this.$store.dispatch(action, this.quiz.quizId);
    },
  },
};
</script>
