<template>
  <q-list class="no-padding no-margin">
    <q-item class="no-padding no-margin">
      <q-toggle class="no-padding no-margin" v-model="accept">
          I accept the
          <a href="https://www.privacypolicygenerator.info/live.php?token=7W3mWapJbQujo0q5mEfBJYsZk8ryDW2p">
          license and terms</a>
      </q-toggle>
    </q-item>
    <q-card-actions class="no-padding no-margin">
      <q-btn no-caps dense class="actions" outline color="blue-grey-6"
        @click="onSubmit()" type="submit" label="Register">
      </q-btn>
        <q-btn no-caps class="actions" type="button" outline dense color="blue-grey-6"
          @click="onReset()" label="Reset">
        </q-btn>
    </q-card-actions>
  </q-list>
</template>

<script>
import { mapState } from 'vuex';
import { REGISTER } from 'src/store/types/actions.type';

export default {
  name: 'UserRegisterActions',
  props: {
    usernameParent: null,
    emailParent: null,
    passwordParent: null,
  },
  data() {
    return {
      usernameChild: this.usernameParent,
      emailChild: this.emailParent,
      passwordChild: this.passwordParent,
      accept: false,
    };
  },
  watch: {
    usernameParent() {
      this.passwordChild = this.passwordParent;
    },
    emailParent() {
      this.emailChild = this.emailParent;
    },
    passwordParent() {
      this.passwordChild = this.passwordParent;
    },
  },
  methods: {
    onSubmit() {
      if (this.accept !== true) {
        // this.$q.notify({
        //   color: 'red-5',
        //   textColor: 'white',
        //   icon: 'warning',
        //   message: 'You need to accept the license and terms first',
        // });
        this.accept = true;
      } else {
        this.$store
          .dispatch(REGISTER, {
            username: this.usernameChild,
            email: this.emailChild,
            password: this.passwordChild,
          })
          .then(() => this.$router.push({ name: 'home-all' }));
      }
    },
    onReset() {
      this.$emit('update:usernameParent', null);
      this.$emit('update:emailParent', null);
      this.$emit('update:passwordParent', null);
      this.accept = false;
    },
  },
  computed: {
    ...mapState({
      errors: (state) => state.auth.errors,
    }),
  },
};
</script>
