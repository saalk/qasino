<template>
  <q-page class="quiz">
    <!-- <div class="q-pa-md" style="max-width: 300px"> -->
    <div class="q-pa-md">
      <div id="animationSandbox" style="display: block;" class>
        <h1 class="site__title mega">Sign in</h1>
      </div>
      <br>
      <q-form submit="onSubmit">
        <q-input filled type="text" v-model="email" label="Email"
        autocomplete="on" lazy-rules
          :rules="[ val => val && val.length > 0 || 'Please type something']"/>
        <q-input v-model="password" filled :type="isPwd ? 'password' : 'text'"
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
        <UserLoginActions v-if="showButtons"
        v-model:emailParent="email" v-model:passwordParent="password"></UserLoginActions>
      </q-form>
    </div>
    <br>
    <ul v-if="errors" class="error-messages">
      <li v-for="(value, key) in errors" :key="key">
        {{ key }} {{ error(value) }}
      </li>
    </ul>
  </q-page>
</template>

<script>
import { mapState } from 'vuex';
import UserLoginActions from './UserLoginActions';

export default {
  name: 'Login',
  components: {
    UserLoginActions,
  },
  props: {
    showButtons: { type: Boolean, required: false, default: true },
  },
  data() {
    return {
      email: '',
      password: '',
      isPwd: true,
    };
  },
  computed: {
    ...mapState({
      errors: (state) => state.auth.errors,
    }),
  },
};
</script>
