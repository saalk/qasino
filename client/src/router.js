import Auth from "@okta/okta-vue";
import Vue from 'vue'
import Router from 'vue-router'
import Users from './components/Users'

Vue.use(Auth, {
  issuer: 'https://dev-341668.okta.com/oauth2/default',
  client_id: '0oa2cvcr95Wo6NTfK357',
  redirect_uri: 'http://localhost:8080/implicit/callback',
  scope: 'openid profile email'
});

Vue.use(Router);

let router = new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'Users',
      component: Users,
      meta: {
        requiresAuth: false // TODO test with true
      }
    },
    {
      path: '/implicit/callback',
      component: Auth.handleCallback(),
    },
  ]
});

router.beforeEach(Vue.prototype.$auth.authRedirectGuard());

export default router;
