<template>
  <div>
    <vue-range-slider
      :value="value"
      @input="$emit('input', $event)"
      :min="1"
      :max="99"
      :tooltip-merge="false"
    />
  </div>
</template>

<script>
import VueRangeSlider from 'vue-range-component';
// import 'vue-range-component/dist/vue-range-slider.css';

export default {
  components: {
    VueRangeSlider,
  },
  props: ['value'],
};
</script>

<style lang="scss" scoped>
.select-component {
  background-color: white;
  margin-top: -5px;
}
</style>
