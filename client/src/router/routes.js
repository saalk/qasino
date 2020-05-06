const routes = [
  {
    path: '/',
    component: () => import('src/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'home-all',
        component: () => import('src/components/home/HomeAll'),
      },
      {
        path: 'follow',
        name: 'home-follow',
        component: () => import('src/components/home/HomeFollowing'),
      },
      {
        path: 'favorited',
        name: 'home-favorite',
        component: () => import('src/components/home/HomeFavorited'),
      },
      {
        path: 'tag/:tag',
        name: 'home-tag',
        component: () => import('src/components/home/HomeTag'),
      },
      {
        path: 'search',
        name: 'home-new',
        component: () => import('src/components/search/SearchQuizList'),
      },
    ],
  },
  {
    name: 'login',
    path: '/login',
    component: () => import('src/components/user/UserLogin'),
  },
  {
    name: 'register',
    path: '/register',
    component: () => import('src/components/user/UserRegister'),
  },
  {
    name: 'settings',
    path: '/settings',
    component: () => import('src/components/user/UserSettings'),
  },
  // Handle child routes with a default, by giving the name to the
  // child.
  // SO: https://github.com/vuejs/vue-router/issues/777
  {
    path: '/:username',
    component: () => import('src/components/profile/ProfileUser'),
    children: [
      {
        path: '',
        name: 'profile-user',
        component: () => import('src/components/profile/ProfileUser'),
      },
      {
        path: 'author',
        name: 'profile-author',
        component: () => import('src/components/profile/ProfileAuthor'),
      },
    ],
  },
  {
    name: 'quiz-play',
    path: '/play/:quizId',
    component: () => import('src/components/quiz/QuizPlay'),
    props: true,
  },
  {
    name: 'quiz-edit',
    path: '/editor/:quizId?',
    props: true,
    component: () => import('src/components/quiz/QuizEdit'),
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
