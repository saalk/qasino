function load(component) {
  return () => import(`src/components/${component}.vue`);
}
const routes = [

  {
    path: '/',
    component: () => import('~../../../src/layouts/MainLayout.vue'),
    children: [
      // { path: '/auth', component: () =>
      // import('~../../../src/pages/Auth'), meta: { name: 'Form Login' } },
      // { path: '/success', component: () =>
      // import('~/../../src/pages/AuthSuccess'), meta: { name: 'Form Profile' } },
      { path: '/dashboard', component: load('views/dashboard/one/dashboard'), meta: { name: 'Dashboard One' } },
      { path: '/form', component: load('views/form/simpleForm/simpleForm'), meta: { name: 'Form find / edit' } },
      { path: '/embeeded', component: load('views/form/embeeded/embeeded'), meta: { name: 'Embeeded Validations' } },
      { path: '/advanced-form-one', component: load('views/form/advancedFormOne/advancedFormOne'), meta: { name: 'Advanced Form One' } },
      { path: '/login-one', component: load('views/login/login-one'), meta: { name: 'Login One' } },
      { path: '/pricing', component: load('views/pricing/pricing'), meta: { name: 'Pricing' } },
      { path: '/drag-and-drop', component: load('views/dragAndDrop/dragAndDrop'), meta: { name: 'Drag and Drop' } },
      { path: '/server-side-data-table', component: load('views/serverSideDataTable/serverSideDataTable'), meta: { name: 'Server Side Data Table' } },
      { path: '*', component: () => import('~../../../src/pages/Error404') }, // Not found
    ],
  },
  { path: '/auth', component: () => import('~../../../src/pages/Auth'), meta: { name: 'Form Login' } },
  { path: '/success', component: () => import('~/../../src/pages/AuthSuccess'), meta: { name: 'Form Profile' } },
];

// Always leave this as last one
if (process.env.MODE !== 'ssr') {
  routes.push({
    path: '*',
    component: () => import('src/pages/Error404.vue'),
  });
}

export default routes;
