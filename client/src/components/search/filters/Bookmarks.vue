<template>
  <q-toggle
    dense filled style="width: 230px"
    :value="value"
    @input="$emit('input', $event)"
    v-model="trueFalse"
    label="Bookmarks"
    class="select-component"
  />
</template>

<script>
export default {
  data() {
    return {
      trueFalse: true,
    };
  },
  props: ['value'],
  computed: {
    options() {
      return this.trueFalse;
    },
  },
};
</script>

<style lang="scss" scoped>
// .select-component {
//   background-color: white;
//   margin-top: -5px;
// }
</style>
