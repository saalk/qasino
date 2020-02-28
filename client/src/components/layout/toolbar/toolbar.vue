<template>
  <div v-show="getLayoutNeeded" class="toolbar">
    <q-ajax-bar color="#80cbc4" />
    <router-link v-if="$router.currentRoute.meta.backButton" :to="'/'">
      <button>
        <i>arrow_back</i>
      </button>
    </router-link>
    <button v-else class="hide-on-drawer-visible" @click="leftDrawer.open()">
      <i>menu</i>
    </button>
    <!-- <q-toolbar-title :padding="1"> -->
    <q-toolbar-title>
      Qasino Admin App
    </q-toolbar-title>
    <div class="right-itens">
      <message-popover />
      <a class="text-white gt-sm inline" @click="setMobileMode(true)">
        <i class="fa fa-3x fa-mobile" />
      </a>
      <a href="https://github.com/saalk/qaino" class="text-white">
        <i class="fa fa-2x fa-github" />
      </a>
    </div>
  </div>
</template>
<script>
import { mapMutations, mapGetters } from 'vuex';
import messagePopover from './messagePopover.vue';

export default {
  components: {
    messagePopover,
  },
  computed: {
    ...mapGetters(['getLayoutNeeded']),
    leftDrawer() {
      return this.$parent.$children[1].$refs.leftDrawer;
    },
  },
  methods: {
    ...mapMutations(['setMobileMode']),
  },
};
</script>
<style scoped>
  .toolbar{
    min-height: 60px;
  }
  .right-itens a, .right-itens button{
    margin-right: 10px;
  }
</style>
