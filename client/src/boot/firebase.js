import * as firebase from 'firebase/app';
import 'firebase/firestore';
import 'firebase/auth';
import 'firebase/analytics';
// import router from '../router/index';

import firebaseConfig from '../../firebase.conf';
// a store keeps states eg. is user loggedIn
// import store from '../store/index';
// import router from '../router/index';

export default ({ Vue }) => {
  // Initialize Firebase from settings
  firebase.initializeApp(firebaseConfig);
  Vue.prototype.$firebase = firebase;
};
