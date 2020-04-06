<template>
  <q-toolbar>

    <q-btn flat dense round icon="menu" aria-label="Menu" @click="toggleLeft">
    </q-btn>
    <q-toolbar-title>
      <router-link class="navbar-brand" :to="{ name: 'home-all' }">
      Lion quiz
      </router-link>
    </q-toolbar-title>

    <q-list v-if="!isAuthenticated">
      <q-btn :to="{ name: 'home-all' }" flat round dense icon="las la-home">
        <q-tooltip content-class="bg-accent">home</q-tooltip>
      </q-btn>
      <q-btn :to="{ name: 'login' }"  flat round dense icon="las la-sign-in-alt">
        <q-tooltip content-class="bg-accent">sign-in</q-tooltip>
      </q-btn>
      <q-btn :to="{ name: 'register' }"  flat round dense icon="las la-user-plus">
       <q-tooltip content-class="bg-accent">register</q-tooltip>
      </q-btn>
    </q-list>

    <q-list v-else>
      <q-btn :to="{ name: 'home-all' }"  flat round dense icon="las la-home">
       <q-tooltip content-class="bg-accent">home</q-tooltip>
      </q-btn>
      <q-btn :to="{ name: 'quiz-edit', params: { quizId: 0 } }"
        flat round dense icon="las la-plus-square">
        <q-tooltip content-class="bg-accent">new quiz</q-tooltip>
      </q-btn>
      <q-btn :to="{ name: 'settings' }"  flat round dense icon="las la-cog">
        <q-tooltip content-class="bg-accent">settings</q-tooltip>
      </q-btn>
      <q-btn @click="logout()" flat round dense icon="las la-sign-out-alt">
        <q-tooltip content-class="bg-accent">logout</q-tooltip>
      </q-btn>
    </q-list>

  </q-toolbar>
</template>

<script>
import { mapGetters } from 'vuex';
import { LOGOUT } from 'src/store/types/actions.type';

export default {
  name: 'HeaderToolbar',
  props: {
    parentLeft: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      childLeft: {
        type: Boolean,
        default: false,
      },
    };
  },
  mounted() {
    this.loadData();
  },
  computed: {
    ...mapGetters(['isAuthenticated']),
  },
  methods: {
    loadData() {
      this.childLeft = this.parentLeft;
    },
    toggleLeft() {
      this.$emit('interface', !this.childLeft);
    },
    logout() {
      this.$store.dispatch(LOGOUT).then(() => {
        if (this.$route.name !== 'home-all') {
          this.$router.push({ name: 'home-all' });
        }
      });
    },
  },
};
</script>

<style scoped>
.navbar-brand{
    /* no underlining */
    text-decoration: none;
    font-size: 1.5rem;
    padding-top: 0;
    margin-right: 1rem;
    color: #fdfffd;
}
</style>
