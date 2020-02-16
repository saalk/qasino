import Vue from "vue";
import App from "./App.vue";
import router from "./router/index";
import * as firebase from "firebase";
// a store keeps states eg. is user loggedIn
import store from "./store/store";

Vue.config.productionTip = false;

const configOptions = {
  apiKey: "AIzaSyD172twA-UVtThYDVIX7R_A6VK3PVO4_4U",
  authDomain: "cloudqasino.firebaseapp.com",
  databaseURL: "https://cloudqasino.firebaseio.com",
  projectId: "cloudqasino",
  storageBucket: "cloudqasino.appspot.com",
  messagingSenderId: "98507201126",
  appId: "1:98507201126:web:ad1f14beed0b0148282ba7",
  measurementId: "G-N1G3X0N16S"
};
// Initialize Firebase
firebase.initializeApp(configOptions);
firebase.analytics();
// dispatch the fetchUser() action on the Firebase observer
firebase.auth().onAuthStateChanged(user => {
  store.dispatch("fetchUser", user);
});


// <!-- The core Firebase JS SDK is always required and must be listed first -->
// <script src="https://www.gstatic.com/firebasejs/7.8.2/firebase-app.js"></script>

// <!-- TODO: Add SDKs for Firebase products that you want to use
//      https://firebase.google.com/docs/web/setup#available-libraries -->
// <script src="https://www.gstatic.com/firebasejs/7.8.2/firebase-analytics.js"></script>


new Vue({

  router,
  store,
  render: h => h(App)
  
}).$mount("#app");