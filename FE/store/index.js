import Vuex from "vuex";
import axios from "axios";

const createStore = () => {
  return new Vuex.Store({
    state: () => ({
      data: []
    }),
    mutations: {
      SET_DATA (state, data) {
        state.data = data
      }
    },
    actions: {
      async fetchData ({ commit }) {
        try {
          const { data } = await axios.get('http://localhost:3000/data.json')
          commit('SET_DATA', data)
        } catch (error) {
          console.error(error)
        }
      }
    }
  })
}

export default createStore