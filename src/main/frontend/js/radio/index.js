/* global Redux */
/* global ReactRedux */
/* global ReactDOM */
/* global ReduxThunk */

import {setSource, fetchData} from "./actions";
import {reducers} from "./reducers";
import components from "./components";

const {render} = ReactDOM;
const {createStore, applyMiddleware} = Redux;
const {Provider} = ReactRedux;

const el = document.getElementById('radio-component');

let store;

if (el) {
    const {localStorage} = window;
    const localIsPlaying = localStorage.getItem('isPlaying');
    const localIsExtraVisible = localStorage.getItem('isExtraVisible');
    const localVolume = parseFloat(localStorage.getItem('volume'));

    const initialState = {
        isPlaying: (localIsPlaying === null ? false : localIsPlaying !== 'false'),
        volume: (isNaN(localVolume) ? 1 : localVolume),
        isExtraVisible: (localIsExtraVisible === null ?
            false : localIsExtraVisible !== 'false')
    };

    store = createStore(reducers, initialState, applyMiddleware(ReduxThunk.default));

    $.get('//radio.ruranobe.ru', (data) => store.dispatch(setSource(data)));

    const updater = () => {
        if (store.getState().isPlaying) {
            store.dispatch(fetchData());
        }
    };

    setInterval(updater, 1000);

    render(
        <Provider store={store}>
            <components.Radio />
        </Provider>, el
    );
}