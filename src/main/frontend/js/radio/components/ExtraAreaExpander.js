export const ExtraAreaExpander = ({onClick, isEnabled}) => (
    <button
        className={
      'radio__extra__expander' +
      (isEnabled ? '' : ' radio__extra__expander--disabled')
    }
        onClick={onClick}
    >
        <span />
        <span />
        <span />
    </button>
);