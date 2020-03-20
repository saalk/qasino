<template>
  <div class="card">
    <div class="card-block">
      <p class="card-text">{{ test.body }}</p>
    </div>
    <div class="card-footer">
      <a href="" class="test-author">
        <img :src="test.author.image" class="test-author-img" />
      </a>
      <router-link
        class="test-author"
        :to="{ name: 'profile', params: { username: test.author.username } }"
      >
        {{ test.author.username }}
      </router-link>
      <span class="date-posted">{{ test.createdAt | date }}</span>
      <span v-if="isCurrentUser" class="mod-options">
        <i class="ion-trash-a" @click="destroy(id, test.id)"></i>
      </span>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import { TEST_DESTROY } from 'src/store/types/actions.type';

export default {
  name: 'RwvTest',
  props: {
    id: { type: String, required: true },
    test: { type: Object, required: true },
  },
  computed: {
    isCurrentUser() {
      if (this.currentUser.username && this.test.author.username) {
        return this.test.author.username === this.currentUser.username;
      }
      return false;
    },
    ...mapGetters(['currentUser']),
  },
  methods: {
    destroy(id, testId) {
      this.$store.dispatch(TEST_DESTROY, { id, testId });
    },
  },
};
</script>
