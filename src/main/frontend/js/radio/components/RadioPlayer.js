import {TitleContainer} from "../containers/TitleContainer";
import {VolumeControllerContainer} from "../containers/VolumeControllerContainer";
import {PlayButtonContainer} from "../containers/PlayButtonContainer";

export const RadioPlayer = () => (
    <div className="radio__player clearfix">
        <TitleContainer />
        <VolumeControllerContainer />
        <PlayButtonContainer />
    </div>
);