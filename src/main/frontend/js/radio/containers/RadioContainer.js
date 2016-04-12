/* global ReactRedux */

import {Radio} from '../components/Radio';

const {connect} = ReactRedux;

export const RadioContainer = connect(
  ({isPlaying}) => ({
    isPlaying
  })
)(Radio);