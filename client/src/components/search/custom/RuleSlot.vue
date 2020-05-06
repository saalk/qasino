<template>
  <div class="query-builder-child__component">
    <div class="rule-slot">
      <!-- <span class="slot-text">
        {{ ruleName }}
      </span> -->
      <component
        :is="ruleCtrl.ruleComponent"
        :value="ruleCtrl.ruleData"
        @input="ruleCtrl.updateRuleData"
      />
    </div>
  </div>
</template>

<script>
export default {
  props: [
    'rules',
    'ruleCtrl',
  ],
  computed: {
    ruleName() {
      return this.rules.find((r) => r.component === this.ruleCtrl.ruleComponent).name;
    },
  },
};
</script>

<style lang="scss" scoped>
.query-builder-child__component {
  margin: 1px;
  padding: 1px;
  // background-color: hsl(33, 100%, 93%);
}
.rule-slot {
  // background-color: hsl(30, 49%, 75%);
  width: 100%;
}
// .slot-text {
//   margin-right: 8px;
// }
</style>
