export const Title = ({isPlaying, isOffline, isFetched, artist, title, showname}) => (
  <div className="radio__title">
    {(() =>
      isPlaying ? (
        isFetched ? (
          isOffline ? (
            <i>Сервер недоступен</i>
          ) : (
            <span title={`${artist} - ${title}&#13;${showname}`}>
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