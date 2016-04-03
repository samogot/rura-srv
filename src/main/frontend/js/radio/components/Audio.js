export const Audio = ({isPlaying, volume, source, onAudioLoad}) => (
    isPlaying ? (
        <audio
            ref={(node) => onAudioLoad(node, volume)}
            src={source}
            autoPlay
        />
    ) : (
        <span></span>
    )
);