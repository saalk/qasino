import Firebase from 'firebase/app'
import 'firebase/firestore' // eslint-disable-line
import firebaseConfig from '../../firebase.conf'
// a store keeps states eg. is user loggedIn
import store from "../store/index";

export default ({ Vue }) => {
  // Initialize Firebase from settings
  Firebase.initializeApp(firebaseConfig);
  Firebase.analytics();
  // Firebase.auth().onAuthStateChanged(user => {
  //   store.dispatch("fetchUser", user);
  // });

  Vue.prototype.$firebase = Firebase
}