<template>
  <div>
    <div class="flex wrap">
      <div class="auto">
        <tag-knob v-model="currentIncome"
                  :min="0"
                  :max="doubleRangeMinMax.max"
                  title="Current Income"
                  track-color=""
                  title-color="blue"
                  tag-classes="bg-blue"
                  size="120px"
                  placeholder-before="$"
        />
      </div>
      <div class="auto">
        <tag-knob v-model="currentExpenses"
                  :min="0"
                  :max="maxExpenses"
                  title="Current Expenses"
                  track-color=""
                  title-color="red"
                  tag-classes="bg-red"
                  size="120px"
                  placeholder-before="$ -"
        />
      </div>
    </div>
    <div class="flex wrap small-gutter">
      <div class="auto">
        <q-range
          v-model="income"
          :min="minIncome"
          :max="doubleRangeMinMax.max"
          label-always
          snap
        />
      </div>
    </div>
    <div class="flex wrap small-gutter">
      <div class="auto">
        <q-range
          v-model="expenses"
          class="red"
          :min="minExpenses"
          :max="maxExpenses"
          label-always
          snap
        />
      </div>
    </div>
    <div class="flex small-gutter">
      <div class="auto">
        <q-range
          v-model="doubleRangeMinMax"
          class="teal"
          :min="0"
          :max="maxIncome"
          label drag-range
          label-always
        />
      </div>
    </div>
  </div>
</template>
<script type="text/javascript">
/* eslint-disable */
  import tagKnob from './tagKnob.vue'
  export default {
    data () {
      return {
        minExpenses: 0,
        minIncome: 0,
        maxIncome: 5000,
        currentIncome: 998,
        currentExpenses: 125,
        doubleRangeMinMax: {
          min: 0,
          max: 5000
        },
        income: {
          min: 0,
          max: 0
        },
        expenses: {
          min: 0,
          max: 0
        }
      }
    },
    watch: {
      currentIncome () {
        if (this.currentIncome < this.currentExpenses){
          this.currentExpenses = this.currentIncome
          this.$q.notify.create.negative({html: `You're in financial trouble.`})
        }
      }
    },
    computed: {
      maxExpenses () {
        return this.currentIncome
      }

    },
    props: ['cardTitle'],
    components: {
      tagKnob
    }
  }
</script>
<style scoped>
</style>
