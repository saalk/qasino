const routes = [
  {
    path: '/',
    component: () => import('src/pages/QuizFromHome.vue'),
    // component: () => import("src/layouts/Home"),

    // children: [
    //   { path: '', component: () => import('src/pages/Quiz.vue') },
    // ],
    children: [
      {
        path: '',
        name: 'home',
        component: () => import('src/components/real/view/HomeGlobal'),
      },
      {
        path: 'my-feed',
        name: 'home-my-feed',
        component: () => import('src/components/real/view/HomeMyFeed'),
      },
      {
        path: 'tag/:tag',
        name: 'home-tag',
        component: () => import('src/components/real/view/HomeTag'),
      },
    ],
  },
  {
    name: 'login',
    path: '/login',
    component: () => import('src/components/real/view/Login'),
  },
  {
    name: 'register',
    path: '/register',
    component: () => import('src/components/real/view/Register'),
  },
  {
    name: 'settings',
    path: '/settings',
    component: () => import('src/components/real/view/Settings'),
  },
  // Handle child routes with a default, by giving the name to the
  // child.
  // SO: https://github.com/vuejs/vue-router/issues/777
  {
    path: '/@:username',
    component: () => import('src/components/real/view/Profile'),
    children: [
      {
        path: '',
        name: 'profile',
        component: () => import('src/components/real/view/ProfileQuizzes'),
      },
      {
        name: 'profile-favorites',
        path: 'favorites',
        component: () => import('src/components/real/view/ProfileFavorited'),
      },
    ],
  },
  {
    name: 'quiz',
    path: '/quizzes/:id',
    component: () => import('src/components/real/view/Quiz'),
    props: true,
  },
  {
    name: 'quiz-edit',
    path: '/editor/:id?',
    props: true,
    component: () => import('src/components/real/view/QuizEdit'),
  },
];

// Always leave this as last one
if (process.env.MODE !== 'ssr') {
  routes.push({
    path: '*',
    component: () => import('src/pages/Error404.vue'),
  });
}

export default routes;
