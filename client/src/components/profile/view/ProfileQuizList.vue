<template>
  <q-page padding>
    <q-card class="no-padding no-margin q-pa-md items-start">
      <q-card-section>
        <div class="text-h6 text-orange-9">
          Profile: {{ trimto(profile.username, 30) }}
        </div>
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
              Bio: {{ trimto(profile.bio, 20) }}
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
import format from 'date-fns/format';
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
  methods: {
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
