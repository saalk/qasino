<template>
  <div id="q-app" >
    <div v-if="getMobileMode">
      <div  id="android-preview" class="platform-android mat-only">
        <iframe id="android-iframe" frameborder="0" src="https://quasar-admin.firebaseapp.com/android/#/"></iframe>
      </div>
      <div  id="ios-preview" class="platform-ios ios-only">
        <iframe id="ios-iframe" frameborder="0" src="https://quasar-admin.firebaseapp.com/ios/#/"></iframe>
      </div>
    </div>
    <q-layout :style="{ backgroundColor: backgroundColor}" v-else>
      <toolbar slot="header"></toolbar>
      <drawer></drawer>
      <navbar></navbar>
      <div class="layout-view">
        <content-header></content-header>
        <transition name="fade" mode="out-in">
          <router-view></router-view>
        </transition>
      </div>
    </q-layout>
  </div>
</template>

<script>
// register all components here
import Navbar from "./components/Navbar";
import contentHeader from './components/layout/content-header.vue'

export default {
  name: "app",
  components: {
    // register the navbar as component
    'navbar': Navbar,
    'contentHeader': contentHeader
  }
};
</script>

