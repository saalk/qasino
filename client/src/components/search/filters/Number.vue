<script>
export default {
  props: {
    value: {
      type: Number,
      default: 1,
    },
  },

  computed: {
    model: {
      get() {
        return this.value;
      },
      set(value) {
        if (typeof value === 'number') {
          this.$emit('input', value);
        }
      },
    },
  },
};
</script>

<template>
  <input type="number" v-model.number="model" placeholder="dummy number">
</template>

<style lang="scss" scoped>
.select-component {
  background-color: white;
  margin-top: -5px;
}
</style>
