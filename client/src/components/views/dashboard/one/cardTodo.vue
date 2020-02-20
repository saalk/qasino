<template>
  <div class="card bg-white">
    <div class="card-title bg-teal text-white">
      {{ cardTitle }}
      <div class="float-right" />
    </div>
    <div class="card-content">
      <div class="flex">
        <div class="list item-inset-delimiter auto">
          <q-infinite-scroll :handler="loadMore" :offset="7">
            <!-- Content, in this case some <p> tags -->
            <div v-for="(todo, index) in showingData" :key="index" class="item">
              <div class="item-primary">
                {{ index + 1 }}
              </div>
              <div class="item-content has-secondary">
                <span v-show="todo.completed" class="completed-line">{{ todo.title }}</span>
                <input
                  v-show="!todo.completed"
                  v-model.lazy="todo.title"
                  class="fit"
                  @change="changeTitle(todo)"
                >
              </div>
              <q-toggle
                v-model="todo.completed"
                class="item-secondary"
                icon="done"
                @input="completeTodo(todo)"
              />
            </div>
            <div class="row justify-center">
              <spinner slot="message" name="dots" :size="40" />
            </div>
          </q-infinite-scroll>
        </div>
      </div>
    </div>
  </div>
</template>
<script type="text/javascript">
/* eslint-disable */
import Chart from "chart.js";
import Spinner from 'vue-simple-spinner';

export default {
  components: {
    Spinner
  },
  mounted() {
    this.$axios
      .get("https://jsonplaceholder.typicode.com/" + this.api)
      .then(response => {
        this.todos = response.data;
      });
  },
  data() {
    return {
      todos: [],
      actualMaxPosition: 10
    };
  },
  props: ["cardTitle", "api"],
  watch: {},
  computed: {
    showingData() {
      return this.todos.slice(0, this.actualMaxPosition);
    }
  },
  methods: {
    loadMore(index, done) {
      setTimeout(() => {
        this.actualMaxPosition += 9;
        done();
      }, 1000);
    },
    completeTodo(todo) {
      this.$axios
        .patch(
          "https://jsonplaceholder.typicode.com/" + `${this.api}/${todo.id}`,
          { completed: todo.completed }
        )
        .then(response => {
          this.$q.notify.create.positive("Completed todo!");
        });
    },
    changeTitle(todo) {
      this.$axios
        .patch(
          "https://jsonplaceholder.typicode.com/" + `${this.api}/${todo.id}`,
          { title: todo.title }
        )
        .then(response => {
          this.$q.notify.create.positive("Title updated successful!");
        });
    }
  }
};
</script>
<style scoped>
input {
  padding: 7px 0;
  font-size: 16px;
}
.completed-line {
  text-decoration: line-through;
}
</style>
