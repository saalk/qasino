<template>
    <div>
        <h1 class="title">Users</h1>
        <h1 class="email">{{userEmail}}</h1>
        <section class="userapp">
            <div v-if="loading">
                <h1 class="loading">Loading...</h1>
            </div>
            <div v-else>
                <header class="header">
                    <input class="new-user"
                           autofocus autocomplete="off"
                           :placeholder="this.inputPlaceholder"
                           v-model="newUser"
                           @keyup.enter="addUser">
                </header>
                <section class="main" v-show="users.length" v-cloak>
                    <input class="toggle-all" type="checkbox" v-model="allDone">
                    <ul class="user-list">
                        <li v-for="user in filteredUsers"
                            class="user"
                            :key="user.userId "
                            :class="{ completed: user.fiches, editing: user == editedUser }">
                            <div class="view">
                                <input class="toggle" type="checkbox" v-model="user.fiches" @change="completeUser(user)">
                                <label @dblclick="editUser(user)">{{ user.alias }}</label>
                                <button class="destroy" @click="removeUser(user)"></button>
                            </div>
                            <input class="edit" type="text"
                                   v-model="user.alias"
                                   v-user-focus="user == editedUser"
                                   @blur="doneEdit(user)"
                                   @keyup.enter="doneEdit(user)"
                                   @keyup.esc="cancelEdit(user)">
                        </li>
                    </ul>
                </section>
                <footer class="footer" v-show="users.length" v-cloak>
          <span class="user-count">
            <strong>{{ remaining }}</strong> {{ remaining | pluralize }} left
          </span>
                    <ul class="filters">
                        <li><a href="#/all" @click="setVisibility('all')" :class="{ selected: visibility == 'all' }">All</a></li>
                        <li><a href="#/active" @click="setVisibility('active')" :class="{ selected: visibility == 'active' }">Active</a></li>
                        <li><a href="#/completed" @click="setVisibility('completed')" :class="{ selected: visibility == 'completed' }">Completed</a></li>
                    </ul>
                    <button class="clear-completed" @click="removeCompleted" v-show="users.length > remaining">
                        Clear completed
                    </button>
                </footer>
            </div>
        </section>
        <div v-if="error" class="error" @click="handleErrorClick">
            ERROR: {{this.error}}
        </div>
    </div>
</template>

<script>

  import api from '../Api';

  // visibility filters
  let filters = {
    all: function (users) {
      return users
    },
    active: function (users) {
      return users.filter(function (user) {
        return !user.fiches
      })
    },
    completed: function (users) {
      return users.filter(function (user) {
        return user.fiches
      })
    }
  }

  // app Vue instance
  const Users = {

    name: 'Users',

    props: {
      activeUser: Object
    },

    // app initial state
    data: function() {
      return {
        users: [],
        newUser: '',
        editedUser: null,
        visibility: 'all',
        loading: true,
        error: null,
      }
    },

    mounted() {
      api.getAll()
        .then(response => {
          this.$log.debug("Data loaded: ", response.data)
          this.users = response.data
        })
        .catch(error => {
          this.$log.debug(error)
          this.error = "Failed to load users"
        })
        .finally(() => this.loading = false)
    },

    // computed properties
    // http://vuejs.org/guide/computed.html
    computed: {
      filteredUsers: function () {
        return filters[this.visibility](this.users)
      },
      remaining: function () {
        return filters.active(this.users).length
      },
      allDone: {
        get: function () {
          return this.remaining === 0
        },
        set: function (value) {
          this.users.forEach(function (user) {
            user.fiches = value
          })
        }
      },
      userEmail: function () {
        return this.activeUser ? this.activeUser.email : ''
      },
      inputPlaceholder: function () {
        return this.activeUser ? this.activeUser.given_name + ', what needs to be done?' : 'What needs to be done?'
      }
    },

    filters: {
      pluralize: function (n) {
        return n === 1 ? 'item' : 'items'
      }
    },

    // methods that implement data logic.
    // note there's no DOM manipulation here at all.

    methods: {

      addUser: function () {
        var value = this.newUser && this.newUser.trim()
        if (!value) {
          return
        }

        api.createNew(value, false).then( (response) => {
          this.$log.debug("New item created:", response);
          this.users.push({
            id: response.data.userId ,
            title: value,
            completed: false
          })
        }).catch((error) => {
          this.$log.debug(error);
          this.error = "Failed to add user"
        });

        this.newUser = ''
      },

      setVisibility: function(vis) {
        this.visibility = vis
      },

      completeUser (user) {
        api.updateForId(user.userId , user.alias, user.fiches).then((response) => {
          this.$log.info("Item updated:", response.data);
        }).catch((error) => {
          this.$log.debug(error)
          user.fiches = !user.fiches
          this.error = "Failed to update user"
        });
      },
      removeUser: function (user) { // notice NOT using "=>" syntax
        api.removeForId(user.userId ).then(() => { // notice AM using "=>" syntax
          this.$log.debug("Item removed:", user);
          this.users.splice(this.users.indexOf(user), 1)
        }).catch((error) => {
          this.$log.debug(error);
          this.error = "Failed to remove user"
        });
      },

      editUser: function (user) {
        this.beforeEditCache = user.alias
        this.editedUser = user
      },

      doneEdit: function (user) {
        if (!this.editedUser) {
          return
        }
        this.$log.info("Item updated:", user);
        api.updateForId(user.userId , user.alias.trim(), user.fiches).then((response) => {
          this.$log.info("Item updated:", response.data);
          this.editedUser = null
          user.alias = user.alias.trim()
        }).catch((error) => {
          this.$log.debug(error)
          this.cancelEdit(user)
          this.error = "Failed to update user"
        });

        if (!user.alias) {
          this.removeUser(user)
        }
      },

      cancelEdit: function (user) {
        this.editedUser = null
        user.alias = this.beforeEditCache
      },

      removeCompleted: function () {
        this.users = filters.active(this.users)
      },

      handleErrorClick: function () {
        this.error = null;
      },

    },

    // a custom directive to wait for the DOM to be updated
    // before focusing on the input field.
    // http://vuejs.org/guide/custom-directive.html
    directives: {
      'user-focus': function (el, binding) {
        if (binding.value) {
          el.focus()
        }
      }
    }
  }

  export default Users

</script>

<style>

    [v-cloak] { display: none; }

</style>
