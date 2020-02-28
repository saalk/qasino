<template>
  <div class="card">
    <div class="card-title bg-teal text-white">
      Example One - Drap Fruits to the cart
    </div>
    <div class="card-content bg-white">
      <div class="flex wrap">
        <div id="left" class="card shadow-3 no-margin">
          <button class="relative-position no-padding" value="5">
            <img src="./img/apple.svg" class="icon-size" alt>
            <span class="floating label bg-primary text-white">$5</span>
          </button>
          <button class="relative-position no-padding" value="3">
            <img src="./img/pear.svg" class="icon-size" alt>
            <span class="floating label bg-primary text-white">$3</span>
          </button>
          <button class="relative-position no-padding" value="2">
            <img src="./img/orange.svg" class="icon-size" alt>
            <span class="floating label bg-primary text-white">$2</span>
          </button>
          <button class="relative-position no-padding" value="4">
            <img src="./img/watermelon.svg" class="icon-size" alt>
            <span class="floating label bg-primary text-white">$4</span>
          </button>
          <button class="relative-position no-padding" value="7">
            <img src="./img/strawberry.svg" class="icon-size" alt>
            <span class="floating label bg-primary text-white">$7</span>
          </button>
        </div>
        <div id="cart" ref="cart" class="text-center" :style="move">
          <h5>
            <span id="span-price" class="label bg-amber text-white">
              <span class="left-detail">Total</span> $
              <span ref="number" />
            </span>
          </h5>
          <div id="right" />
        </div>
        <div v-show="totalCost > 0" class="auto self-end">
          <button class="green raised" @click="pay()">
            Pay
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Dragula from 'dragula/dragula';
import { CountUp } from 'countup.js';

export default {
  data() {
    return {
      dragula: '',
      totalCost: 0,
      options: {
        separator: '.',
      },
      move: '',
    };
  },
  watch: {
    totalCost(newValue, oldValue) {
      /* eslint-disable no-new */
      const countUp = new CountUp(
        this.$refs.number,
        oldValue,
        newValue,
        0,
        1.5,
        this.options,
      );
      countUp.start();
    },
  },
  mounted() {
    const vm = this;
    this.dragula = Dragula([
      document.querySelector('#left'),
      document.querySelector('#right'),
    ]).on('drop', (el, container, source) => {
      if (source.id === container.id) {
        return;
      }
      if (source.id === 'left') {
        vm.totalCost += parseInt(el.value, 10);
      } else {
        vm.totalCost -= parseInt(el.value, 10);
      }
    });
  },
  methods: {
    pay() {
      const vm = this;
      this.move = { animation: 'cartOut 2s' };
      setTimeout(() => {
        vm.move = '';
      }, 2100);
    },
  },
};
</script>

<style scoped>
.card {
  margin-right: 2%;
}
#cart {
  background: url('img/cart.svg') white no-repeat;
  background-size: 300px 300px;
  width: 300px;
  height: 320px;
}
#span-price {
  margin-top: 16px;
  margin-left: 166px;
  width: 132px;
}
#left {
  padding: 15px;
  width: 100px;
}
#right {
  margin-left: 87px;
  margin-top: 20px;
  width: 194px;
  height: 111px;
}
.icon-size {
  width: 56px;
  height: 57px;
}
</style>
