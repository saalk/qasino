<template>
  <div>
    <q-item-label header class="text-grey-8">
    Preset filters
    </q-item-label>
    <div class="tagz">
      <q-btn
        :color=colorMine label="Mine"
        unelevated rounded no-caps
        @click="toggleMine">
        <q-tooltip class="bg-accent">Your quizzes</q-tooltip>
      </q-btn>
    </div>
    <div class="tagz">
      <q-btn-toggle v-model=selectedQuizzes
        @input="toggleList"
        no-caps rounded
        unelevated :toggle-color=colorQuizzes
        color="grey" text-color="white"
        :options="[
        {label: 'All', value: 'all', slot: 'one' },
        {label: 'Fav', value: 'fav', slot: 'two' },
        {label: 'Feed', value: 'follow', slot: 'three' }]"
        >
        <template v-slot:one>
          <q-tooltip class="bg-accent">No Filter</q-tooltip>
        </template>

        <template v-slot:two>
          <q-tooltip class="bg-accent">Your bookmarks</q-tooltip>
        </template>

        <template v-slot:three>
          <q-tooltip class="bg-accent">Followed authors</q-tooltip>
        </template>
      </q-btn-toggle>
    </div>
    <div class="tagz">
      <q-btn
         :color=toggleTagsColor(tag) no-caps unelevated rounded
        :label="tag | capitalize"
        v-for="(tag, index) in tags"
        :key="index"
        @click="toggleTags(tag)">
        <q-tooltip class="bg-accent">Only this tag</q-tooltip>
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
      selectedMine: '',
      selectedQuizzes: 'all',
      selectedTags: '',

      colorMine: 'grey',
      colorQuizzes: 'purple',
      // colorTags: 'grey',
    };
  },
  mounted() {
    this.$store.dispatch(FETCH_TAGS);
  },
  computed: {
    ...mapGetters(['isAuthenticated', 'currentUser', 'tags']),
  },
  methods: {
    toggleMine() {
    // MINE
      this.colorMine = 'purple';
      this.colorQuizzes = 'grey';
      // this.colorTags = 'grey';

      if (!this.isAuthenticated) {
        if (this.$route.name !== 'login') {
          this.$router.push({ name: 'login' });
        }
        return;
      }

      this.selectedMine = 'mine';
      this.selectedQuizzes = 'none';
      this.selectedTags = 'none';
      if (this.$route.matched.some(({ name }) => name === 'profile-user')) {
        // already on profile-user
        this.colorMine = 'purple';
        this.colorQuizzes = 'grey';
        return;
      }
      this.$router.push({
        name: 'profile-user',
        params: { username: this.currentUser.username },
      });
    },
    toggleList() {
      // ALL | FAV | FEED
      this.colorMine = 'grey';
      this.colorQuizzes = 'purple';
      // this.colorTags = 'grey';

      if (!this.isAuthenticated) {
        if (this.$route.name !== 'login') {
          this.$router.push({ name: 'login' });
        }
        return;
      }

      this.selectedMine = 'none';
      this.selectedTags = 'none';
      if (this.selectedQuizzes === 'all') {
        if (this.$route.matched.some(({ name }) => name === 'home-all')) {
          // already on home-all
          return;
        }
        this.$router.push({ name: 'home-all' });
      } else if (this.selectedQuizzes === 'fav') {
        if (this.$route.matched.some(({ name }) => name === 'home-favorite')) {
          // already on home-favorite
          return;
        }
        this.selectedQuizzes = 'fav';
        this.$router.push({ name: 'home-favorite' });
      } else if (this.selectedQuizzes === 'follow') {
        if (this.$route.matched.some(({ name }) => name === 'home-follow')) {
          // already on home-follow
          return;
        }
        this.selectedQuizzes = 'follow';
        this.$router.push({ name: 'home-follow' });
      }
    },
    toggleTags(value) {
      // TAG
      this.colorMine = 'grey';
      this.colorQuizzes = 'grey';
      // this.colorTags = 'purple';

      this.selectedMine = 'none';
      this.selectedQuizzes = 'none';
      if (this.$route.matched.some(({ name }) => name === 'home-tag')) {
        if (this.selectedTags === value) {
        // already on home-tag
          return;
        }
      }
      this.selectedTags = value;
      this.$router.push({ name: 'home-tag', params: { tag: this.selectedTags } });
    },
    toggleTagsColor(value) {
      // TAG
      if (this.$route.matched.some(({ name }) => name === 'home-tag')) {
        // already on home-tag
        if (this.selectedTags === value) {
          return 'purple';
        }
      }
      if (this.selectedTags === value) {
        return 'purple';
      }
      return 'grey';
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
