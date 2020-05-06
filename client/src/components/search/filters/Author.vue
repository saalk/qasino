<template>
  <q-input
    class="no-padding no-margin"
    dense filled style="width: 230px"
    type="text" label="Author"
    v-model="model"
    placeholder="3 letters minimal"
    lazy-rules
    :rules="[ val => val && val.length > 2 || '3 letters minimal']"/>
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
  background-color: rgb(110, 27, 27);
  margin-top: -5px;
}
</style>
