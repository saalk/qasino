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
        <q-toolbar-title>
          <router-link class="navbar-brand" :to="{ name: 'home' }">
            Quiz App
          </router-link>
        </q-toolbar-title>
        <q-list v-if="!isAuthenticated">
         <q-btn flat round dense icon="home">
          <q-tooltip content-class="bg-accent">home</q-tooltip>
          <router-link
            active-class="active"
            exact
            :to="{ name: 'home' }"
          >
          </router-link>
          </q-btn>
          <q-btn flat round dense icon="person">
            <q-tooltip content-class="bg-accent">login</q-tooltip>
            <router-link
              active-class="active"
              exact
              :to="{ name: 'login' }"
            >
            </router-link>
          </q-btn>
          <q-btn flat round dense icon="person_add">
            <q-tooltip content-class="bg-accent">register</q-tooltip>
            <router-link
              active-class="active"
              exact
              :to="{ name: 'register' }"
            >
            </router-link>
          </q-btn>
        </q-list>
        <q-list v-else>
          <q-btn flat round dense icon="home">
          <q-tooltip content-class="bg-accent">home</q-tooltip>
            <router-link
              active-class="active"
              exact
              :to="{ name: 'home' }"
            >
            </router-link>
          </q-btn>
          <q-btn flat round dense icon="add">
            <q-tooltip content-class="bg-accent">quiz</q-tooltip>
            <router-link
              active-class="active"
              exact
              :to="{ name: 'quiz-edit' }"
            >
            </router-link>
          </q-btn>
          <q-btn flat round dense icon="settings">
            <q-tooltip content-class="bg-accent">settings</q-tooltip>
            <router-link
              active-class="active"
              exact
              :to="{ name: 'home' }"
            >
            </router-link>
          </q-btn>
         </q-list>
        <!-- <div>
          Device: {{ $q.platform.is.name }}/{{ $q.platform.is.platform }}
        </div> -->
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
        style="height: 100px"
      >
        <div class="absolute-bottom bg-transparent">
        <q-btn flat round dense icon="person_pin">
          <q-tooltip content-class="bg-accent">profile</q-tooltip>
          <router-link
            active-class="active"
            exact
            :to="{ name: 'profile',
            params: { username: currentUser.username }
            }"
          >
          </router-link>
          </q-btn>
          <div class="text-weight-bold"> {{ currentUser.username }}</div>
        </div>
      </q-img>
      <q-scroll-area
        style="height: calc(100% - 150px); margin-top: 100px; border-right: 1px solid #ddd"
      >
        <q-list padding>
          <q-item-label header class="text-grey-8"
            >Essential</q-item-label
          >
            <q-item clickable tag="a" target="_blank" :href="link">
              <q-item-section v-if="icon" avatar>
                <q-icon :name="icon" />
              </q-item-section>
            </q-item>

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
      <QuizFromHome></QuizFromHome>
      <!-- <QuizLayout></QuizLayout> -->
      <!-- <router-view /> -->
    </q-page-container>
  </q-layout>
</template>

<script>
import EssentialLink from 'src/components/EssentialLink';
// import QuizLayout from 'src/layouts/QuizLayout';
import QuizFromHome from 'src/pages/QuizFromHome';
import { mapGetters } from 'vuex';

export default {
  name: 'MainLayout',
  components: {
    EssentialLink,
    // QuizLayout,
    QuizFromHome,
  },
  computed: {
    ...mapGetters(['currentUser', 'isAuthenticated']),
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
.navbar-brand{
    /* no underlining */
    text-decoration: none;
    font-size: 1.5rem;
    padding-top: 0;
    margin-right: 2rem;
    color: #fdfffd;
}
</style>
