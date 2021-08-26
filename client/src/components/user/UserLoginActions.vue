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
      <q-btn
        class="actions text-h7 text-orange-9"
        dense no-caps color="white"
        @click="onSubmit()" type="submit" label="Submit">
      </q-btn>
      <q-btn
        class="actions text-h7 text-orange-9"
        dense no-caps color="white"
        @click="onReset()" label="Reset">
      </q-btn>
      <q-btn
        class="actions text-h7 text-orange-9"
        dense no-caps color="white"
        @click="onRegister()" label="Register">
      </q-btn>
    </q-card-actions>
  </q-list >
</template>

<script>
import { mapState } from 'vuex';
import { LOGIN } from 'src/store/types/actions.type';

export default {
  name: 'UserLoginActions',
  props: {
    emailParent: null,
    passwordParent: null,
  },
  data() {
    return {
      emailChild: this.emailParent,
      passwordChild: this.passwordParent,
      accept: false,
    };
  },
  watch: {
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
          .dispatch(LOGIN, {
            email: this.emailChild,
            password: this.passwordChild,
          })
          .then(() => this.$router.push({ name: 'home-all' }));
      }
    },
    onReset() {
      this.$emit('update:emailParent', null);
      this.$emit('update:passwordParent', null);
      this.accept = false;
    },
    onRegister() {
      this.$router.push({ name: 'register' });
    },
  },
  computed: {
    ...mapState({
      errors: (state) => state.auth.errors,
    }),
  },
};
</script>
