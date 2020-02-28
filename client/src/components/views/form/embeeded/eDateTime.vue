<template>
  <div>
    <div class="floating-label" :class="labelColor">
      <q-datetime
        type="datetime"
        class="full-width"
        :class="borderColor"
        :label="label"
        :value="value"
        :format="format"
        @input="change($event)"
      />
      <transition-group name="slide-fade">
        <!-- used index+0 to bypass do not use v-for index as key warning which is
        for iterating over is an Array but this is Object-->
        <span
          v-for="(key, index) in messageKeys"
          v-show="!validation[key] && validation.$dirty"
          :key="index+0"
          class="label text-red"
        >{{ validationMessages[key] }}</span>
      </transition-group>
      <transition name="fade" mode="out-in">
        <i v-show="validAndDirty" class="text-green">check</i>
      </transition>
      <transition name="fade" mode="out-in">
        <i v-show="invalidAndDirty" class="text-red">clear</i>
      </transition>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    format: {
      type: String,
      default: '',
    },
    validation: {
      type: Object,
      required: true,
    },
    value: {
      type: String,
      default: '',
      required: true,
    },
    validationMessages: {},
    label: {},
  },
  computed: {
    messageKeys() {
      return Object.keys(this.validationMessages);
    },
    validAndDirty() {
      return !this.validation.$invalid && this.validation.$dirty;
    },
    invalidAndDirty() {
      return this.validation.$invalid && this.validation.$dirty;
    },
    labelColor() {
      if (this.validAndDirty) {
        return 'label-success';
      }
      if (this.invalidAndDirty) {
        return 'label-error animate-pop';
      }
      return '';
    },

    borderColor() {
      if (this.validAndDirty) {
        return 'has-success';
      }
      if (this.invalidAndDirty) {
        return 'has-error';
      }
      return '';
    },
    shakeDiv() {
      return this.invalidAndDirty ? 'animate-pop' : '';
    },
  },
  methods: {
    change(value) {
      // console.log(value);
      this.validation.$touch();
      this.$emit('input', value);
    },
  },
};
</script>
<style scoped>
.has-success {
  border-bottom: 2px solid #4caf50 !important;
}
i {
  position: absolute;
  top: 20px;
  right: 7px;
  font-size: 22px;
}
.floating-label {
  min-height: 81px;
}
.floating-label span .label {
  padding: 0 0.3rem 0 0;
  font-size: 13px;
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter,
.fade-leave-to,
.fade-leave-active {
  opacity: 0;
}
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.7s ease;
}
.slide-fade-enter {
  transform: translateY(-10px);
  opacity: 0;
}
.slide-fade-leave-active {
  transform: translateY(-7px);
  opacity: 0;
}
</style>
