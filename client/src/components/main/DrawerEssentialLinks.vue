<template>
  <div>
    <q-item-label header class="text-grey-8">
    Essential Links
    </q-item-label>
    <EssentialLink
    v-for="link in essentialLinks.links"
    :key="link.title"
    v-bind="link"
    />
  </div>
</template>

<script>
import EssentialLink from 'src/components/main/EssentialLink';
import links from 'src/common/data/essential.links';

export default {
  name: 'DrawerEssentialLinks',
  components: {
    EssentialLink,
  },
  data() {
    return {
      essentialLinks: links,
    };
  },
};
</script>
