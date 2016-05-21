import {AudioContainer} from '../containers/AudioContainer';
import {RadioPlayer} from './RadioPlayer';
import {Links} from './Links';

export const Radio = ({isPlaying, isExtraVisible}) => (
  <div className="radio">
    <AudioContainer />
    <RadioPlayer isPlaying={isPlaying} />
    <Links />
    <img src="//radio.ruranobe.ru" hidden />
  </div>
);