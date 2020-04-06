<template>
  <div>
    <q-item-label header class="text-grey-8">
    Filters
    </q-item-label>
    <div class="tagz">
      <q-btn
        :color="colorMine" label="Mine"
        unelevated rounded no-caps
        @click="toggleMine()">
        <q-tooltip content-class="bg-accent">Your quizzes</q-tooltip>
      </q-btn>
    </div>
    <div class="tagz">
      <q-btn-toggle v-model=selectedQuizzes
        @input="toggleList()"
        no-caps rounded
        unelevated toggle-color="purple"
        color="grey" text-color="white"
        :options="[
        {label: 'All', value: 'all', slot: 'one' },
        {label: 'Fav', value: 'fav', slot: 'two' },
        {label: 'Feed', value: 'follow', slot: 'three' }]"
        >
        <template v-slot:one>
          <q-tooltip content-class="bg-accent">No Filter</q-tooltip>
        </template>

        <template v-slot:two>
          <q-tooltip content-class="bg-accent">Your favorites</q-tooltip>
        </template>

        <template v-slot:three>
          <q-tooltip content-class="bg-accent">Followed authors</q-tooltip>
        </template>
      </q-btn-toggle>
    </div>
    <div class="tagz">
      <q-btn
         color="grey" no-caps         unelevated rounded
        :label="tag | capitalize"
        v-for="(tag, index) in tags"
        :key="index"
        @click="toggleTags(tag)">
        <q-tooltip content-class="bg-accent">Only this tag</q-tooltip>
      </q-btn>
    </div>
    <!-- <div class="tagz">
      <q-btn-dropdown
        v-model=selectedTags no-caps nelevated rounded
        color="grey" label="Tag">
        <q-list>
          <q-item clickable v-close-popup @click="toggleTags">
            <q-item-section v-for="(tag, index) in tags" :key="index">
              <q-item-label
                {{ tag | capitalize }}
              ></q-item-label>
            </q-item-section>
            </q-item>
        </q-list>
      </q-btn-dropdown>
    </div> -->
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import { FETCH_TAGS } from 'src/store/types/actions.type';

export default {
  name: 'DrawerFilters',
  data() {
    return {
      selectedMine: 'none',
      selectedQuizzes: 'all',
      selectedTags: 'none',
      colorMine: 'grey',
    };
  },
  mounted() {
    this.$store.dispatch(FETCH_TAGS);
  },
  computed: {
    ...mapGetters(['isAuthenticated', 'currentUser', 'tags']),
    // tag() {
    //   return this.$route.params.tag;
    // },
  },
  methods: {
    toggleMine() {
      if (!this.isAuthenticated) {
        if (this.$route.name !== 'login') {
          this.$router.push({ name: 'login' });
        }
        return;
      }
      // MINE
      this.colorMine = 'purple';
      this.selectedMine = 'mine';
      this.selectedQuizzes = 'none';
      this.selectedTags = 'none';

      this.$router.push({
        name: 'profile-user',
        params: { username: this.currentUser.username },
      });
    },
    toggleList() {
      if (!this.isAuthenticated) {
        if (this.$route.name !== 'login') {
          this.$router.push({ name: 'login' });
        }
        return;
      }
      // ALL | FAV | FEED
      this.selectedMine = 'none';
      this.selectedTags = 'none';
      this.colorMine = 'grey';
      if (this.selectedQuizzes === 'all') {
        this.$router.push({ name: 'home-all' });
      } else if (this.selectedQuizzes === 'fav') {
        this.selectedQuizzes = 'fav';
        this.$router.push({ name: 'home-favorite' });
      } else if (this.selectedQuizzes === 'follow') {
        this.selectedQuizzes = 'follow';
        this.$router.push({ name: 'home-follow' });
      }
    },
    toggleTags(value) {
      // TAG
      this.selectedMine = 'none';
      this.colorMine = 'grey';
      this.selectedQuizzes = 'none';
      this.selectedTags = value;
      this.$router.push({ name: 'home-tag', params: { tag: this.selectedTags } });
    },
  },
};
</script>

<style scoped>
.tagz{
    /* no underlining */
    font-size: 1.0rem;
    padding-left: 1rem;
    padding-bottom: 1rem;
    margin-right: 0.5rem;
}
</style>
