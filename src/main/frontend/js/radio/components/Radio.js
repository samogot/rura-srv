import {AudioContainer} from '../containers/AudioContainer';
import {RadioPlayer} from './RadioPlayer';
import {ExtraAreaContainer} from '../containers/ExtraAreaContainer';

export const Radio = ({isPlaying, isExtraVisible}) => (
  <div className="radio">
    <AudioContainer />
    <RadioPlayer isPlaying={isPlaying} />
    <ExtraAreaContainer isExtraVisible={isExtraVisible} />
    <a
      className="radio__trouble"
      href="//vk.com/topic-43340456_33272955"
      target="_blank"
    >
      У меня проблема
    </a>
    <img src="//radio.ruranobe.ru" className="radio__mpegurl-loader" />
  </div>
);