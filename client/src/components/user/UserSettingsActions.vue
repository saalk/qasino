<template>
  <q-list class="no-padding no-margin">
    <q-item class="no-padding no-margin">
      <q-toggle v-model="accept">
        I accept the&nbsp;
        <a href="https://www.privacypolicygenerator.info/live.php?token=7W3mWapJbQujo0q5mEfBJYsZk8ryDW2p">
        license and terms</a>
      </q-toggle>
    </q-item>
    <q-card-actions class="no-padding no-margin">

      <q-btn no-caps class="actions" outline dense color="blue-grey-6"
        @click="onSubmit()" type="submit" label="Update">
      </q-btn>
      <q-btn no-caps class="actions" type="button" outline dense color="blue-grey-6"
        @click="onReset()" label="Reset">
      </q-btn>
      <q-btn v-if="isAuthenticated" no-caps @click="logout()" type="button" label="Logout"
      outline dense color="blue-grey-6"></q-btn>
    </q-card-actions>
  </q-list>
</template>

<script>
import { mapGetters } from 'vuex';
import { LOGOUT, UPDATE_USER } from 'src/store/types/actions.type';

export default {
  name: 'UserSettingsActions',
  props: {
    currentUserParent: null,
  },
  data() {
    return {
      accept: false,
      currentUserChild: null,
    };
  },
  watch: {
    currentUserParent() {
      this.currentUserChild = this.currentUserParent;
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
          .dispatch(UPDATE_USER, this.currentUserChild)
          .then(() => this.$router.push({ name: 'home-all' }));
      }
    },
    logout() {
      this.$store.dispatch(LOGOUT).then(() => {
        this.$router.push({ name: 'home-all' });
      });
    },
    // no setter available :
    onReset() {
      this.$emit('update:currentUserParent', null);
      this.accept = false;
    },
  },
  computed: {
    ...mapGetters(['isAuthenticated']),
  },
};
</script>
