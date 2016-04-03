/* global ReactRedux */

import {Audio} from "../components/Audio";

const {connect} = ReactRedux;

export const AudioContainer = connect(
    ({isPlaying, volume, source}) => (console.log(source) || {
        isPlaying: isPlaying && source.isFetched,
        source: source.source,
        volume
    }),

    (dispatch) => ({
        onAudioLoad(node, volume) {
            if (!node) {
                return;
            }

            node.volume = volume;
        }
    })
)(Audio);