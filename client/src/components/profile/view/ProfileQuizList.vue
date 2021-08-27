<template>
  <q-page padding>
    <q-card class="no-padding no-margin q-pa-md items-start">
      <q-card-section>
        <div class="text-h6 text-orange-9">Profile: {{ trimto(30)(profile.username) }}</div>
        <div class="text-grey-6">
          <div class="row">
            <div class="no-padding col-md-4">
              <q-img class="bg-transparent" width="100px" height="100px"
              :src="profile.image">
              </q-img>
            </div>
            <div class="no-padding col-md-4">
              &nbsp;&nbsp;&nbsp;
            </div>
            <div class="no-padding col-md-4 offset-md-4">
              Bio: {{ trimto(20)(profile.bio) }}
            </div>
          </div>
        </div>
        <br>
        <ProfileQuizListActions v-if="showButtons"></ProfileQuizListActions>
        <ul v-if="errors" class="error-messages">
          <li v-for="(v, k) in errors" :key="k">{{ k }} {{ error(v) }}</li>
        </ul>
      </q-card-section>
    </q-card>
    <QuizList :author="this.profile.username" />
  </q-page>
</template>

<script>
import { mapState, mapGetters } from 'vuex';
import { FETCH_PROFILE } from 'src/store/types/actions.type';
import QuizList from 'src/components/quiz/QuizList';
import ProfileQuizListActions from './ProfileQuizListActions';

export default {
  name: 'ProfileQuizList',
  components: {
    ProfileQuizListActions,
    QuizList,
  },
  props: {
    showButtons: { type: Boolean, required: false, default: true },
    author: {
      type: String,
      required: false,
    },
  },
  mounted() {
    this.$store.dispatch(FETCH_PROFILE, this.$route.params);
  },
  computed: {
    ...mapGetters(['profile']),
    ...mapState({
      errors: (state) => state.auth.errors,
    }),
  },
  watch: {
    $route(to) {
      this.$store.dispatch(FETCH_PROFILE, to.params);
    },
  },
};
</script>
