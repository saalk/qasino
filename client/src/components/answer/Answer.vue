<template>
  <div class="card">
    <div class="card-block">
      <p class="card-text">{{ answer }}</p>
    </div>
    <div class="card-footer">
      <!-- <a href="" class="answer-author">
        <img :src="answer.author.image" class="answer-author-img" />
      </a> -->
      <!-- <router-link
        class="answer-author"
        :to="{ name: 'profile', params: { username: answer.author.username } }"
      >
        {{ answer.author.username }}
      </router-link> -->
      <span class="date-posted">{{ score.createdAt | date }}</span>
      <span v-if="isCurrentUser" class="mod-options">
        <i class="ion-trash-a" @click="destroy(scoreId, questionId)"></i>
      </span>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import { SCORE_DESTROY } from 'src/store/types/actions.type';

export default {
  name: 'Answer',
  props: {
    answerId: { type: String, required: true },
    answer: { type: Object, required: true },
  },
  computed: {
    isCurrentUser() {
      if (this.currentUser.username && this.answer.author.username) {
        return this.answer.author.username === this.currentUser.username;
      }
      return false;
    },
    ...mapGetters(['currentUser']),
  },
  methods: {
    destroy(scoreid) {
      this.$store.dispatch(SCORE_DESTROY, { scoreid });
    },
  },
};
</script>
