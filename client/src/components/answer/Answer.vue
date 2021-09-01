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
      <span class="date-posted">{{ date(score.created) }}</span>
      <span v-if="isCurrentUser" class="mod-options">
        <i class="ion-trash-a" @click="destroy(scoreId, questionId)"></i>
      </span>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import { SCORE_DESTROY } from 'src/store/types/actions.type';
import format from 'date-fns/format';

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
    currencyEUR(value) {
      return `€${value}`;
    },
    capitalize(stringToCap) {
      if (typeof stringToCap !== 'string') return 'no-string';
      return `${stringToCap.charAt(0).toUpperCase() + stringToCap.slice(1)}`;
    },
    date(date) {
      return format(new Date(date), 'MMMM d, yyyy');
    },
    error(errorValue) {
      return `${errorValue[0]}`;
    },
    trimto(value, len) {
      if (!value) return '';
      if (!len) len = 20;
      value = value.toString();
      if (value.length <= len) {
        // return `${value}`;
        return value;
      }
      // return `${value.substr(0, size)}...`;
      return `${value.substring(0, len - 3)}...`;
    },
  },
};
</script>
