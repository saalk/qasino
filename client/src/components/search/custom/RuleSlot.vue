<template>
  <div class="query-builder-child__component">
    <div class="rule-slot">
      <span class="slot-text">SLOT #rule</span>
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
  props: ['ruleCtrl'],
};
</script>

<style lang="scss" scoped>
.rule-slot {
  background-color: hsl(30, 49%, 75%);
  width: 100%;
}

.slot-text {
  margin-right: 8px;
}
</style>
