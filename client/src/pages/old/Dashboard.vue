<template>
  <div class="container">
    <div class="row justify-content-center">
      <div class="col-md-8">
        <div class="card">
          <div class="card-header">
            Dashboard
          </div>
          <div class="card-body">
            <div v-if="user" class="alert alert-success" role="alert">
              You are logged in!
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapGetters } from 'vuex';

export default {
  computed: {
    // map `this.user` to `this.$store.getters.user`
    // import the mapGetters from vuex, this is a helper
    // that maps store getters to local computed properties
    //
    // user is the logged on user
    ...mapGetters({
      user: 'user',
    }),
  },
};
</script>
