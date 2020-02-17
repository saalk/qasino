import Vue from "vue";
import Vuex from "vuex";

// import example from './module-example

Vue.use(Vuex);

/*
 * If not building with SSR mode, you can
 * directly export the Store instantiation;
 *
 * The function below can be async too; either use
 * async/await or return a Promise which resolves
 * with the Store instance.
 */

export default new Vuex.Store({
    state: {
        user: {
            // is users logged in or not
            // default is false
            loggedIn: false,
            // info about logged in user
            data: null
        },
        layoutNeeded: true,
        isLoginPage: false,
        mobileMode: false,
        menuCollapse: true,

        posts: []
    },
    getters: {
        user(state) {
            return state.user
        }
        ,
        getLayoutNeeded(state) {
            return state.layoutNeeded
        },
        getIsLoginPage(state) {
            return state.isLoginPage
        },
        getMobileMode(state) {
            return state.mobileMode
        },
        getMenuCollapse(state) {
            return state.menuCollapse
        },
        getPosts(state) {
            return state.posts
        }
    },
    // make changes to the state
    mutations: {
        // SET_LOGGED_IN(state, value) {
        //     state.user.loggedIn = value;
        // },
        // SET_USER(state, data) {
        //     state.user.data = data;
        // }
        // ,
      
        setLayoutNeeded(state, value) {
            state.layoutNeeded = value
        },
        setIsLoginPage(state, value) {
            state.isLoginPage = value
        },
        setMobileMode(state, value) {
            state.mobileMode = value
        },
        setMenuCollapse(state, value) {
            state.menuCollapse = value
        },
        setPosts(state, posts) {
            state.posts = posts
        }
    },
    actions: {
        // use the fetchUser in the firebase onAuthStateChanged() observer
        // 1 - configure first in main.js
        //   - a store keeps states eg. is user loggedIn
        // import * as firebase from "firebase";
        // import store from "./store/store";
        // firebase.auth().onAuthStateChanged(user => {
        //   store.dispatch("fetchUser", user);
        // });
        // 2 - then you can do:
        // firebase.auth().onAuthStateChanged(user =>  {
        //     if (user) {
        //       // User is signed in.
        //     } else {
        //       // No user is signed in.
        //     }
        //   });
        fetchUser({ commit }, user) {
            commit("SET_LOGGED_IN", user !== null);
            if (user) {
                commit("SET_USER", {
                    displayName: user.displayName,
                    email: user.email
                });
            } else {
                commit("SET_USER", null);
            }
        }
    }
});