export const PlayButton = ({isPlaying, onClick}) => (
  <button className="radio__play-button">
    <div
      className={
        'radio__play-button__icon' +
        (isPlaying ? ' radio__play-button__icon--stop' : '')
      }
      onClick={onClick}
    ></div>
  </button>
);