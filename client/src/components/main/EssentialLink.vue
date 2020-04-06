<template>
  <q-item clickable tag="a" target="_blank" :href="url">
    <q-item-section v-if="icon" avatar>
      <q-icon :name="icon" />
    </q-item-section>

    <q-item-section>
      <q-item-label>{{ title }}</q-item-label>
      <q-item-label caption>{{ caption }}</q-item-label>
    </q-item-section>
  </q-item>
</template>

<script>
export default {
  name: 'EssentialLink',
  props: {
    icon: {
      type: String,
      default: '',
    },
    title: {
      type: String,
      required: true,
    },
    caption: {
      type: String,
      default: '',
    },
    url: {
      type: String,
      default: '#',
    },

  },
};
</script>
<style scoped>
.fixed-bottom {
  margin-bottom: 1%;
}

.fixed-bottom a img {
  width: 25px;
  height: 25px;
}
#avatar {
  padding: 20px;
}
#profile {
  height: 120px;
  background-color: #009688;
}
#user-name {
  left: 90px;
  bottom: 77px;
  position: relative;
  width: 159px;
}
#user-actions {
  left: 90px;
  bottom: 71px;
  position: relative;
  width: 171px;
}
#menu-collapse {
  margin-top: 5%;
}
</style>
