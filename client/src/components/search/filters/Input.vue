<template>
  <input
    type="text"
    v-model="model"
    placeholder="minimal 3 letters"
  >
</template>

<script>
export default {
  props: {
    value: {
      type: String,
      default: '',
    },
  },

  computed: {
    model: {
      get() {
        return this.value;
      },
      set(value) {
        this.$emit('input', value);
      },
    },
  },
};
</script>

<style lang="scss" scoped>
.select-component {
  background-color: white;
  margin-top: -5px;
}
</style>
