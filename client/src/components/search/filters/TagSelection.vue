<template>
  <q-select
    dense filled style="width: 230px"
    :value="value"
    @input="$emit('input', $event)"
    :reduce="data => data"
    :options="options"
    label="Tag"
    class="select-component"
  />
</template>

<script>
import { FETCH_TAGS } from 'src/store/types/actions.type';
import { mapGetters } from 'vuex';
// import VueSelect from 'vue-select';
// import 'vue-select/dist/vue-select.css';

export default {
  components: {
    // VueSelect,
  },
  props: ['value'],
  mounted() {
    this.$store.dispatch(FETCH_TAGS);
  },
  computed: {
    ...mapGetters(['tags']),
    options() {
      return this.tags;
    },
  },
};
</script>

<style lang="scss" scoped>
// .select-component {
//   background-color: white;
//   margin-top: -5px;
// }
</style>
