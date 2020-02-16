/* 
 * lazy loading is solely inside "export default ["
 * - component: () => import('layouts/user'),
 * normal loading is
 * - import UserLayout from 'layouts/user'
 * and inside "export default ["
 * - component: UserLayout,
 * 
 * Quasar uses Webpack aliases eg. ‘layouts’ which points to ‘/src/layouts’
 * 
 */
import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

function load(component) {
  return () => import(`components/${component}.vue`)
}

export default new VueRouter({
  routes: [

    { path: '/', component: load('views/dashboard/one/dashboard'), meta: { name: 'Dashboard One', permission: 'any', fail: 'pages/Error404.vue' } },
    { path: '/auth', component: load('Auth'), meta: { name: 'Form Login' } },
    { path: '/success', component: load('AuthSuccess'), meta: { name: 'Form Profile' } },
    { path: '/form', component: load('views/form/simpleForm/simpleForm'), meta: { name: 'Form find / edit' } },
    { path: '/embeeded', component: load('views/form/embeeded/embeeded'), meta: { name: 'Embeeded Validations' } },
    { path: '/advanced-form-one', component: load('views/form/advancedFormOne/advancedFormOne'), meta: { name: 'Advanced Form One' } },
    { path: '/login-one', component: load('views/login/login-one'), meta: { name: 'Login One' } },
    { path: '/pricing', component: load('views/pricing/pricing'), meta: { name: 'Pricing' } },
    { path: '/drag-and-drop', component: load('views/dragAndDrop/dragAndDrop'), meta: { name: 'Drag and Drop' } },
    { path: '/server-side-data-table', component: load('views/serverSideDataTable/serverSideDataTable'), meta: { name: 'Server Side Data Table' } },
    { path: '*', component: load('Error404') } // Not found
  ],
  component: () => import('layouts/MainLayout.vue'),
  children: [
    { path: '', component: () => import('pages/Index.vue') }
  ]
})
// Always leave this as last one
if (process.env.MODE !== 'ssr') {
  routes.push({
    path: '*',
    component: () => import('pages/Error404.vue')
  })
}