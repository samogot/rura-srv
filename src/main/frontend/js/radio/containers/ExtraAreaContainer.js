/* global ReactRedux */
/* global store */

import {ExtraArea} from "../components/ExtraArea";
import {toggleExtraArea} from "../actions";

const {connect} = ReactRedux;

export const ExtraAreaContainer = connect(
    ({isExtraVisible, isPlaying, data}, ownProps) => ({
        isVisible: isPlaying && data.isFetched && !data.isOffline && isExtraVisible,
        isEnabled: isPlaying && data.isFetched && !data.isOffline,
        data
    }),

    (dispatch, ownProps) => ({
        onExpand() {
            dispatch(toggleExtraArea());
            window.localStorage.setItem('isExtraVisible', store.getState().isExtraVisible);
        }
    })
)(ExtraArea);