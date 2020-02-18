import Firebase from 'firebase/app'
import 'firebase/firestore' // eslint-disable-line
import firebaseConfig from '../../firebase.conf'
// a store keeps states eg. is user loggedIn
import store from '../store/index'
import router from '../router/index'

export default ({ Vue }) => {
  // Initialize Firebase from settings
  Firebase.initializeApp(firebaseConfig);
  Firebase.analytics();
  Firebase.auth().onAuthStateChanged((user) => {
    if (user) {
      store.state.user = Firebase.auth().currentUser;
      //router.push('/success');
    }
    else {
      store.state.user = null
      if (router.path !== '/auth') {
        router.push('/auth');
      }
    }
  })
  Vue.prototype.$firebase = Firebase
}
