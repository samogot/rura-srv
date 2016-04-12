import {STORE_INPUT} from "../actions";

export const inputNode = (state = {}, action) => {
  switch (action.type) {
    case STORE_INPUT: {
      return action.node;
    }

    default: {
      return state;
    }
  }
};