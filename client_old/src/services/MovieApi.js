import axios from 'axios'
import Vue from 'vue'

const SERVER_URL = 'http://localhost:9000/qasino/';

const instance = axios.create({
  baseURL: SERVER_URL,
  timeout: 10000
});

export default {

  async execute(method, resource, data, config) {
    let accessToken = await Vue.prototype.$auth.getAccessToken()
    return instance({
      method:method,
      url: resource,
      data,
      headers: {
        Authorization: `Bearer ${accessToken}`
      },
      ...config
    })
  },

  fetchMovieCollection (name) {
    return axios.get('&s=' + name)
      .then(response => {
        return response.data
      })
  },

  fetchSingleMovie (id) {
    return axios.get('&i=' + id)
      .then(response => {
        return response.data
      })
  }

  /*

    // (C)reate
    createNew(text, completed) {
      return this.execute('POST', 'users', {title: text, completed: completed})
    },
    // (R)ead
    getAll() {
      return this.execute('GET','users', null, {
        transformResponse: [function (data) {
          return data? JSON.parse(data)._embedded.users : data;
        }]
      })
    },
    // (U)pdate
    updateForId(id, text, completed) {
    return this.execute('PUT', 'users/' + id, { title: text, completed: completed })
    },

    // (D)elete
    removeForId(id) {
    return this.execute('DELETE', 'users/'+id)
    }

    */
}
