import https from "https";
import Vuex from "vuex";
import axios from "axios";

const axiosInstance = axios.create({
  httpsAgent: new https.Agent({
    rejectUnauthorized: false
  })
});

const createStore = () => {
  return new Vuex.Store({
    state: () => ({
      testData: []
    }),
    mutations: {
      SET_TESTDATA (state, testData) {
        state.testData = testData
      }
    },
    actions: {
      async fetchData ({ commit }) {
        try {
          const { data } = await axiosInstance.get('https://127.0.0.1:8443/api/Gateway/DataProducts');
          commit('SET_TESTDATA', data)
        } catch (error) {
          console.error(error)
        }
      }
    }
  })
}

export default createStore
