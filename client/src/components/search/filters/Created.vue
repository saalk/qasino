<template>
    <q-input v-model="model" mask="date"
      :rules="['date']" label="Creation date"
      dense filled style="width: 230px"
      class="no-padding no-margin"
      >
      <template v-slot:append>
        <q-icon
          name="event"
          class="cursor-pointer">
          <q-popup-proxy ref="qDateProxy" transition-show="scale" transition-hide="scale">
            <q-date v-model="model" @input="() => $refs.qDateProxy.hide()" />
          </q-popup-proxy>
        </q-icon>
      </template>
    </q-input>

</template>

<script>
export default {
  props: {
    value: {
      type: String,
      default: () => new Date().toLocaleDateString('en-CA'),
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
