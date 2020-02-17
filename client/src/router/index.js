import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router)

function load(component) {
    // replace all the - import Login from '../components/Login'
    return () => import(`components/${component}.vue`)
}

const router = new Router({
    //mode: 'history',
    routes: [
        { path: '/login', component: load('Login'), meta: { name: 'Login' } },
        { path: '/register', component: load('Register'), meta: { name: 'Register' } },
        { path: '/dashboard', component: load('Dashboard'), meta: { name: 'Dashboard' } },
        
        { path: '/', component: load('views/dashboard/one/dashboard'), meta: { name: 'Dashboard One' } },
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
    ]
});

export default router