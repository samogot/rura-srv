export const TOGGLE_PLAYING_STATE = 'TOGGLE_PLAYING_STATE';
export const togglePlayingState = () => {
    return {type: TOGGLE_PLAYING_STATE};
};

export const SET_VOLUME = 'SET_VOLUME';
export const setVolume = (volume) => ({
    type: SET_VOLUME,
    volume
});

export const TOGGLE_EXTRA_AREA = 'TOGGLE_EXTRA_AREA';
export const toggleExtraArea = () => ({
    type: TOGGLE_EXTRA_AREA
});

export const REQUEST_DATA = 'REQUEST_DATA';
export const requestData = () => ({
    type: REQUEST_DATA
});

export const RECEIVE_DATA = 'RECEIVE_DATA';
export const receiveData = ({artist, title, showname, playcount, listeners}) => ({
    type: RECEIVE_DATA,
    data: {
        artist,
        title,
        showname,
        playcount,
        listeners
    }
});

export const SET_OFFLINE_STATUS = 'SET_OFFLINE_STATUS';
export const setOfflineStatus = () => ({
    type: SET_OFFLINE_STATUS
});

export const STORE_INPUT = 'STORE_INPUT';
export const storeInput = (node) => ({
    type: STORE_INPUT,
    node
});

export const SET_SOURCE = 'SET_SOURCE';
export const setSource = (source) => ({
    type: SET_SOURCE,
    source
});

export const fetchData = () => (dispatch) => (
    $.when($.get('/api/radio/nowplaying'))
        .then(
            (data) => dispatch(receiveData(data[data.length - 1])),
            () => dispatch(setOfflineStatus())
        )
);