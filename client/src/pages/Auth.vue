<template lang="html">
  <div id="firebaseui-auth-container" />
</template>

<script>
// import firebase from 'firebase'
// https://firebase.google.com/docs/auth/web/firebaseui
import * as firebaseui from 'firebaseui';

export default {
  name: 'Auth',
  mounted() {
    const uiConfig = {
      signInSuccessUrl: '/success',
      signInOptions: [
        // firebase.auth.GoogleAuthProvider.PROVIDER_ID,
        this.$firebase.auth.EmailAuthProvider.PROVIDER_ID,
        // firebase.auth.GithubAuthProvider.PROVIDER_ID
      ],
    };
    // Initialize the FirebaseUI Widget using Firebase.
    const ui = new firebaseui.auth.AuthUI(this.$firebase.auth());
    ui.start('#firebaseui-auth-container', uiConfig);
  },
};
</script>

<style lang="css">

</style>
