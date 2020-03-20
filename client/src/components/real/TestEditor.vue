<template>
  <div>
    <RwvListErrors :errors="errors" />
    <form class="card test-form" @submit.prevent="onSubmit(id, test)">
      <div class="card-block">
        <textarea
          class="form-control"
          v-model="test"
          placeholder="Write a test..."
          rows="3"
        >
        </textarea>
      </div>
      <div class="card-footer">
        <img :src="userImage" class="test-author-img" />
        <button class="btn btn-sm btn-primary">Post Test</button>
      </div>
    </form>
  </div>
</template>

<script>
import { TEST_CREATE } from 'src/store/types/actions.type.js';
import RwvListErrors from './ListErrors.vue';

export default {
  name: 'RwvTestEditor',
  components: { RwvListErrors },
  props: {
    id: { type: String, required: true },
    content: { type: String, required: false },
    userImage: { type: String, required: false },
  },
  data() {
    return {
      test: this.content || null,
      errors: {},
    };
  },
  methods: {
    onSubmit(id, test) {
      this.$store
        .dispatch(TEST_CREATE, { id, test })
        .then(() => {
          this.test = null;
          this.errors = {};
        })
        .catch(({ response }) => {
          this.errors = response.data.errors;
        });
    },
  },
};
</script>
