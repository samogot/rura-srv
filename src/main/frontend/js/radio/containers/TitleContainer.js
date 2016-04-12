/* global ReactRedux */

import {Title} from '../components/Title';

const {connect} = ReactRedux;

export const TitleContainer = connect(
  ({isPlaying, data: {isOffline, isFetched, artist, title, showname}}) => ({
    isPlaying,
    isOffline,
    isFetched,
    artist,
    title,
    showname
  })
)(Title);