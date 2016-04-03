/* global ReactRedux */

import {Title} from "../components/Title";

const {connect} = ReactRedux;

export const TitleContainer = connect(
    ({isPlaying, data: {isOffline, isFetched, artist, title}}) => ({
        isPlaying,
        isOffline,
        isFetched,
        artist,
        title
    })
)(Title);