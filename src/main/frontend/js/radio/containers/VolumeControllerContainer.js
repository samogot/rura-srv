/* global ReactRedux */

import {VolumeController} from '../components/VolumeController';
import {setVolume, storeInput} from '../actions';

const {connect} = ReactRedux;

const applyVolume = (dispatch, value) => {
  dispatch(setVolume(value));
  window.localStorage.setItem('volume', value);
};

export const VolumeControllerContainer = connect(
  ({volume, inputNode}) => ({
    volume,
    inputNode
  }),

  (dispatch) => ({
    onChange(e) {
      applyVolume(dispatch, e.target.value);
    },

    onWheel(node) {
      return (e) => {
        e.preventDefault();

        if (node) {
          node.stepDown(e.deltaY / 10);
          applyVolume(dispatch, node.value);
        }
      };
    },

    onInputLoad(node) {
      dispatch(storeInput(node));
    }
  })
)(VolumeController);