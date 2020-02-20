<template>
  <div>
    <div class="flex wrap gutter">
      <div class="width-1of3 sm-width-1of1">
        <div class="floating-label">
          <input v-model="user.username" required class="full-width">
          <label>Username</label>
        </div>
      </div>
      <div class="width-1of3 sm-width-1of1">
        <div class="floating-label">
          <input v-model="user.phone" required class="full-width">
          <label>Phone</label>
        </div>
      </div>
    </div>
    <div class="flex wrap gutter">
      <div class="width-1of3 sm-width-1of1">
        <div class="floating-label">
          <input v-model="user.website" required class="full-width">
          <label>Website</label>
        </div>
      </div>
      <div class="width-1of3 sm-width-1of1">
        <div class="floating-label">
          <input v-model="user.email" required class="full-width">
          <label>Email</label>
        </div>
      </div>
    </div>
  </div>
</template>

<script type="text/javascript">
export default {
  props: ['user'],
  data() {
    return {};
  },
};
</script>
<style scoped>
  .flex {
    margin-top: 1%;
  }
</style>
