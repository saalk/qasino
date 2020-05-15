<template>
  <q-card class="no-padding no-margin q-pa-md items-start">
    <q-card-section>

      <div class="text-h6 text-orange-9">Title: {{ quiz.meta.title | trimto(30) }}</div>
      <div class="no-margin text-h7 q-mt-sm q-mb-xs">{{ quiz.meta.description | trimto(55) }}</div>
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
            &nbsp;&nbsp;Audiance: {{ quiz.meta.audiance | trimto(20) }}
            <br>
            &nbsp;&nbsp;Subject: {{ quiz.meta.subject | trimto(20) }}
            <br>
            &nbsp;&nbsp;Questions: {{ quiz.questions.length }}
            <br>
            <br>
            &nbsp;&nbsp;{{ quiz.meta.createdAt | date }}
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
  },
};
</script>
