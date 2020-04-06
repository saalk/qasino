<template>
  <div id="q-app">
    <MainLayout />
    <!--
    Even when routes usez the same component, treat them
    as distinct and create the component again.
    -->
    <!-- <router-view></router-view> -->
  </div>
</template>

<script>
import MainLayout from 'src/layouts/MainLayout';

export default {
  name: 'App',
  components: {
    MainLayout,
  },
};
</script>
