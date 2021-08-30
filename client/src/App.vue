<template>
    <MainLayout />
    <!--
    Even when routes usez the same component, treat them
    as distinct and create the component again.
    -->
    <!-- <router-view></router-view> -->
</template>

<script>
// no main.js needed for quasar since the quasar.config.js generates
// the needed js files for importing Vue, App, router, store and css
// and creating a Vue instance and mounting them to index.html

import MainLayout from 'src/layouts/MainLayout';

// don't use new Vue here since this is not the root Vue instance
// just make an App.js component which can be registered and reused later

// the App.js is imported in the generated js files
export default {
  name: 'App',
  components: {
    MainLayout,
  },
  // preFetch hook runs only once
  // preFetch ({ store }) {
  //   // initialize something in store here
  // }
};
</script>
