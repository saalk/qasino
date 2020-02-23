<template>
  <div class="card text-white" :class="backgroundColor">
    <div class="card-content ">
      <div class="row">
        <div class="width-1of3">
          <i>{{ iconName }}</i>
        </div>
        <div class="width-2of3">
          <p class="text-italic ">
            {{ title }}
          </p>
          <h5 ref="number" />
        </div>
      </div>
    </div>
  </div>
</template>

<script type="text/javascript">
import { CountUp } from 'countup.js';

export default {
  props: ['title', 'total', 'backgroundColor', 'iconName'],
  data() {
    return {
      options: {
        separator: '.',
      },
    };
  },
  computed: {
    countUp() {
      return new CountUp(this.$refs.number, 0, this.total, 0, 2.5, this.options);
    },
  },
  watch: {
    total() {
      this.countUp.start();
    },
  },
  mounted() {
    this.countUp.start();
  },
};
</script>
<style scoped>
  i {
    font-size: 54px;
  }
</style>
