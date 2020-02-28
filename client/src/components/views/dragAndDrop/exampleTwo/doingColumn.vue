<template>
  <div class="card">
    <div class="card-title bg-blue-grey-2">
      <h5>
        Doing
        <button
          class="primary bg-green float-right cursor-pointer"
          @click="addCardTask()"
        >
          <i>add</i>
        </button>
      </h5>
    </div>
    <div ref="doing" class="card-content bg-blue-grey-1">
      <card-task v-for="(task, index) in doingTasks" :key="index" :class="index" />
    </div>
  </div>
</template>

<script>
import cardTask from './cardTask.vue';

export default {
  components: {
    cardTask,
  },
  data() {
    return {
      doingTasks: [],
    };
  },
  methods: {
    addCardTask() {
      this.doingTasks.push({});
    },
  },
};
</script>

<style scoped>
  .bg-blue-grey-1{
    min-height: 500px;
  }
</style>
