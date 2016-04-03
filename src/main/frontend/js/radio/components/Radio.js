import {AudioContainer} from "../containers/AudioContainer";
import {RadioPlayer} from "./RadioPlayer";
import {ExtraAreaContainer} from "../containers/ExtraAreaContainer";

export const Radio = () => (
    <div className="radio">
        <AudioContainer />
        <RadioPlayer />
        <ExtraAreaContainer />
    </div>
);