/* eslint-disable no-shadow */
import { ApiUserService } from 'src/common/api.service';
import {
  FETCH_PROFILE,
  FETCH_PROFILE_FOLLOW,
  FETCH_PROFILE_UNFOLLOW,
} from '../types/actions.type';
import { SET_PROFILE, SET_ERROR } from '../types/mutations.type';

const state = {
  errors: {},
  profile: {},
};

const getters = {
  profile(state) {
    return state.profile;
  },
};

const actions = {
  [FETCH_PROFILE](context, payload) {
    // const { username } = payload;
    // do not send an id, does not work
    let { username } = payload;
    if (username !== null) { username = ''; }
    // return ApiUserService.get('profiles', username)
    //   .then(({ data }) => {
    //     context.commit(SET_PROFILE, data.profile);
    //     return data;
    //   })
    //   .catch(() => {
    //     // #todo SET_ERROR cannot work in multiple states
    //     // context.commit(SET_ERROR, response.data.errors)
    //   });
    ApiUserService.get('profile')
      .then(({ data }) => {
        context.commit(SET_PROFILE, data.profile);
      })
      .catch(({ response }) => {
        context.commit(SET_ERROR, response.data.errors);
      });
  },
  [FETCH_PROFILE_FOLLOW](context, payload) {
    // const { username } = payload;
    // do not send an id, does not work
    let { username } = payload;
    if (username !== null) { username = ''; }
    // return ApiUserService.post(`profiles/${username}/follow`)
    return ApiUserService.post('profile')
      .then(({ data }) => {
        context.commit(SET_PROFILE, data.profile);
        return data;
      })
      .catch(() => {
        // #todo SET_ERROR cannot work in multiple states
        // context.commit(SET_ERROR, response.data.errors)
      });
  },
  [FETCH_PROFILE_UNFOLLOW](context, payload) {
    // const { username } = payload;
    // do not send an id, does not work
    let { username } = payload;
    if (username !== null) { username = ''; }
    // return ApiUserService.delete(`profiles/${username}/follow`)
    return ApiUserService.delete('profile')
      .then(({ data }) => {
        context.commit(SET_PROFILE, data.profile);
        return data;
      })
      .catch(() => {
        // #todo SET_ERROR cannot work in multiple states
        // context.commit(SET_ERROR, response.data.errors)
      });
  },
};

const mutations = {
  // [SET_ERROR] (state, error) {
  //   state.errors = error
  // },
  [SET_PROFILE](state, profile) {
    state.profile = profile;
    state.errors = {};
  },
};

export default {
  state,
  actions,
  mutations,
  getters,
};
