import {ExtraAreaInfo} from './ExtraAreaInfo';
import {ExtraAreaExpander} from './ExtraAreaExpander';

export const ExtraArea = ({isVisible, isEnabled, onExpand, data}) => (
  <div className="radio__extra">
    <ExtraAreaInfo
      isVisible={isVisible}
      {...data}
    />
    <ExtraAreaExpander onClick={onExpand} isEnabled={isEnabled} />
  </div>
);