export const ExtraAreaInfo = ({isVisible, showname}) => (
  <ReactCollapse isOpened={isVisible}>
    <div className="radio__extra__info">
      <div className="radio__extra__info__inner">
        <div
          className="radio__extra__info__showname"
          title={showname}
        >
          {showname}
        </div>
        <a href="//vk.com/topic-43340456_33272953" target="_blank">
          Новости радио
        </a>
      </div>
    </div>
  </ReactCollapse>
);