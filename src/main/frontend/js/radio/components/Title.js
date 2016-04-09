export const Title = ({isPlaying, isOffline, isFetched, artist, title}) => (
  <div className="radio__title">
    {(() =>
      isPlaying ? (
        isFetched ? (
          isOffline ? (
            <i>Сервер недоступен</i>
          ) : (
            <span title={`${artist} - ${title}`}>
              <b>{artist}</b> - {title}
            </span>
          )
        ) : (
          <i>Загрузка...</i>
        )
      ) : (
        <i>Плеер выключен</i>
      )
    )()}
  </div>
);