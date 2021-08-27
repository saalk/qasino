<template>
  <q-page class="quiz">
      <!-- <div class="q-pa-md" style="max-width: 300px"> -->
      <div class="q-pa-md">
        <div id="animationSandbox" style="display: block;" class>
          <h1 class="site__title mega">Your Settings</h1>
        </div>
        <br>
        <q-form submit="onSubmit">
          <q-input filled type="text" v-model="currentUserModify.image"
          label="URL of profile picture"
          lazy-rules
            :rules="[ val => val && val.length > 0 || 'Please type something']"/>
          <q-input @keydown.native.space.prevent filled type="text"
          v-model="currentUserModify.username" label="Your username (no spaces)"
          autocomplete="on" lazy-rules
            :rules="[ val => val && val.length > 0 || 'Please type something']"/>
          <q-input filled type="textarea" rows="3"
            v-model="currentUserModify.bio" label="Short bio about you"
            lazy-rules :rules="[ val => val && val.length > 0 || 'Please type something']"/>
          <q-input filled type="text" v-model="currentUserModify.email" label="Email"
          lazy-rules
            :rules="[ val => val && val.length > 0 || 'Please type something']"/>
          <q-input v-model="currentUserModify.password" filled :type="isPwd ? 'password' : 'text'"
            class="no padding" lazy-rules label="Password with toggle" autocomplete="on"
            :rules="[ val => val && val.length > 0 || 'Password may not be empty']">
            <template v-slot:append>
              <q-icon
                :name="isPwd ? 'visibility_off' : 'visibility'"
                class="cursor-pointer"
                @click="isPwd = !isPwd"
              />
            </template>
          </q-input>
          <UserSettingsActions v-if="showButtons" v-model:currentUserParent="currentUserModify">
          </UserSettingsActions>
        </q-form>
      </div>
      <br>
      <ul v-if="errors" class="error-messages">
        <li v-for="(v, k) in errors" :key="k">{{ k }} {{ error(v) }}</li>
      </ul>
    </q-page>
</template>

<script>
import { mapState, mapGetters } from 'vuex';
import UserSettingsActions from './UserSettingsActions';

export default {
  name: 'Settings',
  components: {
    UserSettingsActions,
  },
  props: {
    showButtons: { type: Boolean, required: false, default: true },
  },
  data() {
    return {
      currentUserModify: Object,
      isPwd: true,
    };
  },
  mounted() {
    this.loadData();
  },
  computed: {
    ...mapState({
      errors: (state) => state.auth.errors,
    }),
    ...mapGetters(['currentUser']),
  },
  methods: {
    loadData() {
      this.currentUserModify = this.currentUser;
    },
  },
};
</script>
