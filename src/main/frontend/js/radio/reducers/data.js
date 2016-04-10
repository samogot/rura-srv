import {REQUEST_DATA, RECEIVE_DATA, SET_OFFLINE_STATUS} from "../actions";

export const data = (state = {
    isFetched: false,
    isOffline: true
}, action) => {
    switch (action.type) {
        case REQUEST_DATA:
        {
            return state;
        }

        case RECEIVE_DATA:
        {
            return {
                ...state,
                ...action.data,
                isFetched: true,
                isOffline: false
            };
        }

        case SET_OFFLINE_STATUS:
        {
            return {
                ...state,
                isFetched: true,
                isOffline: true
            };
        }

        default:
        {
            return state;
        }
    }
};