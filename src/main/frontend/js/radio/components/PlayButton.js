export const PlayButton = ({isPlaying, onClick}) => (
  <button className="radio__play-button" onClick={onClick}>
    <div
      className={
        'radio__play-button__icon' +
        (isPlaying ? ' radio__play-button__icon--stop' : '')
      }
    ></div>
  </button>
);