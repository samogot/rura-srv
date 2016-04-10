/* global Redux */

import {isPlaying} from "./isPlaying";
import {volume} from "./volume";
import {isExtraVisible} from "./isExtraVisible";
import {data} from "./data";
import {inputNode} from "./inputNode";
import {source} from "./source";

const {combineReducers} = Redux;

export const reducers = combineReducers({
    isPlaying,
    volume,
    isExtraVisible,
    data,
    inputNode,
    source
});