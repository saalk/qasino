<template>
  <div class="quiz-meta">
    <router-link
      :to="{ name: 'profile', params: { username: quiz.author.username } }"
    >
      <img :src="quiz.author.image" />
    </router-link>
    <div class="info">
      <router-link
        :to="{ name: 'profile', params: { username: quiz.author.username } }"
        class="author"
      >
        {{ quiz.author.username }}
      </router-link>
      <span class="date">{{ quiz.createdAt | date }}</span>
    </div>
    <rwv-quiz-actions
      v-if="actions"
      :quiz="quiz"
      :canModify="isCurrentUser()"
    ></rwv-quiz-actions>
    <button
      v-else
      class="btn btn-sm pull-xs-right"
      @click="toggleFavorite"
      :class="{
        'btn-primary': quiz.favorited,
        'btn-outline-primary': !quiz.favorited
      }"
    >
      <i class="ion-heart"></i>
      <span class="counter"> {{ quiz.favoritesCount }} </span>
    </button>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import RwvQuizActions from 'src/components/real/QuizActions';
import { FAVORITE_ADD, FAVORITE_REMOVE } from 'src/store/types/actions.type';

export default {
  name: 'RwvQuizMeta',
  components: {
    RwvQuizActions,
  },
  props: {
    quiz: {
      type: Object,
      required: true,
    },
    actions: {
      type: Boolean,
      required: false,
      default: false,
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
      this.$store.dispatch(action, this.quiz.id);
    },
  },
};
</script>
