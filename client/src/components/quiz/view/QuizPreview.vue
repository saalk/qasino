<template>
  <q-card class="no-padding no-margin q-pa-md items-start">
    <q-card-section>

      <div class="text-h6 text-orange-9">
        Title: {{ trimto(quiz.meta.title, 30) }}
      </div>
      <div class="no-margin text-h7 q-mt-sm q-mb-xs">
        {{ trimto(quiz.meta.description, 55) }}
      </div>
      <div class="no-margin text-h7 q-mt-sm q-mb-xs">
        Subject: {{ trimto(quiz.meta.subject, 55) }}</div>
      <q-item dense class="no-padding no-margin ">
        <q-item-label class="no-padding no-margin">Tags:&nbsp;[&nbsp;</q-item-label>
        <q-item-label class="no-padding no-margin"
        v-for="(tag, index) in quiz.meta.tagList" :key="tag">
          <span>{{tag}}</span>
          <span v-if="index+1 < quiz.meta.tagList.length">&nbsp;|&nbsp;</span>
          <span v-else>&nbsp;]</span>
        </q-item-label>
      </q-item>
      <!-- text-caption text-grey-6" -->
      <div class="text-grey-6">
        <div class="row">
          <div class="col-md-4">
            <q-img class="bg-transparent" width="100px" height="100px"
            :src="quiz.author.image">
            <span>&nbsp;by:&nbsp;</span>
            <router-link
              :to="{ name: 'profile-author',
              params: { username: this.quiz.author.username } }">{{ quiz.author.username }}
            </router-link>
            </q-img>
          </div>
          <!-- <div class="no-padding col-md-4">
            &nbsp;&nbsp;&nbsp;
          </div> -->
          <div class="no-padding col-md-4 offset-md-4">
            &nbsp;&nbsp;Audiance: {{ trimto(quiz.meta.audiance, 20) }}
            <br>
            &nbsp;&nbsp;Subject: {{ trimto(quiz.meta.subject, 20) }}
            <br>
            &nbsp;&nbsp;Hints allowed: {{ quiz.settings.numberOfHints }}
            <br>
            &nbsp;&nbsp;Questions: {{ quiz.questions.length }}
            <br>
            &nbsp;&nbsp;{{ date(quiz.meta.created) }}
          </div>
        </div>
      </div>
      <br>
      <QuizPreviewActions v-if="buttons" :quiz="quiz" :canModify="isCurrentUser()">
      </QuizPreviewActions>
    </q-card-section>
  </q-card>
</template>

<script>
import { mapGetters } from 'vuex';
import format from 'date-fns/format';
import QuizPreviewActions from './QuizPreviewActions';

export default {
  name: 'QuizPreview',
  components: {
    QuizPreviewActions,
  },
  props: {
    quiz: { type: Object, required: true },
    buttons: { type: Boolean, required: false, default: true },
  },
  computed: {
    ...mapGetters(['currentUser', 'profile', 'isAuthenticated']),
  },
  methods: {
    isCurrentUser() {
      if (this.currentUser.username && this.quiz.author.username) {
        return this.currentUser.username === this.quiz.author.username;
      }
      return false;
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
