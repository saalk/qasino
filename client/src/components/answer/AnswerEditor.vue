<template>
  <div>
    <ListErrors :errors="errors" />
    <form class="card answer-form" @submit.prevent="onSubmit(answerId, answer)">
      <div class="card-block">
        <textarea
          class="form-control"
          v-model="answer"
          placeholder="Write a answer..."
          rows="3"
        >
        </textarea>
      </div>
      <div class="card-footer">
        <img :src="userImage" class="answer-author-img" />
        <button class="btn btn-sm btn-primary">Post Answer</button>
      </div>
    </form>
  </div>
</template>

<script>
import { ANSWER_CREATE } from 'src/store/types/actions.type.js';
import ListErrors from 'src/components/quiz/view/ListErrors';

export default {
  name: 'AnswerEditor',
  components: { ListErrors },
  props: {
    answerId: { type: String, required: true },
    content: { type: String, required: false },
    userImage: { type: String, required: false },
  },
  data() {
    return {
      answer: this.content || null,
      errors: {},
    };
  },
  methods: {
    onSubmit(answerId, answer) {
      this.$store
        .dispatch(ANSWER_CREATE, { answerId, answer })
        .then(() => {
          this.answer = null;
          this.errors = {};
        })
        .catch(({ response }) => {
          this.errors = response.data.errors;
        });
    },
  },
};
</script>
