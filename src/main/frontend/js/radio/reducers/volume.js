import {SET_VOLUME} from "../actions";

export const volume = (state = 1, action) => {
  switch (action.type) {
    case SET_VOLUME: {
      return action.volume;
    }

    default: {
      return state;
    }
  }
};