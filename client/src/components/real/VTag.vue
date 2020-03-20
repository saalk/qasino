<template>
  <router-link :to="homeRoute" :class="className" v-text="name"></router-link>
</template>

<script>
export default {
  name: 'RwvTag',
  props: {
    name: {
      type: String,
      required: true,
    },
    className: {
      type: String,
      default: 'tag-pill tag-default',
    },
  },
  computed: {
    // eslint-disable-next-line no-restricted-globals
    homeRoute: () => ({ name: 'home-tag', params: { tag: name } }),
  },
};
</script>
