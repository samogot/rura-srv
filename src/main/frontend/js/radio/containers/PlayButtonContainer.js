/* global ReactRedux */
/* global store */

import {PlayButton} from "../components/PlayButton";
import {togglePlayingState} from "../actions";

const {connect} = ReactRedux;

export const PlayButtonContainer = connect(
    ({isPlaying}) => ({
        isPlaying
    }),

    (dispatch, ownProps) => ({
        onClick() {
            dispatch(togglePlayingState());
            window.localStorage.setItem('isPlaying', store.getState().isPlaying);
        }
    })
)(PlayButton);