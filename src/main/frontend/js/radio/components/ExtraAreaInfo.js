export const ExtraAreaInfo = ({isVisible, showname, playcount, listeners}) => (
  <ReactCollapse isOpened={isVisible}>
    <div className="radio__extra__info">
      <div className="radio__extra__info__inner">
        <div
          className="radio__extra__info__showname"
          title={showname}
        >
          {showname}
        </div>
        <div className="radio__extra__info__playcount">
          Проигрываний трека: {playcount}
        </div>
        <div className="radio__extra__info__listeners">
          Слушатели: {listeners}
        </div>
        <a href="#">У меня проблема</a>
      </div>
    </div>
  </ReactCollapse>
);