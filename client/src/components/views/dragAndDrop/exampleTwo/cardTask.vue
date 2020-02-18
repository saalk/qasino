<template>
  <div class="card">
    <div class="card-title" :class="`bg-${cardColor}`">
      <i class="handle cursor-pointer">crop_free</i>
      <input v-model="title" type="text">
      <div class="float-right">
        <i>keyboard_arrow_down</i>
        <q-menu ref="popover">
          <div class="list item-delimiter highlight">
            <div
              v-for="color in cardColorOptions"
              :key="color"
              class="item item-link no-margin"
              :class="`bg-${color}`"
              @click="chooseColor(color)"
            />
          </div>
        </q-menu>
      </div>
    </div>
    <div class="card-content bg-white">
      <input
        v-model="body"
        class="full-width"
        type="text"
      >
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      title: '',
      body: '',
      cardColor: 'blue',
      cardColorOptions: ['orange', 'red', 'blue', 'green'],
    };
  },
  methods: {
    chooseColor(color) {
      this.$refs.popover.close();
      this.cardColor = color;
    },
  },
};
</script>
<style scoped>
  .item-link {
    min-width: 50px;
  }
</style>
