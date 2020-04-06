/* eslint-disable no-shadow */
import { ApiUserService } from 'src/common/api.service';
import JwtService from 'src/common/api/jwt.service';
import {
  LOGIN,
  LOGOUT,
  REGISTER,
  CHECK_AUTH,
  UPDATE_USER,
} from 'src/store/types/actions.type';
import { SET_AUTH, PURGE_AUTH, SET_ERROR } from '../types/mutations.type';

const state = {
  errors: null,
  user: {},
  isAuthenticated: !!JwtService.getToken(),
};

const getters = {
  currentUser(state) {
    return state.user;
  },
  isAuthenticated(state) {
    return state.isAuthenticated;
  },
};

const actions = {
  // [LOGIN](context, credentials) {
  //   return new Promise((resolve) => {
  //     // ApiService.post("users/login", { user: credentials })
  //     ApiUserService.post('login', { user: credentials })
  //       .then(({ data }) => {
  //         context.commit(SET_AUTH, data.user);
  //         resolve(data);
  //       })
  //       .catch(({ response }) => {
  //         context.commit(SET_ERROR, response.data.errors);
  //       });
  //   });
  // },
  // replace by this since a post gives the request in the resonse
  [LOGIN](context) {
    ApiUserService.get('user')
      .then(({ data }) => {
        context.commit(SET_AUTH, data.user);
      })
      .catch(({ response }) => {
        context.commit(SET_ERROR, response.data.errors);
      });
  },
  [LOGOUT](context) {
    context.commit(PURGE_AUTH);
  },
  // [REGISTER](context, credentials) {
  //   return new Promise((resolve, reject) => {
  //     ApiUserService.post('users', { user: credentials })
  //       .then(({ data }) => {
  //         context.commit(SET_AUTH, data.user);
  //         resolve(data);
  //       })
  //       .catch(({ response }) => {
  //         context.commit(SET_ERROR, response.data.errors);
  //         reject(response);
  //       });
  //   });
  // },
  // replace by this since a put gives the no reponse
  [REGISTER](context) {
    ApiUserService.get('user')
      .then(({ data }) => {
        context.commit(SET_AUTH, data.user);
      })
      .catch(({ response }) => {
        context.commit(SET_ERROR, response.data.errors);
      });
  },
  [CHECK_AUTH](context) {
    if (JwtService.getToken()) {
      ApiUserService.setHeader();
      ApiUserService.get('user')
        .then(({ data }) => {
          context.commit(SET_AUTH, data.user);
        })
        .catch(({ response }) => {
          context.commit(SET_ERROR, response.data.errors);
        });
    } else {
      context.commit(PURGE_AUTH);
    }
  },
  [UPDATE_USER](context, payload) {
    const {
      email, username, password, image, bio, token,
    } = payload;
    const user = {
      username,
      bio,
      image,
      email,
      token,
    };
    if (password) {
      user.password = password;
    }

    // return ApiUserService.put('user', user).then(({ data }) => {
    //   context.commit(SET_AUTH, data.user);
    //   return data;
    // });
    ApiUserService.get('user')
      .then(({ data }) => {
        context.commit(SET_AUTH, data.user);
      })
      .catch(({ response }) => {
        context.commit(SET_ERROR, response.data.errors);
      });
  },
};

const mutations = {
  [SET_ERROR](state, error) {
    state.errors = error;
  },
  [SET_AUTH](state, user) {
    state.isAuthenticated = true;
    state.user = user;
    state.errors = {};
    // token in the api response so disable this for now ?
    JwtService.saveToken(state.user.token);
  },
  [PURGE_AUTH](state) {
    state.isAuthenticated = false;
    state.user = {};
    state.errors = {};
    JwtService.destroyToken();
  },
};

export default {
  state,
  actions,
  mutations,
  getters,
};
