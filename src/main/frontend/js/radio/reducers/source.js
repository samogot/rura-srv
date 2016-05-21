import {SET_SOURCE} from "../actions";


export const source = (state = {
  isFetched: false,
  source: ''
}, action) => {
  switch (action.type) {
    case SET_SOURCE: {
      return {
        isFetched: true,
        source: `${action.source};`
      };
    }

    default: {
      return state;
    }
  }
};