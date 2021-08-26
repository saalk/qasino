<template>
  <!-- <q-img  class="absolute-top"
          src="https://cdn.quasar.dev/img/material.png"
          style="height: 140px">   -->
  <q-img  class="absolute-top"
          :src=currentUser.image
          style="height: 140px">
    <div v-if="(currentUser !== null) && (isAuthenticated)"
      class="absolute-bottom bg-transparent">
      <q-btn size="23px" type="button" flat round icon="person_pin" to="settings">
        <q-tooltip class="bg-accent">{{ toolTip }}</q-tooltip>
      </q-btn>
      <div class="tags"> {{ currentUser.username }}
        <hr>
        Device: {{ $q.platform.is.name }}/{{ $q.platform.is.platform }}
      </div>
    </div>
    <div v-else class="absolute-bottom bg-transparent">
      <q-btn @click="onClick()" size="23px" type="button" flat round icon="person_pin">
        <q-tooltip class="bg-accent">{{ toolTip }}</q-tooltip>
      </q-btn>
      <div class="tags">&nbsp;
        <hr>
        Device: {{ $q.platform.is.name }}/{{ $q.platform.is.platform }}
      </div>
    </div>
  </q-img>
</template>

<script>
import { mapGetters } from 'vuex';

export default {
  name: 'DrawerImage',
  data() {
    return {
      toolTip: 'Sign-in',
    };
  },
  computed: {
    ...mapGetters(['currentUser', 'isAuthenticated']),
  },
  watch: {
    isAuthenticated() {
      if (this.isAuthenticated === true) {
        this.toolTip = 'Settings';
      } else {
        this.toolTip = 'Sign-in';
      }
    },
    currentUser() {
      if (this.currentUser !== null) {
        this.toolTip = 'Settings';
      } else {
        this.toolTip = 'Sign-in';
      }
    },
  },
  methods: {
    onClick() {
      if (this.isAuthenticated) {
        this.$router.push({ name: 'settings' });
      } else {
        this.$router.push({ name: 'login' });
      }
    },
  },
};
</script>

<style scoped>
.tagz{
    /* no underlining */
    font-size: 1.5rem;
    padding-left: 1rem;
    padding-bottom: 1rem;
    margin-right: 0.5rem;
}
</style>
