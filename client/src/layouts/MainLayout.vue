<template>
  <q-layout view="hhh lpR fFf">

    <q-header reveal elevated class="bg-primary">
      <HeaderToolbar :left="left" @interface="left = $event"/>
    </q-header>

    <q-drawer v-model="left" side="left" overlay behavior="mobile" elevated
              :width="200" :breakpoint="500" bordered content-class="bg-grey-1">
      <DrawerImage></DrawerImage>
      <q-scroll-area
        style="height: calc(100% - 150px); margin-top: 140px; border-right: 1px solid #ddd">
        <q-list padding>
          <DrawerFilters></DrawerFilters>
          <DrawerEssentialLinks></DrawerEssentialLinks>
        </q-list>
      </q-scroll-area>
      <DrawerPoweredBy></DrawerPoweredBy>
    </q-drawer>

    <q-page-container class="no-padding" style="margin-top: 60px">
      <router-view></router-view>
    </q-page-container>

  </q-layout>
</template>

<script>
import HeaderToolbar from 'src/components/main/HeaderToolbar';
import DrawerImage from 'src/components/main/DrawerImage';
import DrawerFilters from 'src/components/main/DrawerFilters';
import DrawerEssentialLinks from 'src/components/main/DrawerEssentialLinks';
import DrawerPoweredBy from 'src/components/main/PoweredBy';

export default {
  name: 'MainLayout',
  components: {
    HeaderToolbar,
    DrawerImage,
    DrawerFilters,
    DrawerEssentialLinks,
    DrawerPoweredBy,
  },
  data() {
    return {
      left: false,
    };
  },
};
</script>
