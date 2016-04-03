export const VolumeController = ({
    volume, inputNode, onChange, onWheel, onInputLoad
}) => (
    <div
        className="radio__volume-wrapper"
        onWheel={onWheel(inputNode)}
    >
        <div className="radio__volume">
            <input
                ref={onInputLoad}
                type="range"
                defaultValue={volume}
                onChange={onChange}
                min="0.1"
                max="1"
                step="0.01"
            />
            <i className="fa fa-volume-up"/>
        </div>
    </div>
);