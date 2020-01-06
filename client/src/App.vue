<template>
<ion-app>
    <v-app>

      <TopToolbar></TopToolbar>
      <BottomNav></BottomNav>

      <v-toolbar app>

        <v-toolbar-title class="headline text-uppercase">
          <router-link
          to='/'
          tag='span'
          style='cursor: pointer'>
          Karina's Filmzoeker
          </router-link>
        </v-toolbar-title>

        <v-spacer></v-spacer>

        <v-flex xs12 sm6 md3>
          <v-text-field
            label='Movie Name'
            v-model='searchString'
            >
          </v-text-field>
          </v-flex>
        <v-btn
          flat
          :disabled="!dataAvailable"
          @click="searchMovie"
        >

         <span class="mr-2">Search</span>
        </v-btn>
      </v-toolbar>

      <v-content>
      <router-view/>
      </v-content>

    </v-app>
    </ion-app>
</template>

<script>

export default {
  name: 'App',
  components: {
  },
  data () {
    return {
      searchString: ''
    }
  },
  methods: {
    searchMovie () {
      this.$router.push('/search/' + this.searchString)
      this.searchString = ''
    }
  },
  computed: {
    dataAvailable () {
      return this.searchString !== null && this.searchString !== ''
    }
  }
}
</script>

<style>

  [v-cloak] { display: none; }

</style>
<!--
          <footer class="info">
            <p v-if="activeUser" class="logout-link"><a @click="handleLogout" href="#">Logout</a>
            </p>
            <p> &copy; 2020 Saalk@knikit.nl - trying to beat
            <a href="https://medium.com/@uwehook/sturgeons-law-285c007406db">sturgeon's law</a></p>
          </footer>
-->
<!--    async created () {
      await this.refreshActiveUser()
    },

    watch: {
      '$route': 'refreshActiveUser'
    },

    methods: {
      async refreshActiveUser () {
        this.activeUser = await this.$auth.getUser()
        this.$log.debug('activeUser',this.activeUser)
      },

      async handleLogout () {
        await this.$auth.logout()
        await this.refreshActiveUser()
        this.$router.go('/')
      }
    },
-->