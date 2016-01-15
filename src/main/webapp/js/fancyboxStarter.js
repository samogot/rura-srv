$(document).ready(function () {
    $(".fancybox")
        .attr('rel', 'gallery')
        .fancybox({
            mouseWheel: false,
            padding: 0,
            margin: 5,
            scrolling: 'no',
            autoResize: false,
            fitToView: true,
            afterLoad: function () {
                var fancy = this;
                this.outer.mousewheel(function (e, a1) {
                    fancy.wrap.css('width', 'auto');
                    var d = Math.pow(1.1, a1);
                    var x = e.pageX - fancy.inner.offset().left;
                    var y = e.pageY - fancy.inner.offset().top;
                    fancy.inner.width(fancy.inner.width() * d);
                    fancy.inner.height(fancy.inner.height() * d);
                    fancy.wrap.css('left', fancy.wrap.position().left + x * (1 - d));
                    fancy.wrap.css('top', fancy.wrap.position().top + y * (1 - d));
                    return false;
                });
                this.wrap.draggable();
            }
        });
});