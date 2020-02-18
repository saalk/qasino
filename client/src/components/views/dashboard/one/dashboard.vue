<template>
  <div class="layout-padding">
    <div class="flex wrap gutter">
      <div class="width-1of3 sm-auto">
        <cardTotal
          title="Total Posts"
          background-color="bg-teal-9"
          icon-name="local_post_office"
          :total="totalPosts"
        />
      </div>
      <div class="width-1of3 sm-auto">
        <cardTotal
          title="Total comments"
          background-color="bg-teal-7"
          icon-name="comment"
          :total="totalComments"
        />
      </div>
      <div class="width-1of3 sm-auto">
        <cardTotal
          title="Static total"
          background-color="bg-teal-5"
          icon-name="repeat_one"
          :total="50004"
        />
      </div>
    </div>
    <div class="flex wrap gutter">
      <div class="width-1of2 lg-width-1of3 sm-auto">
        <card-chart card-title="Total Graph" :data="dataForGraph" />
      </div>
      <div class="auto">
        <knob-statistics card-title="General statistics" />
      </div>
    </div>
    <div class="flex wrap gutter">
      <div class="width-4of5 sm-width-1of1">
        <card-todo card-title="Generic todos" api="todos" />
      </div>
    </div>
  </div>
</template>
<script>
import { mapMutations } from 'vuex';
import cardChart from './cardChart.vue';
import cardTotal from './cardTotal.vue';
import cardTodo from './cardTodo.vue';
import knobStatistics from './knobStatistics.vue';

export default {
  name: 'Home',
  components: {
    cardTotal,
    cardChart,
    cardTodo,
    knobStatistics,
  },
  data() {
    return {
      totalPosts: 0,
      totalComments: 0,
      totalTodos: 0,
    };
  },
  computed: {
    dataForGraph() {
      return {
        posts: this.totalPosts,
        todos: this.totalTodos,
        comments: this.totalComments,
      };
    },
  },
  mounted() {
    // Axios.all not working
    Promise.all([
      this.$axios.get('https://jsonplaceholder.typicode.com/posts'),
      this.$axios.get('https://jsonplaceholder.typicode.com/comments'),
      this.$axios.get('https://jsonplaceholder.typicode.com/todos'),
    ]).then(response => {
      this.setPosts(response[0].data);
      this.totalPosts = response[0].data.length;
      this.totalComments = response[1].data.length;
      this.totalTodos = response[2].data.length;
    });
  },
  methods: {
    ...mapMutations(['setPosts']),
  },
};
</script>
<style></style>
