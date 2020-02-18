<template>
  <div>
    <div class="layout-padding">
      <div class="card">
        <div class="card-title bg-teal text-white">
          Search Users
        </div>
        <div class="card-content bg-white">
          <div class="flex">
            <div class="width-2of3 gt-bg-width-1of3 sm-auto">
              <q-autocomplete
                v-model="terms"
                :delay="0"
                :max-results="4"
                @search="search"
                @selected="selected"
              >
                <q-search v-model="terms" placeholder="Search by name" />
              </q-autocomplete>
            </div>
          </div>
          <q-transition name="slide">
            <user-form v-show="selectedUser.name" :user="selectedUser" />
          </q-transition>
        </div>
        <q-transition name="slide">
          <div v-show="selectedUser.name" class="card-actions justify-center flex">
            <div class="width-1of3 sm-width-3of3">
              <button class="primary raised outline fit" @click="save()">
                Save
              </button>
            </div>
            <div class="width-1of3 sm-width-3of3">
              <button
                class="teal raised outline fit"
                @click="$refs.modal.$children[0].open()"
              >
                Check Adress
              </button>
            </div>
          </div>
        </q-transition>
      </div>
    </div>
    <modal-adress ref="modal" :user="selectedUser" />
  </div>
</template>
<script type="text/javascript">
import filter from 'quasar';
import userForm from './userForm.vue';
import modalAdress from './modalAdress.vue';

export default {
  name: 'Form',
  components: {
    userForm,
    modalAdress,
  },
  data() {
    return {
      terms: '',
      users: [],
      selectedUser: { address: {} },
    };
  },
  computed: {
    usersParsed() {
      return this.users.map(user => ({
        allData: user,
        label: user.name,
        secondLabel: user.email,
        value: user.name,
      }));
    },
  },
  mounted() {
    this.getUsers();
  },
  methods: {
    search(terms, done) {
      setTimeout(() => {
        done(filter(terms, { field: 'value', list: this.usersParsed }));
      }, 500);
    },
    getUsers() {
      this.$http.jsonplaceholder.get('users').then(response => {
        this.users = response.data;
      });
    },
    selected(user) {
      this.selectedUser = user.allData;
    },
    save() {
      this.$http.jsonplaceholder.put(
        `users/${this.selectedUser.id}`, this.selectedUser,
      );
      // never used:
      // .then(response => { this.$q.notify.create.positive('Updated successful!'); });
    },
  },
};
</script>
<style>
#map {
  height: 180px;
}
</style>
