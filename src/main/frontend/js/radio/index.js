/* global Redux */
/* global ReactRedux */
/* global ReactDOM */
/* global ReduxThunk */
/* global $ */

import {setSource, fetchData} from './actions';
import {reducers} from './reducers';
import {RadioContainer} from './containers/RadioContainer';

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

  // console.log(localIsPlaying)

  const initialState = {
    isPlaying: (localIsPlaying === null ? false : localIsPlaying !== 'false'),
    volume: (isNaN(localVolume) ? 1 : localVolume),
    isExtraVisible: (localIsExtraVisible === null ?
                     false : localIsExtraVisible !== 'false')
  };

  store = createStore(reducers, initialState, applyMiddleware(ReduxThunk.default));

  // $.get('//radio.ruranobe.ru',
  //   (data) => console.log(data.substring(5)));

  store.dispatch(setSource("http://s5.myradiostream.com:7234/"));

  const updater = () => {
    if (store.getState().isPlaying) {
      store.dispatch(fetchData());
    }
  };

  setInterval(updater, 3000);

  render(
    <Provider store={store}>
      <RadioContainer />
    </Provider>, el
  );
}