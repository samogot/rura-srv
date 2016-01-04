var people = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.whitespace,
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    prefetch: '/api/people'
});

// passing in `null` for the `options` arguments will result in the default
// options being used
$('.people-name-input.typeahead').typeahead({
    hint: true,
    highlight: true,
    minLength: 1
}, {
    name: 'people',
    source: people
});