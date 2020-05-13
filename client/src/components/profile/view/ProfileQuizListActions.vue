<template>
  <q-list class="no-padding no-margin">
    <q-card-actions class="no-padding no-margin">
      <q-btn v-if="isCurrentUser()" class="actions" outline color="blue-grey-6"
        no-caps dense type="submit" label="Edit" :to="{ name: 'settings' }">
      </q-btn>
      <div v-else>
        <q-btn to="error" no-caps class="actions" type="button" outline dense color="blue-grey-6"
        v-if="profile.computed.following" @click.prevent="unfollow()" label="Unfollow">
        </q-btn>
        <q-btn to="error" no-caps class="actions" type="button" outline dense color="blue-grey-6"
        v-else @click.prevent="follow()" label="Follow">
        </q-btn>
      </div>
    </q-card-actions>
  </q-list>
</template>

<script>
import { mapGetters } from 'vuex';
import {
  FETCH_PROFILE,
  FETCH_PROFILE_FOLLOW,
  FETCH_PROFILE_UNFOLLOW,
} from 'src/store/types/actions.type';

export default {
  name: 'ProfileQuizListActions',
  mounted() {
    this.$store.dispatch(FETCH_PROFILE, this.$route.params);
  },
  computed: {
    ...mapGetters(['currentUser', 'profile', 'isAuthenticated']),
  },
  methods: {
    isCurrentUser() {
      if (this.currentUser.username && this.profile.username) {
        return this.currentUser.username === this.profile.username;
      }
      return false;
    },
    follow() {
      if (!this.isAuthenticated) return;
      this.$store.dispatch(FETCH_PROFILE_FOLLOW, this.$route.params);
    },
    unfollow() {
      this.$store.dispatch(FETCH_PROFILE_UNFOLLOW, this.$route.params);
    },
  },
  watch: {
    $route(to) {
      this.$store.dispatch(FETCH_PROFILE, to.params);
    },
  },
};
</script>
