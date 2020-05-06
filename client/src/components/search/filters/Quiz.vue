<template>
  <q-input
  class="no-padding no-margin"
  dense filled style="width: 230px"
  type="number" label="Quiz"
  v-model.number="model"
  placeholder="default is 1"
  lazy-rules
  :rules="[ typeof value !== 'number' || 'digits only']"/>
</template>

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

<style lang="scss" scoped>
.select-component {
  background-color: white;
  margin-top: -5px;
}
</style>
