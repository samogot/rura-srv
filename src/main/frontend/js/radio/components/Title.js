export const Title = ({isPlaying, isOffline, isFetched, artist, title, showname}) => (
  <div className="radio__title">
    {(() =>
      isPlaying ? (
        isFetched ? (
          isOffline ? (
            <i>Сервер недоступен</i>
          ) : (
            <span title={`${showname}\n${artist} - ${title}`}>
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