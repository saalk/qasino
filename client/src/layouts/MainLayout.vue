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
        <div>Device: {{ $q.platform.is.name }}/{{ $q.platform.is.platform }}</div>
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
          <div>@ing.com</div>
        </div>
      </q-img>
      <q-scroll-area
        style="height: calc(100% - 150px); margin-top: 150px; border-right: 1px solid #ddd"
      >
        <q-list padding>
          <q-item-label header class="text-grey-8">Essential Links</q-item-label>
          <EssentialLink v-for="link in essentialLinks" :key="link.title" v-bind="link" />
        </q-list>
      </q-scroll-area>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
import EssentialLink from 'src/components/EssentialLink';
// import Index from 'src/pages/Index';
// import { Platform } from 'quasar';

export default {
  name: 'MainLayout',

  components: {
    EssentialLink,
    // Index,
    // Platform,
  },

  data() {
    return {
      leftDrawerOpen: false,
      essentialLinks: [
        {
          title: 'Docs',
          caption: 'ING Blog',
          icon: 'school',
          link:
            'https://medium.com/ing-blog/ing-open-sources-lion-a-library-for-performant-accessible-flexible-web-components-22ad165b1d3d',
        },
        {
          title: 'Github',
          caption: 'github.com/ing-bank/lion',
          icon: 'code',
          link: 'https://github.com/ing-bank/lion',
        },
        {
          title: 'Slack Channel',
          caption: 'slack',
          icon: 'chat',
          link:
            'https://polymer.slack.com/?redir=%2Fmessages%2FCJGFWJN9J%2Fconvo%2FCE6D9DN05-1557486154.187100%2F',
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
