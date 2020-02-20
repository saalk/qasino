<template>
  <div v-if="!getIsLoginPage" class="content-header">
    <span id="content-header-title" class="vertical-bottom">{{ routerTitle }}</span>
    <button id="configurations" class="bg-blue-grey-8 text-white">
      <i>settings</i>
      <q-menu ref="popover" anchor="top left" self="top right" class="bg-blue-grey-8 text-white">
        <div class="list highlight">
          <div class="item">
            <div class="item-content has-secondary">
              Layout
            </div>
            <div class="item-secondary">
              <q-toggle
                v-model="getLayoutNeeded"
                class="green"
                @input="setLayoutNeeded(!getLayoutNeeded)"
              />
            </div>
          </div>
          <div class="item">
            <div class="item-content has-secondary">
              Menu Collapse
            </div>
            <div class="item-secondary">
              <q-toggle
                v-model="getMenuCollapse"
                class="green"
                @input="setMenuCollapse(!getMenuCollapse)"
              />
            </div>
          </div>
        </div>
      </q-menu>
    </button>
    <hr>
  </div>
</template>
<script>
import { mapMutations, mapGetters } from 'vuex';

export default {
  computed: {
    ...mapGetters(['getLayoutNeeded', 'getIsLoginPage', 'getMenuCollapse']),
    routerTitle() {
      return this.$route.meta.name;
    },
  },
  methods: {
    ...mapMutations(['setMobileMode', 'setMenuCollapse', 'setLayoutNeeded']),
  },
};
</script>
<style scoped>
.list {
  min-height: 120px;
}
#configurations {
  position: absolute;
  right: 65px;
  top: 7px;
}
.content-header {
  position: relative;
  padding: 1em 4em 0em 4em;
}
@media screen and (max-width: 600px) {
  .content-header {
    padding: 1.5em 0.5em;
  }
  #configurations {
    position: absolute;
    right: 8px;
    top: 12px;
  }
}
#content-header-title {
  font-size: 22px;
  font-weight: 300;
}
</style>
