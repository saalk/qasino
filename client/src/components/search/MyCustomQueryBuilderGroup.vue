<template>
  <div>
    <div class="margin-of-ten">
      <q-btn
        class="actions text-h7 text-orange-9"
        :icon="expanded ? 'keyboard_arrow_up' : 'keyboard_arrow_down'"
        @click="expanded = !expanded"
        no-caps dense outline color="white"
        :label="expanded ? 'Search' : 'Filter'"
      >
      </q-btn>
      <q-btn
        class="actions text-h7 btn-fixed-width"
        no-caps dense color="white" text-color="grey"
        :label=quasarQuery
      >
      </q-btn>
      <!-- {{ query }} -->
    </div>
    <q-slide-transition>
      <QueryBuilder :config="config" v-model="query" v-show="expanded">
        <!-- groupOperator Slot -->
        <template #groupOperator="props"
          class="query-builder-group-slot__group-selection">
          <q-select class="margin-of-ten"
            :value="props.currentOperator"
            @click="props.updateCurrentOperator($event.target.value)"
            v-model="query.operatorIdentifier"
            option-value="identifier"
            option-label="name" options-dense
            filled map-options
            input-debounce="0"
            :options="props.operators"
            label="Select match type"
            style="width: 150px"
            clickable emit-value dense
          ></q-select>
        </template>
        <!-- groupControl Slot -->
        <template #groupControl="props">
          <GroupCtrlSlot :group-ctrl="props"/>
        </template>
        <!-- ruleControl Slot -->
        <template #rule="props"
          class="query-builder-rule">
          <RuleSlot :rules="config.rules" :ruleCtrl="props"/>
        </template>
      </QueryBuilder>
    </q-slide-transition>
  </div>
</template>

<script>
// https://rtucek.github.io/vue-query-builder/getting-started.html#installation
import QueryBuilder from 'query-builder-vue';

// import FlatPickr from 'vue-flatpickr-component';
import TagSelection from './filters/TagSelection.vue';
import Questions from './filters/Questions.vue';
import Author from './filters/Author.vue';
import Quiz from './filters/Quiz.vue';
import Created from './filters/Created.vue';
import Bookmarks from './filters/Bookmarks.vue';
import Following from './filters/Following.vue';

// https://rtucek.github.io/vue-query-builder/demos.html#theming
// import 'flatpickr/dist/flatpickr.css';
import GroupCtrlSlot from './custom/GroupCtrlSlot';
import RuleSlot from './custom/RuleSlot.vue';

export default {
  components: {
    QueryBuilder,
    GroupCtrlSlot,
    RuleSlot,
  },
  props: {
    presetQuery: Object,
  },
  watch: {
    presetQuery() {
      this.query = this.presetQuery;
    },
  },
  mounted() {
    this.query = this.presetQuery;
  },
  data() {
    return {
      expanded: false,
      dragging: true,
      colorNo: 5,
      query: {
        operatorIdentifier: 'AND',
        children: [],
      },
    };
  },
  computed: {
    colors() {
      return [
        'hsl(88, 50%, 55%)',
        'hsl(187, 100%, 45%)',
        'hsl(15, 100%, 55%)',
        'hsl(273, 40%, 61%)',
        'hsl(60, 51%, 47%)',
      ].slice(0, this.colorNo);
    },
    config() {
      return {
        operators: [
          { name: 'All', identifier: 'AND' },
          { name: 'Any', identifier: 'OR' },
          { name: 'All but not', identifier: 'BUT NOT' },
        ],
        rules: [
          {
            identifier: 'tag',
            name: 'Tag',
            component: TagSelection,
            initialValue: '',
          },
          {
            identifier: 'author',
            name: 'Author',
            component: Author,
            initialValue: '',
          },
          {
            identifier: 'quiz',
            name: 'Quiz',
            component: Quiz,
            initialValue: 1,
          },
          {
            identifier: 'created',
            name: 'Created',
            component: Created,
            initialValue: () => new Date().toLocaleDateString('en-CA'),
          },
          {
            identifier: 'between',
            name: 'Questions',
            component: Questions,
            initialValue: () => [1, 99],
          },
          {
            identifier: 'bookmarks',
            name: 'Bookmarks',
            component: Bookmarks,
            initialValue: 'true',
          },
          {
            identifier: 'following',
            name: 'Following',
            component: Following,
            initialValue: 'true',
          },
        ],
        colors: this.colors,
        dragging: {
          animation: 300,
          disabled: !this.dragging,
          dragClass: 'sortable-drag',
        },
      };
    },
    quasarOperators() {
      return this.config.operators.map((item) => {
        let id = {};
        id = item.identifier;
        return id;
      });
    },
    quasarQuery() {
      // const operator = this.query.operatorIdentifier.toLowerCase();
      let filter = '';
      let operator = '';
      if (this.query.children.length > 0) {
        for (let i = 0; i < this.query.children.length; i += 1) {
          operator = this.query.children[i].operatorIdentifier;
          if (i > 0) {
            filter += this.query.operatorIdentifier;
          } else {
            filter += 'where (';
          }
          if (typeof (operator) !== 'undefined' && operator != null) {
            if (this.query.children[i].children.length > 0) {
              for (let j = 0; j < this.query.children[i].children.length; j += 1) {
                if (j > 0) {
                  filter += this.query.children[i].operatorIdentifier;
                } else {
                  filter += ' where (';
                }
                filter += ' ';
                filter += this.query.children[i].children[j].identifier;
                filter += " is '";
                filter += this.query.children[i].children[j].value;
                filter += " '";
              }
            }
          } else {
            filter += ' ';
            filter += this.query.children[i].identifier;
            filter += " is '";
            filter += this.query.children[i].value;
            filter += "' ";
          }
        }
        filter += ')';
      } else {
        filter = 'None';
      }
      return filter;
    },
  },
};
</script>

<style>
.margin-of-ten {
  margin-bottom: 10px;
}
.query-builder-group-slot__group-selection {
  padding: 6px;
  margin-top: 10px;
  background-color: hsl(33, 100%, 93%);
}
.query-builder-rule[data-v-2d9be62f] {
    position: relative;
    background-color: #f2f2f2;
    padding: 6px;
    padding-left: 16px;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-orient: horizontal;
    -webkit-box-direction: normal;
    -ms-flex-direction: row;
    flex-direction: row;
}
</style>
