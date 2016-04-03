/* global ReactRedux */

import {VolumeController} from "../components/VolumeController";
import {setVolume, storeInput} from "../actions";

const {connect} = ReactRedux;

export const VolumeControllerContainer = connect(
    ({volume, inputNode}) => ({
        volume,
        inputNode
    }),

    (dispatch, ownProps) => ({
        onChange(e) {
            const volume = e.target.value;
            window.localStorage.setItem('volume', volume);
            dispatch(setVolume(volume));
        },

        onWheel(node) {
            return (e) => {
                e.preventDefault();

                if (node) {
                    node.stepDown(e.deltaY / 10);
                    window.localStorage.setItem('volume', node.value);
                    dispatch(setVolume(node.value));
                }
            };
        },

        onInputLoad(node) {
            dispatch(storeInput(node));
        }
    })
)(VolumeController);