<template>
  <div>
    <div class="layout-padding">
      <div class="card">
        <div class="card-title bg-teal text-white">
          Example using pagination / filter outside of data tables (Tks to
          <a href="https://github.com/wilcorrea" class="text-black underline">Willian Correa</a> )
        </div>
        <div class="card-content">
          <div class="flex wrap gutter">
            <div class="auto">
              <q-search v-model.lazy="searchBeer" placeholder="Search for beer name"
                        :debounce="500" @input="getBeers"
              />
            </div>
            <!-- <div class="auto">
              <q-pagination
                v-model="page"
                :max="10"
              ></q-pagination>
            </div> -->
          </div>
          <q-table title="Beer List"
                   :data="beers"
                   :columns="columns"
                   row-key="name"
                   virtual-scroll
                   :pagination.sync="pagination"
                   :rows-per-page-options="[0]"
          />
          <!-- <template slot="col-image_url" slot-scope="cell">
              <tooltip-button :url="cell.row.image_url"></tooltip-button>
            </template> -->
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// import tooltipButton from './tooltipButton.vue'
export default {
  data() {
    return {
      beers: [],
      // page: 1,
      pagination: {
        rowsPerPage: 0,
      },
      searchBeer: '',
      columns: [
        {
          name: 'name', required: true, label: 'Name', field: 'name', width: '80px', sortable: true,
        },
        {
          name: 'description', label: 'Description', field: 'description', width: '150px', sortable: true,
        },
        {
          name: 'first', label: 'First Brewed', field: 'first_brewed', width: '50px', sortable: true,
        },
        {
          name: 'picture', label: 'Picture', field: 'image_url', width: '50px',
        },
      ],
      // configs: {
      //   columnPicker: true,
      //   title: 'Beer List'
      // }
    };
  },
  computed: {
    url() {
      return `beers?page=${this.page}&per_page=10${this.search}`;
    },
    search() {
      return this.searchBeer ? `&beer_name=${this.searchBeer}` : '';
    },
  },
  watch: {
    page() {
      this.getBeers();
    },
  },
  mounted() {
    this.getBeers();
  },
  methods: {
    getBeers() {
      this.$http.punk
        .get(this.url)
        .then(response => { this.beers = response.data; });
    },
  },
  // ,
  // components: {
  //   tooltipButton
  // }
};
</script>

<style scoped>
  .grid-filter{
    max-width: 200px
  }
  .grid-search{
    max-width: calc(100% - 400px)
  }
  .grid-pagination{
    max-width: 200px
  }
</style>
