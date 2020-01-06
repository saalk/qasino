import Vue from 'vue'
import Auth from "@okta/okta-vue";
import VueRouter from 'vue-router'

import LatestMovie from '@/components/LatestMovie'
import Movie from '@/components/Movie'
import SearchMovie from '@/components/SearchMovie'

/*
Vue.use(Auth, {
  issuer: 'https://dev-341668.okta.com/oauth2/default',
  client_id: '0oa2cvcr95Wo6NTfK357',
  redirect_uri: 'http://localhost:8080/implicit/callback',
  scope: 'openid profile email'
});
*/
Vue.use(VueRouter)

export default new VueRouter({
  routes: [
    {
    path: '/implicit/callback',
    component: Auth.handleCallback(),
    },
    {
      path: '/',
      name: 'LatestMovie',
      component: LatestMovie,
      meta: {
        requiresAuth: false // TODO test with true
      }
    },
    {
      path: '/movie/:id',
      name: 'Movie',
      props: true,
      component: Movie
    },
    {
      path: '/search/:name',
      name: 'SearchMovie',
      props: true,
      component: SearchMovie
    }
  ],
  mode: 'history'
})
