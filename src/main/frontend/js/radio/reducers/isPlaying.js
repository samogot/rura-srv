import {TOGGLE_PLAYING_STATE} from "../actions";

export const isPlaying = (state = true, action) => {
  switch (action.type) {
    case TOGGLE_PLAYING_STATE: {
      return !state;
    }

    default: {
      return state;
    }
  }
};