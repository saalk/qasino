<template>
  <div class="query-builder-group-slot__rule-selection">
      <q-select
        :value="groupCtrl.rules.identifier"
        v-model="selectedRule"
        option-value="identifier"
        option-label="name"
        filled map-options
        input-debounce="0"
        style="width: 300px"
        :options="groupCtrl.rules"
        label="Where" options-dense bottom-slots
        clickable emit-value dense>

        <template v-slot:after>
          <q-btn
            flat round
            :disabled="selectedRule === ''"
            @click="groupCtrl.addRule(selectedRule)"
            icon="las la-plus">
          </q-btn>
          <!-- <q-icon name="cancel"
          :v-if="selectedRule !== ''"
          @click.stop="selectedRule = ''"
          class="cursor-pointer"/> -->
          <q-btn
            v-if=false
            flat round
            @click="groupCtrl.newGroup"
            icon="las la-plus-square">
          </q-btn>
        </template>
      </q-select>
    </div>
</template>

<script>
export default {
  props: [
    'groupCtrl',
  ],
  data() {
    return {
      selectedRule: '',
    };
  },
};
</script>

<style lang="scss" scoped>
.q-field--with-bottom {
    padding-bottom: 2px;
}
.margin-of-ten {
  margin-bottom: 10px;
}

.query-builder-group-slot__group-control {
  // background-color: hsl(0, 100%, 93%);
  // padding: 16px;
  display: flex;
  flex-direction: row;
}

.query-builder-group-slot__rule-selection,
.query-builder-group-slot__rule-adding-button {
  margin-left: 16px;
}

.query-builder-group-slot__spacer {
  // width: 0;
  // margin-left: 12px;
  // margin-right: 12px;
  border-left: 1px solid hsl(0, 0%, 75%);
}
</style>
