import * as firebase from 'firebase/app';
import 'firebase/firestore';
import 'firebase/auth';
import 'firebase/analytics';
import router from '../router/index';

import firebaseConfig from '../../firebase.conf';
// a store keeps states eg. is user loggedIn
import store from '../store/index';
// import router from '../router/index';

export default ({ Vue }) => {
  // Initialize firebase from settings
  firebase.initializeApp(firebaseConfig);
  firebase.analytics();
  firebase.auth().onAuthStateChanged((user) => {
    if (user) {
      store.state.user = firebase.auth().currentUser;
      router.push('/success');
    }
    else {
      store.state.user = null;
      if (router.path !== '/auth') {
        router.push('/auth');
      }
    }
  });
  Vue.prototype.$firebase = firebase;
  // ^ ^ ^ this will allow you to use this.$  Vue.prototype.$firebase = firebase
  //       so you won't necessarily have to import firebase in each vue file
};
