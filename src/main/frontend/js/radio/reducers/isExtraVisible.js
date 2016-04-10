import {TOGGLE_EXTRA_AREA} from "../actions";

export const isExtraVisible = (state = false, action) => {
    switch (action.type) {
        case TOGGLE_EXTRA_AREA:
        {
            return !state;
        }

        default:
        {
            return state;
        }
    }
};