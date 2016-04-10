import {TitleContainer} from '../containers/TitleContainer';
import {VolumeControllerContainer} from '../containers/VolumeControllerContainer';
import {PlayButtonContainer} from '../containers/PlayButtonContainer';

export const RadioPlayer = ({isPlaying}) => (
  <div className="radio__player clearfix">
    <TitleContainer />
    <VolumeControllerContainer />
    <PlayButtonContainer isPlaying={isPlaying} />
  </div>
);