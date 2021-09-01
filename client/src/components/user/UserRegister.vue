<template>
  <q-page class="quiz">
    <!-- <div class="q-pa-md" style="max-width: 300px"> -->
    <div class="q-pa-md">
      <div id="animationSandbox" style="display: block;">
        <h1 class="site__title mega">Register</h1>
      </div>
      <br>
      <router-link :to="{ name: 'login' }">
        Already have an account
      </router-link>
      <br>
      <br>
      <br>
      <q-form submit="onSubmit">
        <q-input filled type="text" v-model="username" label="Username"
        lazy-rules
          :rules="[ val => val && val.length > 0 || 'Please type something']"/>
        <q-input filled type="text" v-model="email" label="Email"
        lazy-rules
          :rules="[ val => val && val.length > 0 || 'Please type something']"/>
        <q-input class="no padding" filled type="password"
        v-model="password" label="Password"/>
        <UserRegisterActions v-if="showButtons"
          v-model:usernamearent="username"
          v-model:emailParent="email"
          v-model:passwordParent="password">
        </UserRegisterActions>
      </q-form>
    </div>
    <br>
    <ul v-if="errors" class="error-messages">
      <li v-for="(v, k) in errors" :key="k">{{ k }} {{ $filters.error(v) }}</li>
    </ul>
  </q-page>
</template>

<script>
import { mapState } from 'vuex';
// import { REGISTER } from 'src/store/types/actions.type';
import UserRegisterActions from './UserRegisterActions';

export default {
  name: 'Register',
  components: {
    UserRegisterActions,
  },
  props: {
    showButtons: { type: Boolean, required: false, default: true },
  },
  data() {
    return {
      username: '',
      email: '',
      password: '',
    };
  },
  computed: {
    ...mapState({
      errors: (state) => state.auth.errors,
    }),
  },
  methods: {
    // onSubmit() {
    //   this.$store
    //     .dispatch(REGISTER, {
    //       email: this.email,
    //       password: this.password,
    //       username: this.username,
    //     })
    //     .then(() => this.$router.push({ name: 'home-all' }));
    // },
  },
};
</script>
