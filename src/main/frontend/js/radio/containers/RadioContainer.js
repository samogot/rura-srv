/* global ReactRedux */

import {Radio} from '../components/Radio';

const {connect} = ReactRedux;

export const RadioContainer = connect(
  ({isPlaying, isExtraVisible}) => ({
    isPlaying,
    isExtraVisible
  })
)(Radio);