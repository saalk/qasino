<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          @click="leftDrawerOpen = !leftDrawerOpen"
          icon="menu"
          aria-label="Menu"
        />
        <q-toolbar-title>Quiz App</q-toolbar-title>
        <div>
          Device: {{ $q.platform.is.name }}/{{ $q.platform.is.platform }}
        </div>
      </q-toolbar>
    </q-header>

    <q-drawer
      v-model="leftDrawerOpen"
      :width="200"
      :breakpoint="500"
      show-if-above
      bordered
      content-class="bg-grey-1"
    >
      <q-img
        class="absolute-top"
        src="https://cdn.quasar.dev/img/material.png"
        style="height: 150px"
      >
        <div class="absolute-bottom bg-transparent">
          <q-avatar color="primary" text-color="white">KM</q-avatar>
          <div class="text-weight-bold">Klaas</div>
        </div>
      </q-img>
      <q-scroll-area
        style="height: calc(100% - 150px); margin-top: 150px; border-right: 1px solid #ddd"
      >
        <q-list padding>
          <q-item-label header class="text-grey-8"
            >Essential Links</q-item-label
          >
          <EssentialLink
            v-for="link in essentialLinks"
            :key="link.title"
            v-bind="link"
          />
        </q-list>
      </q-scroll-area>
      <div class="fixed-bottom text-center">
        <a href="http://quasar-framework.org/">
          <img src="~assets/logo/quasar-logo.png" alt />
        </a>
        <a href="https://vuejs.org/">
          <img src="~assets/logo/vue-logo.png" alt />
        </a>
        <a href="https://www.ing.nl/particulier/betalen/creditcards/index.html">
          <img src="~assets/logo/ing-logo.png" alt />
        </a>
        <a href="https://vuex.vuejs.org/guide/">
          <img src="~assets/logo/vuex-logo.png" alt />
        </a>
        <a href="https://cordova.apache.org/">
          <img src="~assets/logo/cordova-logo.jpg" alt />
        </a>
        <a href="https://capacitor.ionicframework.com/">
          <img src="~assets/logo/capacitator-logo.png" alt />
        </a>
      </div>
    </q-drawer>
    <q-page-container>
      <Quiz></Quiz>
      <!-- <router-view /> -->
    </q-page-container>
  </q-layout>
</template>

<script>
import EssentialLink from 'src/components/EssentialLink';
import Quiz from 'src/pages/Quiz';
// import Index from 'src/pages/Index';
// import { Platform } from 'quasar';

export default {
  name: 'MainLayout',

  components: {
    EssentialLink,
    Quiz,
    // Platform,
  },

  data() {
    return {
      leftDrawerOpen: false,
      essentialLinks: [
        {
          title: 'Blog',
          caption: 'Medium Blog lion',
          icon: 'chat',
          link:
            'https://medium.com/ing-blog/ing-open-sources-lion-a-library-for-performant-accessible-flexible-web-components-22ad165b1d3d',
        },
        {
          title: 'Github ING',
          caption: 'github.com/ing-bank/lion',
          icon: 'code',
          link: 'https://github.com/ing-bank/lion',
        },
        {
          title: 'Storybook',
          caption: 'Lion on netlify',
          icon: 'school',
          link:
            'https://lion-web-components.netlify.com/?path=/story/intro-lion-web-components--page',
        },
        {
          title: 'Services',
          caption: 'ING Customer Services',
          icon: 'record_voice_over',
          link: 'https://www.ing.nl/particulier/klantenservice/index.html',
        },
      ],
    };
  },
};
</script>

<style scoped>
.fixed-bottom {
  margin-bottom: 1%;
  text-align: left;
  padding: 10px;
}
.fixed-bottom a img {
  width: 25px;
  height: 25px;
}
</style>
