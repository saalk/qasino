<template>
  <q-card-actions class="no-padding no-margin">

    <router-link class="actions"
      :to="{ name: 'quiz-play', params: { quizId: quiz.quizId } }">
      <q-btn dense outline text-color="grey" label="Start quiz" no-caps></q-btn>
    </router-link>

    <router-link v-if=canModify class="actions"
      :to="{ name: 'quiz-edit', params: { quizId: quiz.quizId } }">
      <q-btn outline dense color="blue-grey-6" icon="las la-pen-alt">
        <q-tooltip content-class="bg-accent">edit</q-tooltip>
      </q-btn>
    </router-link>

    <q-btn v-if=canModify outline to="error" dense color="blue-grey-6" icon="las la-trash"
      @click="deleteQuiz()">
        <q-tooltip content-class="bg-accent">delete</q-tooltip>
    </q-btn>

    <q-btn v-if=!canModify to="error" outline dense color="blue-grey-6" :icon="favoriteQuizIcon"
      @click="toggleFavorite()">
        <q-tooltip content-class="bg-accent">{{ toolTipBookmark }}</q-tooltip>
    </q-btn>
  </q-card-actions>
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
  name: 'QuizPreviewActions',
  props: {
    quiz: { type: Object, required: true },
    canModify: { type: Boolean, required: true, default: true },
  },
  data() {
    return {
      toolTipBookmark: {
        type: String,
      },
    };
  },
  mounted() {
    this.favoriteQuizTooltip();
  },
  computed: {
    ...mapGetters(['profile', 'isAuthenticated']),
    toggleFavoriteButtonClasses() {
      return {
        'btn-primary': this.quiz.computed.favorited,
        'btn-outline-primary': !this.quiz.computed.favorited,
      };
    },
    followUserLabel() {
      const isFollowing = this.quiz.author.computed.following ? 'Unfollow' : 'Follow';
      return isFollowing;
      // return `${this.profile.computed.following ? 'Unfollow' : 'Follow'} ${
      //   this.quiz.author.username
      // }`;
    },
    favoriteQuizIcon() {
      // favorite_border = no solid
      return this.quiz.computed.favorited ? 'bookmark' : 'bookmark_border';
    },
    favoriteCounter() {
      return this.quiz.computed.favoritesCount;
    },
  },
  methods: {
    favoriteQuizTooltip() {
      // favorite_border = no solid
      this.toolTipBookmark = this.quiz.computed.favorited ? 'delete bookmark' : 'bookmark this quiz';
    },
    toggleFavorite() {
      if (!this.isAuthenticated) {
        this.$router.push({ name: 'login' });
        return;
      }
      const action = this.quiz.computed.favorited ? FAVORITE_REMOVE : FAVORITE_ADD;
      this.$store.dispatch(action, this.quiz.quizId);
    },
    toggleFollow() {
      if (!this.isAuthenticated) {
        this.$router.push({ name: 'login' });
        return;
      }
      const action = this.quiz.author.computed.following
        ? FETCH_PROFILE_UNFOLLOW
        : FETCH_PROFILE_FOLLOW;
      this.$store.dispatch(action, {
        username: this.profile.username,
      });
    },
    async deleteQuiz() {
      try {
        await this.$store.dispatch(QUIZ_DELETE, this.quiz.quizId);
        this.$router.push('/');
      } catch (err) {
        // eslint-disable-next-line no-console
        console.error(err);
      }
    },
  },
};
</script>
