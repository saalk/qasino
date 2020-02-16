<template>
  <nav class="navbar navbar-expand-md navbar-light navbar-laravel">
    <div class="container">
      <router-link to="/" class="navbar-brand">Vue Firebase Auth</router-link>
      <button
        class="navbar-toggler"
        type="button"
        data-toggle="collapse"
        data-target="#navbarSupportedContent"
        aria-controls="navbarSupportedContent"
        aria-expanded="false"
        aria-label
      >
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav mr-auto"></ul>
        <ul class="navbar-nav ml-auto">
            <!-- switch based on whether user is logged on or not --> 
          <template v-if="user.loggedIn">
            <div class="nav-item">{{user.data.displayName}}</div>
            <li class="nav-item">
              <a class="nav-link" @click.prevent="signOut">Sign out</a>
            </li>
          </template>
          <template v-else>
            <li class="nav-item">
              <router-link to="Login" class="nav-link">Login</router-link>
            </li>
            <li class="nav-item">
              <router-link to="Register" class="nav-link">Register</router-link>
            </li>
          </template>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script lang="ts">
import { mapGetters } from "vuex";
import firebase from "firebase";
export default {
  computed: {
    // map `this.user` to `this.$store.getters.user`
    // import the mapGetters from vuex, this is a helper
    // that maps store getters to local computed properties
    //
    // user is the logged on user
    ...mapGetters({
      user: "user"
    })
  },
  methods: {
    signOut() {
      firebase
        // To log out users, we simply call signOut() on firebase.auth()
        .auth()
        .signOut()
        .then(() => {
          this.$router.replace({
            name: "Login"
          });
        });
    }
  }
};
</script>